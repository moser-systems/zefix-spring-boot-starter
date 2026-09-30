package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Internal type of a SOGC mutation.
 *
 * @param id  mutation type ID
 * @param key mutation type key
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MutationType(Integer id, String key) {
}
