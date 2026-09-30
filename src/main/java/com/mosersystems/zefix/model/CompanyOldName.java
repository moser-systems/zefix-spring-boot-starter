package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Previous name of a company.
 *
 * @param name        primary name
 * @param sequenceNr  hint about the age of the entry; a bigger number is older
 * @param translation translations
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyOldName(String name, Long sequenceNr, List<String> translation) {
}
