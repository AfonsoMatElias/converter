package io.github.afonsomatelias.Mapper.Interfaces;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackV1;
import io.github.afonsomatelias.Options.MappingActions;

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
	public <D> D to(Class<D> clazz, CallbackV1<MappingActions<S, D>> modifier);
}
