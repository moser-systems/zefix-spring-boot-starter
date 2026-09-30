package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

/**
 * Basic company information.
 *
 * @param name                 primary business name of the company
 * @param ehraid               internal company unique ID used by the federal registry of commerce
 * @param uid                  UID number, {@code CHE...}
 * @param chid                 CH-ID (old CH-number, no longer used in public)
 * @param legalSeatId          legal seat ID (commune number according to the swiss official commune register)
 * @param legalSeat            legal seat name (name of the political commune)
 * @param registryOfCommerceId internal office number of the cantonal registry of commerce
 * @param legalForm            legal form
 * @param status               current company status
 * @param sogcDate             date of the last publication in the SOGC
 * @param deletionDate         date of deletion of the legal unit
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyShort(
        String name,
        Long ehraid,
        String uid,
        String chid,
        Long legalSeatId,
        String legalSeat,
        Long registryOfCommerceId,
        LegalForm legalForm,
        CompanyStatus status,
        LocalDate sogcDate,
        LocalDate deletionDate
) {
}
