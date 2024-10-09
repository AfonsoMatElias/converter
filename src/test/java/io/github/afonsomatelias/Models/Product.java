package io.github.afonsomatelias.Models;

import java.util.List;

public class Product {

    private String name;
    private Float price;
    private String categories = "Liquid;Refrig";

    private Product parent;
    private List<Product> products;


    public Product() {
        name = "Coca Cola";
        price = 0.5f;
    }

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

    public Product getParent() {
        return parent;
    }

    public void setParent(Product parent) {
        this.parent = parent;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
}
