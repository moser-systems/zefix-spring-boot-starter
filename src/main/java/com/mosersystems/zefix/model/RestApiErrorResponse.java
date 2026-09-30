package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Generic error response body of the Zefix API.
 *
 * @param error error details
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RestApiErrorResponse(ErrorDetails error) {
}
