package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Postal address of a company.
 *
 * @param organisation organisation name
 * @param careOf       care of
 * @param street       street
 * @param houseNumber  house number
 * @param addon        address addon
 * @param poBox        PO box
 * @param city         city
 * @param swissZipCode zip code
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Address(
        String organisation,
        String careOf,
        String street,
        String houseNumber,
        String addon,
        String poBox,
        String city,
        String swissZipCode
) {
}
