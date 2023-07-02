package io.java.Models;

public class Product {

    private String name;
    private Product parent;

    public Product() {
        name = "Afonso Matumona";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Product getParent() {
        return parent;
    }

    public void setParent(Product parent) {
        this.parent = parent;
    }
}
