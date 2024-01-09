package io.github.cnvtr.Models;

public class Product {

    private String name;
    private Float price;
    private Product parent;
    private String categories = "Liquid;Refrig";


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

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }
}
