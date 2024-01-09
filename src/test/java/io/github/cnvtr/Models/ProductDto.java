package io.github.cnvtr.Models;

public class ProductDto {
    private String name;
    private Float price;
    private ProductDto parent;
    private String[] categories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
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
