package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Company search query. Only {@code name} is required; use {@link #byName(String)} and the
 * {@code with...} methods to add filters.
 * <p>
 * {@code registryOfCommerceId}, {@code legalSeatId} and {@code canton} are mutually exclusive.
 *
 * @param name                 begin of the company name (at least 3 characters), {@code *} can be used as wildcard
 * @param legalFormId          internal legal form ID, see {@link LegalForm#id()}
 * @param legalFormUid         public legal form code according to eCH-0097, see {@link LegalForm#uid()}
 * @param registryOfCommerceId internal office number of the cantonal registry of commerce
 * @param legalSeatId          number of the political commune of the legal seat
 * @param canton               2 character abbreviation of the canton, e.g. {@code BE}
 * @param activeOnly           whether to return active companies only
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompanySearchQuery(
        String name,
        Long legalFormId,
        String legalFormUid,
        Long registryOfCommerceId,
        Long legalSeatId,
        String canton,
        Boolean activeOnly
) {

    /**
     * Creates a query without filters.
     *
     * @param name begin of the company name, {@code *} can be used as wildcard
     * @return the query
     */
    public static CompanySearchQuery byName(String name) {
        return new CompanySearchQuery(name, null, null, null, null, null, null);
    }

    /**
     * @param legalFormId internal legal form ID
     * @return a copy of this query filtered by legal form ID
     */
    public CompanySearchQuery withLegalFormId(Long legalFormId) {
        return new CompanySearchQuery(name, legalFormId, legalFormUid, registryOfCommerceId, legalSeatId, canton, activeOnly);
    }

    /**
     * @param legalFormUid public legal form code according to eCH-0097
     * @return a copy of this query filtered by legal form code
     */
    public CompanySearchQuery withLegalFormUid(String legalFormUid) {
        return new CompanySearchQuery(name, legalFormId, legalFormUid, registryOfCommerceId, legalSeatId, canton, activeOnly);
    }

    /**
     * @param registryOfCommerceId internal office number of the cantonal registry of commerce
     * @return a copy of this query filtered by registry of commerce
     */
    public CompanySearchQuery withRegistryOfCommerceId(Long registryOfCommerceId) {
        return new CompanySearchQuery(name, legalFormId, legalFormUid, registryOfCommerceId, legalSeatId, canton, activeOnly);
    }

    /**
     * @param legalSeatId number of the political commune of the legal seat
     * @return a copy of this query filtered by legal seat
     */
    public CompanySearchQuery withLegalSeatId(Long legalSeatId) {
        return new CompanySearchQuery(name, legalFormId, legalFormUid, registryOfCommerceId, legalSeatId, canton, activeOnly);
    }

    /**
     * @param canton 2 character abbreviation of the canton
     * @return a copy of this query filtered by canton
     */
    public CompanySearchQuery withCanton(String canton) {
        return new CompanySearchQuery(name, legalFormId, legalFormUid, registryOfCommerceId, legalSeatId, canton, activeOnly);
    }

    /**
     * @param activeOnly whether to return active companies only
     * @return a copy of this query with the active filter set
     */
    public CompanySearchQuery withActiveOnly(Boolean activeOnly) {
        return new CompanySearchQuery(name, legalFormId, legalFormUid, registryOfCommerceId, legalSeatId, canton, activeOnly);
    }
}
