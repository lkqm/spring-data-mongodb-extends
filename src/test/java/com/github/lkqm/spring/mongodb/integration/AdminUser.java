package com.github.lkqm.spring.mongodb.integration;

import java.util.Objects;

// 测试实体
public class AdminUser extends User {

    private String role;

    public AdminUser() {
    }

    public AdminUser(String id, String name, Integer age, String role) {
        super(id, name, age);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdminUser)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AdminUser adminUser = (AdminUser) o;
        return Objects.equals(role, adminUser.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }
}
