package io.github.afonsomatelias.Configurations;

import io.github.afonsomatelias.Callback.ICallbacks.ITypeResolver;

public abstract class TypeResolver implements ITypeResolver {
	/** Default constructor */
	public TypeResolver(Class<?> classType) {
		this.type = classType;
	}

	/** The type to be resolved */
	protected Class<?> type;
}