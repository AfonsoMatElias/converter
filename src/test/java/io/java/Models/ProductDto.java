package io.java.Models;

public class ProductDto {
    private String name;
    private ProductDto parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductDto getParent() {
        return parent;
    }

    public void setParent(ProductDto parent) {
        this.parent = parent;
    }
}
