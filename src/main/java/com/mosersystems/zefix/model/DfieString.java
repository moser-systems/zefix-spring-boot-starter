package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Text translated into the Swiss national languages and English.
 *
 * @param de german translation
 * @param fr french translation
 * @param it italian translation
 * @param en english translation
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DfieString(String de, String fr, String it, String en) {
}
