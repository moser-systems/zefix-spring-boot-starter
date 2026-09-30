package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Current status of a company.
 */
public enum CompanyStatus {
    /** Active company. */
    ACTIVE,
    /** Deleted from the commercial register. */
    CANCELLED,
    /** In liquidation. */
    BEING_CANCELLED,
    /** A value not known to this client version. */
    UNKNOWN;

    /**
     * Maps an API value to a constant, falling back to {@link #UNKNOWN} for unknown values.
     *
     * @param value API value
     * @return the matching constant, or {@code null} if {@code value} is {@code null}
     */
    @JsonCreator
    public static CompanyStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (CompanyStatus status : values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
