# Zefix Spring Boot Starter

Spring Boot auto-configuration for a typed client of [Zefix PublicREST](https://www.zefix.admin.ch/ZefixPublicREST/swagger-ui/index.html),
the REST API of the Swiss central business name index (commercial register).

Requires Java 25 and Spring Boot 4.

## Installation

```xml
<dependency>
    <groupId>com.moser-systems</groupId>
    <artifactId>zefix-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Configuration

The API requires HTTP basic authentication. Credentials can be requested from the Federal Registry
of Commerce (zefix@bj.admin.ch).

```yaml
zefix:
  enabled: true                 # default true; set false to disable the client bean
  username: ${ZEFIX_USERNAME}
  password: ${ZEFIX_PASSWORD}
  # base-url: https://www.zefix.admin.ch/ZefixPublicREST        (default, production)
  # base-url: https://www.zefixintg.admin.ch/ZefixPublicREST    (integration environment)
```

If the application provides a `RestClient.Builder` bean (e.g. via `spring-boot-starter-restclient`),
it is used as the base for the client, so timeouts, proxies and observability configured there apply.

## Usage

Inject `ZefixClient`:

```java
List<CompanyShort> companies = zefixClient.searchCompanies(
        CompanySearchQuery.byName("Qube*").withCanton("BE").withActiveOnly(true));

CompanyFull company = zefixClient.getCompanyByUid("CHE107721785").getFirst();
```

| Method                                          | Endpoint                                               |
|-------------------------------------------------|--------------------------------------------------------|
| `searchCompanies(CompanySearchQuery)`           | `POST /api/v1/company/search`                          |
| `getCompanyByUid(String)`                       | `GET /api/v1/company/uid/{id}`                         |
| `getCompanyByEhraid(long)`                      | `GET /api/v1/company/ehraid/{id}`                      |
| `getCompanyByChid(String)`                      | `GET /api/v1/company/chid/{id}`                        |
| `getSogcPublication(long)`                      | `GET /api/v1/sogc/{id}`                                |
| `getSogcPublicationsByDate(LocalDate)`          | `GET /api/v1/sogc/bydate/{date}`                       |
| `getRegistriesOfCommerce()`                     | `GET /api/v1/registryOfCommerce`                       |
| `getRegistryOfCommerceByBfsCommunityId(String)` | `GET /api/v1/registryOfCommerce/byBfsCommunityId/{id}` |
| `getLegalForms()`                               | `GET /api/v1/legalForm`                                |
| `getCommunities()`                              | `GET /api/v1/community`                                |

If the API responds with an error status, the client throws `ZefixApiException`. It exposes the HTTP
status and the parsed error details, e.g. `ErrorType.NOT_FOUND` or `ErrorType.RESULTLIST_TO_LARGE`:

```java
try {
    return zefixClient.getCompanyByUid(uid);
} catch (ZefixApiException e) {
    if (e.getErrorType() == ErrorType.NOT_FOUND) {
        return List.of();
    }
    throw e;
}
```

Without Spring auto-configuration, create a client with
`ZefixClient.create(new ZefixProperties(true, ZefixProperties.DEFAULT_BASE_URL, user, password), RestClient.builder())`.

To customize, define your own `ZefixClient` bean; the auto-configured one backs off.

Data source: Zefix, Federal Registry of Commerce (FRC). The data is published under the
[OGD terms of use](https://opendata.swiss/en/terms-of-use#terms_by) and requires attribution of the source.

## Releasing

Releases are published to Maven Central by the `Release` GitHub workflow when a `v*` tag is pushed:

```
git tag v0.1.0 && git push origin v0.1.0
```

## License

Apache License 2.0
