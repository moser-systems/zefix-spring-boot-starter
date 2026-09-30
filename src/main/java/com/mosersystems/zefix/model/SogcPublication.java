package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;

/**
 * Commercial register publication in the SOGC (Swiss Official Gazette of Commerce).
 *
 * @param sogcDate                      publication date in the SOGC
 * @param sogcId                        publication number in the SOGC (SOGC-ID)
 * @param registryOfCommerceId          internal office number of the publishing cantonal registry of commerce
 * @param registryOfCommerceCanton      canton of the publishing registry of commerce
 * @param registryOfCommerceJournalId   number of the daily register of the publishing registry of commerce
 * @param registryOfCommerceJournalDate date of the daily register of the publishing registry of commerce
 * @param message                       formatted text of the publication
 * @param mutationTypes                 mutation types
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SogcPublication(
        LocalDate sogcDate,
        Long sogcId,
        Long registryOfCommerceId,
        String registryOfCommerceCanton,
        Long registryOfCommerceJournalId,
        LocalDate registryOfCommerceJournalDate,
        String message,
        List<MutationType> mutationTypes
) {
}
