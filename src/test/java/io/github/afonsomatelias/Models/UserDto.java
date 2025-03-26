package io.github.afonsomatelias.Models;

import java.time.LocalDate;

public class UserDto {

    private String name;
    private String username;
    private String password;
    private LocalDate bithdate;
    private String[] roles;
    
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
