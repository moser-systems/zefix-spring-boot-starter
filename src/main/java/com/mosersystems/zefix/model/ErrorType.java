package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Type of an error reported by the Zefix API.
 */
public enum ErrorType {
    /** Internal server error. */
    INTERNAL_SERVER_ERROR,
    /** The query words are invalid, e.g. too short. */
    INVALID_QUERY_WORDS,
    /** The request data is invalid. */
    INVALID_REQUEST_DATA,
    /** The query matches too many results; narrow it down. */
    RESULTLIST_TO_LARGE,
    /** Nothing found. */
    NOT_FOUND,
    /** A value not known to this client version. */
    UNKNOWN;

    /**
     * Maps an API value to a constant, falling back to {@link #UNKNOWN} for unknown values.
     *
     * @param value API value
     * @return the matching constant, or {@code null} if {@code value} is {@code null}
     */
    @JsonCreator
    public static ErrorType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ErrorType type : values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
