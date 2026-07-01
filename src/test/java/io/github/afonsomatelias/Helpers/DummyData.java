package io.github.afonsomatelias.Helpers;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import io.github.afonsomatelias.Models.ProductProjection;

public class DummyData {

	public static ProductProjection generateProductProjection() {
		return new ProductProjection() {
            @Override
            public String getName() { return "Coca Cola"; }
            @Override
            public Float getPrice() { return 0.5f; }

            @Override
            public String[] getCategories() { return new String[]{ "Liquid", "Refrigerator" }; }
            @Override
            public Integer getQuantity() { return 15; }
        };
	}

	public static LinkedHashMap<String, Object> generateUserLinkedHashMap() {

        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();

        data.put("name", "John Doe");
        data.put("username", "johndoe");
        data.put("password", "123.AbC");
        data.put("bithdate", "2025-03-25T22:44:17.605Z");
        data.put("roles", new String[]{ "ADMIN" });

		return data;
	}

	public static LinkedHashMap<String, Object> generateProductWithParentLinkedHashMap() {
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();

        LinkedHashMap<String, Object> parent = new LinkedHashMap<String, Object>();
        parent.put("name", "Water");
        parent.put("price", 0.5f);
        parent.put("quantity", 35);
        
        LinkedHashMap<String, Object> prod1 = new LinkedHashMap<String, Object>();
        prod1.put("name", "Cola-Cola");
        prod1.put("price", 1f);
        prod1.put("quantity", 40);
        LinkedHashMap<String, Object> prod2 = new LinkedHashMap<String, Object>();
        prod2.put("name", "Beer");
        prod2.put("price", 2f);
        prod2.put("quantity", 12);
        List<LinkedHashMap<String, Object>> products = Arrays.asList(prod1, prod2);
        
        
        data.put("name", "Sprite");
        data.put("price", 1f);
        data.put("quantity", 15);
        
        data.put("parent", parent);
        data.put("products", products);

		return data;
	}

	public static LinkedHashMap<String, Object> generateProductLinkedHashMap() {
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("price", 1f);
        data.put("name", "Sprite");
        data.put("quantity", 15);
        return data;
	}

    public static LinkedHashMap<String, Object> generateProductLinkedHashMapWithStringValue() {
		LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("name", "Sprite");
        data.put("price", "1");
        data.put("quantity", "15");
        return data;
	}
}
