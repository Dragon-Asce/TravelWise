package com.travelwise.network

/**
 * Base contract interface for remote API clients in the TravelWise system.
 *
 * Provides a standardized abstraction for executing HTTP requests to external endpoints.
 *
 * @author Farhan
 */
interface ApiClient {

    /**
     * Executes a GET request to the specified HTTP/HTTPS target endpoint URL.
     *
     * @param url The target web endpoint address.
     * @return Raw JSON or string response body from the server, or `null` if the request fails or encounters an error.
     */
    suspend fun fetchData(url: String): String?
}