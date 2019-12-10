package com.trasier.api;

public class Endpoint {
    private String name;
    private String ipAddress;
    private String port;
    private String hostname;

    private Endpoint(Builder builder) {
        this.name = builder.name;
        this.ipAddress = builder.ipAddress;
        this.port = builder.port;
        this.hostname = builder.hostname;
    }

    public static Builder newEndpoint() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public static final class Builder {
        private String name;
        private String ipAddress;
        private String port;
        private String hostname;

        private Builder() {
        }

        public Endpoint build() {
            return new Endpoint(this);
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder port(String port) {
            this.port = port;
            return this;
        }

        public Builder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }
    }
}
