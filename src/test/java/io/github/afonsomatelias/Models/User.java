package io.github.afonsomatelias.Models;

import java.time.LocalDate;

public class User {

    private String name = "Afonso Matumona";
    private String username = "AfonsoMatElias";
    private String password = "Abc.123";
    private LocalDate bithdate = LocalDate.of(1989, 10, 15);
    private String[] roles = { "ADMIN" };

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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LocalDate getBithdate() {
        return bithdate;
    }
    public void setBithdate(LocalDate bithdate) {
        this.bithdate = bithdate;
    }
    public String[] getRoles() {
        return roles;
    }
    public void setRoles(String[] roles) {
        this.roles = roles;
    }
    
}
