package io.java.Mapper.Interfaces;

import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Options.MappingActions;

public interface IObjectProcessor<S> extends IProcessor<S> {
	/**
	 * Converts the {@link S} object to the destination class provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	public <D> D to(Class<D> clazz);

	/**
	 * Converts the {@link S} object to the destination class provided with a
	 * mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> D to(Class<D> clazz, IV1Callback<MappingActions<S, D>> modifier);
}
