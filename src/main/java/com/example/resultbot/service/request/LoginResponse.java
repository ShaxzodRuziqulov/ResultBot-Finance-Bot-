package com.example.resultbot.service.request;

public record LoginResponse(String token, long expiresIn) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private long expiresIn;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder expiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public LoginResponse build() {
            return new LoginResponse(token, expiresIn);
        }
    }
}

