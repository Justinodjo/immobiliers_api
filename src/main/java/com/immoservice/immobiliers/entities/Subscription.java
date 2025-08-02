package com.immoservice.immobiliers.entities;

public class Subscription {
    private String endpoint;
    private SubscriptionKeys keys;

    // getters / setters
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public SubscriptionKeys getKeys() {
        return keys;
    }

    public void setKeys(SubscriptionKeys keys) {
        this.keys = keys;
    }
}
