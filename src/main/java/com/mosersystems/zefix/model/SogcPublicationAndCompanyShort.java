package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A SOGC publication together with the company it concerns.
 *
 * @param sogcPublication the publication
 * @param companyShort    the company
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SogcPublicationAndCompanyShort(SogcPublication sogcPublication, CompanyShort companyShort) {
}
