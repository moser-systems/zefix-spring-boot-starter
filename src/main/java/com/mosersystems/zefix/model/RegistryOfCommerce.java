package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A cantonal registry of commerce.
 *
 * @param registryOfCommerceId internal office number of the cantonal registry of commerce
 * @param canton               2 character abbreviation of the canton
 * @param address1             name of the cantonal registry of commerce
 * @param address2             street and house number
 * @param address3             post office box
 * @param address4             swiss zip code and locality
 * @param homepage             official homepage of the cantonal registry of commerce
 * @param url2                 URL pattern for excerpts of active companies (UID formatted as {@code CHE-NNN.NNN.NNN})
 * @param url3                 contact email of the registry of commerce
 * @param url4                 URL pattern for excerpts of deleted companies (UID as {@code CHE-NNN.NNN.NNN},
 *                             deletion publication date as {@code YYYYMMDD})
 * @param url5                 currently not in use
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RegistryOfCommerce(
        Long registryOfCommerceId,
        String canton,
        String address1,
        String address2,
        String address3,
        String address4,
        String homepage,
        String url2,
        String url3,
        String url4,
        String url5
) {
}
