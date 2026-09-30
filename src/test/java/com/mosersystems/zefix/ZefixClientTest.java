package com.mosersystems.zefix;

import com.mosersystems.zefix.model.BfsCommunity;
import com.mosersystems.zefix.model.CompanyFull;
import com.mosersystems.zefix.model.CompanySearchQuery;
import com.mosersystems.zefix.model.CompanyShort;
import com.mosersystems.zefix.model.CompanyStatus;
import com.mosersystems.zefix.model.ErrorType;
import com.mosersystems.zefix.model.LegalForm;
import com.mosersystems.zefix.model.RegistryOfCommerce;
import com.mosersystems.zefix.model.SogcPublicationAndCompanyShort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("ZefixClient Tests")
class ZefixClientTest {

    private static final String BASE_URL = "https://zefix.example.com/ZefixPublicREST";
    private static final String API = BASE_URL + "/api/v1";

    private static final String COMPANY_SHORT = """
            {"name":"Qube AG","ehraid":348639,"uid":"CHE107721785","chid":"CH40030163378",
             "legalSeatId":623,"legalSeat":"Rubigen","registryOfCommerceId":36,
             "legalForm":{"id":3,"uid":"0106","name":{"de":"Aktiengesellschaft","fr":"Société anonyme"},
                          "shortName":{"de":"AG","fr":"SA"}},
             "status":"ACTIVE","sogcDate":"2020-01-10","deletionDate":null}
            """;

    private MockRestServiceServer server;

    private ZefixClient client(String username, String password) {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        return ZefixClient.create(new ZefixProperties(true, BASE_URL, username, password), builder);
    }

    private ZefixClient client() {
        return client("user", "secret");
    }

    @AfterEach
    void verifyServer() {
        server.verify();
    }

    @Test
    @DisplayName("Should POST search query as JSON with basic auth")
    void testSearchCompanies() {
        ZefixClient client = client();
        server.expect(requestTo(API + "/company/search"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpzZWNyZXQ="))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"name\":\"Qube\",\"canton\":\"BE\",\"activeOnly\":true}", true))
                .andRespond(withSuccess("[" + COMPANY_SHORT + "]", MediaType.APPLICATION_JSON));

        List<CompanyShort> result = client.searchCompanies(
                CompanySearchQuery.byName("Qube").withCanton("BE").withActiveOnly(true));

        assertEquals(1, result.size());
        CompanyShort company = result.getFirst();
        assertEquals("Qube AG", company.name());
        assertEquals(348639L, company.ehraid());
        assertEquals(CompanyStatus.ACTIVE, company.status());
        assertEquals(LocalDate.of(2020, 1, 10), company.sogcDate());
        assertNull(company.deletionDate());
        assertEquals("AG", company.legalForm().shortName().de());
    }

    @Test
    @DisplayName("Should not send authorization header without username")
    void testNoCredentials() {
        ZefixClient client = client(null, null);
        server.expect(requestTo(API + "/legalForm"))
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertTrue(client.getLegalForms().isEmpty());
    }

    @Test
    @DisplayName("Should get company details by UID")
    void testGetCompanyByUid() {
        ZefixClient client = client();
        server.expect(requestTo(API + "/company/uid/CHE107721785"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"name":"Qube AG","uid":"CHE107721785","status":"BEING_CANCELLED","purpose":"Beratung",
                          "address":{"street":"Bahnhofstrasse","houseNumber":"1","swissZipCode":"3113","city":"Rubigen"},
                          "sogcPub":[{"sogcDate":"2020-01-10","sogcId":1004793000,"message":"Neueintragung",
                                      "mutationTypes":[{"id":1,"key":"status.neu"}]}],
                          "branchOffices":[%s],
                          "oldNames":[{"name":"Qube GmbH","sequenceNr":1}],
                          "zefixDetailWeb":{"de":"https://www.zefix.ch/de/search/entity/list/firm/348639"},
                          "someNewField":"ignored"}]
                        """.formatted(COMPANY_SHORT), MediaType.APPLICATION_JSON));

        List<CompanyFull> result = client.getCompanyByUid("CHE107721785");

        CompanyFull company = result.getFirst();
        assertEquals(CompanyStatus.BEING_CANCELLED, company.status());
        assertEquals("Rubigen", company.address().city());
        assertEquals(1004793000L, company.sogcPub().getFirst().sogcId());
        assertEquals("status.neu", company.sogcPub().getFirst().mutationTypes().getFirst().key());
        assertEquals("Qube AG", company.branchOffices().getFirst().name());
        assertEquals("Qube GmbH", company.oldNames().getFirst().name());
        assertNotNull(company.zefixDetailWeb().de());
    }

    @Test
    @DisplayName("Should map unknown enum values to UNKNOWN")
    void testUnknownStatus() {
        ZefixClient client = client();
        server.expect(requestTo(API + "/company/ehraid/348639"))
                .andRespond(withSuccess("{\"name\":\"Qube AG\",\"status\":\"SOMETHING_NEW\"}", MediaType.APPLICATION_JSON));

        assertEquals(CompanyStatus.UNKNOWN, client.getCompanyByEhraid(348639).status());
    }

    @Test
    @DisplayName("Should get company details by CH-ID")
    void testGetCompanyByChid() {
        ZefixClient client = client();
        server.expect(requestTo(API + "/company/chid/CH40030163378"))
                .andRespond(withSuccess("[{\"chid\":\"CH40030163378\"}]", MediaType.APPLICATION_JSON));

        assertEquals("CH40030163378", client.getCompanyByChid("CH40030163378").getFirst().chid());
    }

    @Test
    @DisplayName("Should get SOGC publications by ID and date")
    void testSogc() {
        ZefixClient client = client();
        String publication = """
                {"sogcPublication":{"sogcDate":"2020-01-10","sogcId":1004793000,"registryOfCommerceCanton":"BE"},
                 "companyShort":%s}
                """.formatted(COMPANY_SHORT);
        server.expect(requestTo(API + "/sogc/1004793000"))
                .andRespond(withSuccess(publication, MediaType.APPLICATION_JSON));
        server.expect(requestTo(API + "/sogc/bydate/2020-01-10"))
                .andRespond(withSuccess("[" + publication + "]", MediaType.APPLICATION_JSON));

        SogcPublicationAndCompanyShort byId = client.getSogcPublication(1004793000L);
        assertEquals("BE", byId.sogcPublication().registryOfCommerceCanton());
        assertEquals("Qube AG", byId.companyShort().name());

        List<SogcPublicationAndCompanyShort> byDate = client.getSogcPublicationsByDate(LocalDate.of(2020, 1, 10));
        assertEquals(1, byDate.size());
    }

    @Test
    @DisplayName("Should get registries of commerce")
    void testRegistryOfCommerce() {
        ZefixClient client = client();
        String registry = "{\"registryOfCommerceId\":36,\"canton\":\"BE\",\"address1\":\"Handelsregisteramt\"}";
        server.expect(requestTo(API + "/registryOfCommerce"))
                .andRespond(withSuccess("[" + registry + "]", MediaType.APPLICATION_JSON));
        server.expect(requestTo(API + "/registryOfCommerce/byBfsCommunityId/623"))
                .andRespond(withSuccess(registry, MediaType.APPLICATION_JSON));

        List<RegistryOfCommerce> all = client.getRegistriesOfCommerce();
        assertEquals(36L, all.getFirst().registryOfCommerceId());
        assertEquals("BE", client.getRegistryOfCommerceByBfsCommunityId("623").canton());
    }

    @Test
    @DisplayName("Should get legal forms and communities")
    void testLegalFormsAndCommunities() {
        ZefixClient client = client();
        server.expect(requestTo(API + "/legalForm"))
                .andRespond(withSuccess("[{\"id\":3,\"uid\":\"0106\",\"name\":{\"de\":\"Aktiengesellschaft\"}}]",
                        MediaType.APPLICATION_JSON));
        server.expect(requestTo(API + "/community"))
                .andRespond(withSuccess("[{\"bfsId\":623,\"canton\":\"BE\",\"name\":\"Rubigen\",\"registryOfCommerceId\":36}]",
                        MediaType.APPLICATION_JSON));

        LegalForm legalForm = client.getLegalForms().getFirst();
        assertEquals("0106", legalForm.uid());
        assertEquals("Aktiengesellschaft", legalForm.name().de());

        BfsCommunity community = client.getCommunities().getFirst();
        assertEquals(623L, community.bfsId());
        assertEquals("Rubigen", community.name());
    }

    @Test
    @DisplayName("Should throw ZefixApiException with error details on 404")
    void testNotFound() {
        ZefixClient client = client();
        server.expect(requestTo(API + "/company/uid/CHE000000000"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":{\"type\":\"NOT_FOUND\",\"message\":\"no company found\"}}"));

        ZefixApiException exception = assertThrows(ZefixApiException.class,
                () -> client.getCompanyByUid("CHE000000000"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals(ErrorType.NOT_FOUND, exception.getErrorType());
        assertEquals("no company found", exception.getErrorDetails().message());
        assertTrue(exception.getMessage().contains("no company found"));
    }

    @Test
    @DisplayName("Should throw ZefixApiException on 400 with too many results")
    void testBadRequest() {
        ZefixClient client = client();
        server.expect(requestTo(API + "/company/search"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"error\":{\"type\":\"RESULTLIST_TO_LARGE\",\"message\":\"too many results\"}}"));

        ZefixApiException exception = assertThrows(ZefixApiException.class,
                () -> client.searchCompanies(CompanySearchQuery.byName("AG*")));

        assertEquals(ErrorType.RESULTLIST_TO_LARGE, exception.getErrorType());
    }

    @Test
    @DisplayName("Should throw ZefixApiException without details on unparsable error body")
    void testServerError() {
        ZefixClient client = client();
        server.expect(requestTo(API + "/legalForm")).andRespond(withServerError().body("<html>oops</html>"));

        ZefixApiException exception = assertThrows(ZefixApiException.class, client::getLegalForms);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertNull(exception.getErrorDetails());
    }
}
