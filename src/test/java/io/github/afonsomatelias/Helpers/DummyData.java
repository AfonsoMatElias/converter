package io.github.afonsomatelias.Helpers;

import java.util.Arrays;
import java.util.LinkedHashMap;

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
		return new LinkedHashMap<String, Object>() {{
            put("name", "John Doe");
            put("username", "johndoe");
            put("password", "123.AbC");
            put("bithdate", "2025-03-25T22:44:17.605Z");
            put("roles", new String[]{ "ADMIN" });
        }};
	}

	public static LinkedHashMap<String, Object> generateProductWithParentLinkedHashMap() {
		return new LinkedHashMap<String, Object>() {{
            put("name", "Sprite");
            put("price", 1f);
            put("quantity", 15);

            put("parent", new LinkedHashMap<String, Object>() {{
                put("name", "Water");
                put("price", 0.5f);
                put("quantity", 35);
            }});

            put("products", Arrays.asList(
                new LinkedHashMap<String, Object>() {{
                    put("name", "Cola-Cola");
                    put("price", 1f);
                    put("quantity", 40);
                }},
                new LinkedHashMap<String, Object>() {{
                    put("name", "Beer");
                    put("price", 2f);
                    put("quantity", 12);
                }}
            ));
        }};
	}

	public static LinkedHashMap<String, Object> generateProductLinkedHashMap() {
		return new LinkedHashMap<String, Object>() {{
            put("name", "Sprite");
            put("price", 1f);
            put("quantity", 15);
        }};
	}

    public static LinkedHashMap<String, Object> generateProductLinkedHashMapWithStringValue() {
		return new LinkedHashMap<String, Object>() {{
            put("name", "Sprite");
            put("price", "1");
            put("quantity", "15");
        }};
	}
}
