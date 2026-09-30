package com.mosersystems.zefix;

import com.mosersystems.zefix.model.BfsCommunity;
import com.mosersystems.zefix.model.CompanyFull;
import com.mosersystems.zefix.model.CompanySearchQuery;
import com.mosersystems.zefix.model.CompanyShort;
import com.mosersystems.zefix.model.ErrorDetails;
import com.mosersystems.zefix.model.LegalForm;
import com.mosersystems.zefix.model.RegistryOfCommerce;
import com.mosersystems.zefix.model.RestApiErrorResponse;
import com.mosersystems.zefix.model.SogcPublicationAndCompanyShort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.List;

/**
 * Client for the Zefix PublicREST API, the swiss central business name index.
 * <p>
 * All methods throw {@link ZefixApiException} if the API responds with an error status, e.g. 404 if
 * nothing is found.
 *
 * @see <a href="https://www.zefix.admin.ch/ZefixPublicREST/swagger-ui/index.html">API documentation</a>
 */
@HttpExchange(url = "/api/v1", accept = "application/json")
public interface ZefixClient {

    /**
     * Searches companies registered in the commercial register.
     *
     * @param query search query
     * @return matching companies
     */
    @PostExchange(url = "/company/search", contentType = "application/json")
    List<CompanyShort> searchCompanies(@RequestBody CompanySearchQuery query);

    /**
     * Gets detailed company info by UID.
     *
     * @param uid UID, e.g. {@code CHE107721785}
     * @return matching companies
     */
    @GetExchange("/company/uid/{id}")
    List<CompanyFull> getCompanyByUid(@PathVariable("id") String uid);

    /**
     * Gets detailed company info by EHRA-ID, the internal company ID of the federal registry of commerce.
     *
     * @param ehraid EHRA-ID, e.g. {@code 348639}
     * @return the company
     */
    @GetExchange("/company/ehraid/{id}")
    CompanyFull getCompanyByEhraid(@PathVariable("id") long ehraid);

    /**
     * Gets detailed company info by CH-number, which is no longer used in public.
     *
     * @param chid CH-number, e.g. {@code CH40030163378}
     * @return matching companies
     */
    @GetExchange("/company/chid/{id}")
    List<CompanyFull> getCompanyByChid(@PathVariable("id") String chid);

    /**
     * Gets a SOGC publication by its publication number (SOGC-ID).
     *
     * @param sogcId SOGC-ID
     * @return the publication and the company it concerns
     */
    @GetExchange("/sogc/{id}")
    SogcPublicationAndCompanyShort getSogcPublication(@PathVariable("id") long sogcId);

    /**
     * Gets all SOGC publications of a date.
     *
     * @param date publication date
     * @return the publications and the companies they concern
     */
    @GetExchange("/sogc/bydate/{date}")
    List<SogcPublicationAndCompanyShort> getSogcPublicationsByDate(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    /**
     * Gets all cantonal registries of commerce.
     *
     * @return the registries of commerce
     */
    @GetExchange("/registryOfCommerce")
    List<RegistryOfCommerce> getRegistriesOfCommerce();

    /**
     * Gets the cantonal registry of commerce responsible for a political commune.
     *
     * @param bfsId number of the commune according to the swiss official commune register, e.g. {@code 623}
     * @return the registry of commerce
     */
    @GetExchange("/registryOfCommerce/byBfsCommunityId/{id}")
    RegistryOfCommerce getRegistryOfCommerceByBfsCommunityId(@PathVariable("id") String bfsId);

    /**
     * Gets all legal forms and their codes.
     *
     * @return the legal forms
     */
    @GetExchange("/legalForm")
    List<LegalForm> getLegalForms();

    /**
     * Gets all political communes according to the swiss official commune register.
     *
     * @return the communes
     */
    @GetExchange("/community")
    List<BfsCommunity> getCommunities();

    /**
     * Creates a client. The builder is cloned, so it is not modified.
     *
     * @param properties        base URL and credentials
     * @param restClientBuilder builder for the underlying {@link RestClient}
     * @return the client
     */
    static ZefixClient create(ZefixProperties properties, RestClient.Builder restClientBuilder) {
        RestClient.Builder builder = restClientBuilder.clone()
                .baseUrl(properties.baseUrl())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    throw new ZefixApiException(response.getStatusCode(), readErrorDetails(response.getBody().readAllBytes()));
                });
        if (properties.username() != null && !properties.username().isBlank()) {
            builder.defaultHeaders(headers -> headers.setBasicAuth(properties.username(),
                    properties.password() != null ? properties.password() : ""));
        }
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build()))
                .build()
                .createClient(ZefixClient.class);
    }

    private static ErrorDetails readErrorDetails(byte[] body) {
        try {
            RestApiErrorResponse response = JsonMapper.shared().readValue(body, RestApiErrorResponse.class);
            return response != null ? response.error() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
