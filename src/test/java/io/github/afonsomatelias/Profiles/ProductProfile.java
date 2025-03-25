package io.github.afonsomatelias.Profiles;

import io.github.afonsomatelias.Configurations.Profile;
import io.github.afonsomatelias.Models.Product;
import io.github.afonsomatelias.Models.ProductDto;

public class ProductProfile extends Profile {
	@Override
	public void init() {
		createMap(Product.class, ProductDto.class);
	}
}
