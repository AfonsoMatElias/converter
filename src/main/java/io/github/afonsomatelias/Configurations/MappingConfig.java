package io.github.afonsomatelias.Configurations;

public class MappingConfig {
	private Class<?> source;
	private Class<?> destination;

	public MappingConfig(Class<?> source, Class<?> destination) {
		this.source = source;
		this.destination = destination;
	}

	public Class<?> getSource() {
		return source;
	}

	public Class<?> getDestination() {
		return destination;
	}
}
