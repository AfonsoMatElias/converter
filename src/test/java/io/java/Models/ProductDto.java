package io.java.Models;

public class ProductDto {
    private String name;
    private ProductDto parent;
    private String[] categories;

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

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }
}
