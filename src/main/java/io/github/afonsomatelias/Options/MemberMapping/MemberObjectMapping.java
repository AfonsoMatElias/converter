package io.github.afonsomatelias.Options.MemberMapping;

import io.github.afonsomatelias.Mapper.Processor;

@SuppressWarnings("unchecked")
public class MemberObjectMapping {
	private final Processor<?> processor;

	public MemberObjectMapping(Processor<?> processor) {
		this.processor = processor;
	}

	/**
	 * Maps the source object to the destination class provided
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