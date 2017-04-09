package com.example.patrick.netnix.fragments;

/**
 * AsyncListener, interface for handling async callbacks.
 */
public interface AsyncListener {

    /**
     * Async event callback. Pass the retrieved item as object for reference.
     */
    void callback(Object o);

}
