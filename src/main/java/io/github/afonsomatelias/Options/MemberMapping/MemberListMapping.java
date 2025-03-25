package io.github.afonsomatelias.Options.MemberMapping;

import io.github.afonsomatelias.Mapper.Processor;

@SuppressWarnings("unchecked")
public class MemberListMapping {
	private final Processor<?> processor;

	public MemberListMapping(Processor<?> processor) {
		this.processor = processor;
	}

	/**
	 * Maps the list of {@link S} objects to the list of destination class
	 * provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	public <D> D to(Class<D> clazz) {
		try {
			return (D) this.processor.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}
}