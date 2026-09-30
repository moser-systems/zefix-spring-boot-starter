package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Legal form of a company, e.g. AG or GmbH.
 *
 * @param id        internal legal form ID used by the commercial register
 * @param uid       public legal form code according to the data standard eCH-0097
 * @param name      full name
 * @param shortName abbreviation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LegalForm(Long id, String uid, DfieString name, DfieString shortName) {
}
