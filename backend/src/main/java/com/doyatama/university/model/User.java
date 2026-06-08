package com.doyatama.university.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;

public class User {
    private String id;

    @NotBlank
    @Size(max = 40)
    private String name;
    @NotBlank
    @Size(max = 15)
    private String username;
    @NotBlank
    @Size(max = 40)
    @Email
    private String email;
    @NotBlank
    @Size(max = 100)
    private String password;

    private School school;
    private School study_program;

    @NotBlank
    private String roles;

    private Instant createdAt;

    public User() {

    }

    public User(String id, String name, String username, String email, String password, School school, String roles,
            Instant createdAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.school = school;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public School getSchool() {
        return school != null ? school : study_program;
    }

    public void setSchool(School school) {
        this.school = school;
        this.study_program = school;
    }

    public School getStudy_program() {
        return study_program != null ? study_program : school;
    }

    public void setStudy_program(School study_program) {
        this.study_program = study_program;
        if (this.school == null) {
            this.school = study_program;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isValid() {
        return id != null && name != null && username != null
                && email != null && password != null && getSchool() != null
                && roles != null;
    }

    public void set(String fieldName, String value) {
        switch (fieldName) {
            case "id":
                this.id = value;
                break;
            case "name":
                this.name = value;
                break;
            case "username":
                this.username = value;
                break;
            case "email":
                this.email = value;
                break;
            case "password":
                this.password = value;
                break;
            case "roles":
                this.roles = value;
                break;
            case "createdAt":
                this.createdAt = Instant.parse(value);
                break;
            default:
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
        }
    }
}
