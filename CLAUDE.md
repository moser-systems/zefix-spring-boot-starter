# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Spring Boot starter (library, not an app) providing a typed client for the Zefix PublicREST API
(Swiss central business name index, OpenAPI spec: https://www.zefix.admin.ch/ZefixPublicREST/v3/api-docs).
Targets Java 25 and Spring Boot 4 (Jackson 3, i.e. `tools.jackson.*` packages, not `com.fasterxml.jackson.*`;
annotations stay in `com.fasterxml.jackson.annotation`). Maven coordinates: `com.moser-systems:zefix-spring-boot-starter`.

## Commands

```sh
mvn -B verify                                        # what CI runs: compile, test, source + javadoc jars
mvn test                                             # tests only
mvn test -Dtest=ZefixClientTest                      # single test class
mvn test -Dtest=ZefixClientTest#testSearchCompanies  # single test method
ZEFIX_USERNAME=... ZEFIX_PASSWORD=... mvn test -Dtest=ZefixLiveIT   # smoke test against the real API
```

The javadoc jar is built in the default lifecycle (not only on release), so public API without proper javadoc can break `mvn verify`.

Releases: pushing a `v*` tag triggers `.github/workflows/release.yml`, which sets the version from the tag and runs `mvn -P release deploy` (GPG signing + Maven Central). The `pom.xml` version stays at `-SNAPSHOT`.

## Architecture

- `ZefixClient` is a Spring HTTP interface (`@HttpExchange`), one method per API operation, proxied via `HttpServiceProxyFactory` + `RestClientAdapter`. The model is hand-written, not generated from the OpenAPI spec.
- `ZefixClient.create(ZefixProperties, RestClient.Builder)` clones the builder, sets the base URL, basic auth (only if a username is set) and a status handler that turns 4xx/5xx into `ZefixApiException` (with the parsed `RestApiErrorResponse` details, or `null` if the body is not parsable).
- `ZefixProperties` is an immutable record bound to `zefix.*` (`enabled`, `base-url`, `username`, `password`). Defaults are declared via `@DefaultValue`.
- `autoconfigure/ZefixAutoConfiguration` (registered in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`) creates the `ZefixClient` bean. It uses an application-provided `RestClient.Builder` if present, otherwise `RestClient.builder()`, and backs off when `zefix.enabled=false` or the application defines its own `ZefixClient`.
- `model/` holds one record per API schema. Records use `@JsonIgnoreProperties(ignoreUnknown = true)`; enums (`CompanyStatus`, `ErrorType`) map unknown values to `UNKNOWN` via a `@JsonCreator`, so API additions don't break deserialization.
- API quirks mirrored as-is: `/company/uid/{id}` and `/company/chid/{id}` return arrays, `/company/ehraid/{id}` returns a single object; the `byBfsCommunityId` path variable is a string.

Adding an endpoint means adding a method to `ZefixClient`, any new model records, a test in `ZefixClientTest`, and a row in the README table.

## Testing conventions

- Client tests bind `MockRestServiceServer` to a `RestClient.Builder` and pass it to `ZefixClient.create`. They make no real HTTP calls.
- Auto-configuration tests use `ApplicationContextRunner` with `AutoConfigurations.of(ZefixAutoConfiguration.class)` and property values.
- `ZefixLiveIT` hits the real API and only runs when `ZEFIX_USERNAME` is set (skipped in CI).
