package com.smartshop.dto;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private String role;
    private String shopName;

    public AuthResponse() {}

    public AuthResponse(String token, String type, Long id, String name, String email, String role, String shopName) {
        this.token = token;
        this.type = (type != null) ? type : "Bearer";
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.shopName = shopName;
    }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String name;
        private String email;
        private String role;
        private String shopName;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder type(String type) { this.type = type; return this; }
        public AuthResponseBuilder id(Long id) { this.id = id; return this; }
        public AuthResponseBuilder name(String name) { this.name = name; return this; }
        public AuthResponseBuilder email(String email) { this.email = email; return this; }
        public AuthResponseBuilder role(String role) { this.role = role; return this; }
        public AuthResponseBuilder shopName(String shopName) { this.shopName = shopName; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, type, id, name, email, role, shopName);
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
}
