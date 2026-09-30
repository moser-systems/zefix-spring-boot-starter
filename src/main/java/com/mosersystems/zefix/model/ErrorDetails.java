package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Details of an error reported by the Zefix API.
 *
 * @param type    type of error
 * @param message details about the error
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorDetails(ErrorType type, String message) {
}
