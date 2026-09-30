package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;

/**
 * Detailed company information, an extended version of {@link CompanyShort}.
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
 * @param translation          company name translations
 * @param purpose              purpose
 * @param sogcPub              SOGC publications regarding the registry of commerce
 * @param address              address
 * @param canton               2 character abbreviation of the canton
 * @param capitalNominal       nominal capital (only available for corporations)
 * @param capitalCurrency      currency of the nominal capital
 * @param headOffices          head offices
 * @param furtherHeadOffices   further head offices
 * @param branchOffices        branch offices
 * @param hasTakenOver         companies this company has taken over
 * @param wasTakenOverBy       companies this company was taken over by
 * @param auditCompanies       audit companies
 * @param oldNames             previous names of the company
 * @param cantonalExcerptWeb   link to the excerpt of the cantonal commercial register
 * @param zefixDetailWeb       links to the detail view in Zefix, per language
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyFull(
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
        LocalDate deletionDate,
        List<String> translation,
        String purpose,
        List<SogcPublication> sogcPub,
        Address address,
        String canton,
        String capitalNominal,
        String capitalCurrency,
        List<CompanyShort> headOffices,
        List<CompanyShort> furtherHeadOffices,
        List<CompanyShort> branchOffices,
        List<CompanyShort> hasTakenOver,
        List<CompanyShort> wasTakenOverBy,
        List<CompanyShort> auditCompanies,
        List<CompanyOldName> oldNames,
        String cantonalExcerptWeb,
        DfieString zefixDetailWeb
) {
}
