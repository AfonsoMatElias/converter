package io.java.Configurations;

public class MapperConfig {
	private Class<?> source;
	private Class<?> destination;

	public MapperConfig(Class<?> source, Class<?> destination) {
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
