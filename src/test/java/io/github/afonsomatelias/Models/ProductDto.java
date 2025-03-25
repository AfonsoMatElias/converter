package io.github.afonsomatelias.Models;

import java.util.List;

public class ProductDto {
    private String name;
    private Float price;
    private Integer quantity;
    private String[] categories;

    private ProductDto parent;
    private List<ProductDto> products;
    
    private ProductDto forMemberMapTestChild;
    
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

    public ProductDto getForMemberMapTestChild() {
        return forMemberMapTestChild;
    }

    public void setForMemberMapTestChild(ProductDto forMemberMapTestChild) {
        this.forMemberMapTestChild = forMemberMapTestChild;
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }
    
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
