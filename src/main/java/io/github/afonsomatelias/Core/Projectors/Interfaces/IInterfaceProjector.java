package io.github.afonsomatelias.Core.Projectors.Interfaces;

public interface IInterfaceProjector<Entry> {
	/**
	 * Maps the source object to the destination class provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	public <D> D to(Class<D> clazz);
}
