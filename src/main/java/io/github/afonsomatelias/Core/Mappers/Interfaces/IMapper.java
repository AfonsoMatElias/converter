package io.github.afonsomatelias.Core.Mappers.Interfaces;

public interface IMapper<S> {
	/**
	 * Creates a new instance of the source object, effectively acting like a 
	 * copy operation but with a different memory address.
	 * 
	 * @param <D> the type of the object extending the source type
	 * @return a new instance of the source object, or null if the source is null
	 */
	<D extends S> D to();
}
