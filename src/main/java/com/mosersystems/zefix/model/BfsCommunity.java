package com.mosersystems.zefix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Political commune according to the swiss official commune register.
 *
 * @param bfsId                number of the commune according to the swiss official commune register
 * @param canton               canton
 * @param name                 name of the political commune
 * @param registryOfCommerceId internal office number of the cantonal registry of commerce
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BfsCommunity(Long bfsId, String canton, String name, Long registryOfCommerceId) {
}
