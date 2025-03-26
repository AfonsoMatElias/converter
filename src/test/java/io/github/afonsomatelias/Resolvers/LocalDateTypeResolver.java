package io.github.afonsomatelias.Resolvers;

import java.time.LocalDate;

import io.github.afonsomatelias.Configurations.TypeResolver;

public class LocalDateTypeResolver extends TypeResolver {

	public LocalDateTypeResolver() {
		super(LocalDate.class);
	}

	@Override
	public LocalDate resolve(Object value) {
		if (!(value instanceof String))
			return null;

		final String str = value.toString();
		return LocalDate.parse(str.split("T")[0]);
	}
}
