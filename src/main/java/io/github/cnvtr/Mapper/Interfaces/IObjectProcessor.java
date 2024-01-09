package io.github.cnvtr.Mapper.Interfaces;

import io.github.cnvtr.Callback.ICallbacks.CallbackV1;
import io.github.cnvtr.Options.Interfaces.IMappingActions;

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
	public <D> D to(Class<D> clazz, CallbackV1<IMappingActions<S, D>> modifier);
	
	/**
	 * Maps or Extracts values from a Converts from the destination to the source
	 * 
	 * @param <D>   the {@link D} object type
	 * @return the object Converted
	 */
	public <D> S from(D destination);

	/**
	 * Maps or Extracts values from a Converts from the destination to the source with a
	 * mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> S from(D destination, CallbackV1<IMappingActions<S, D>> modifier);
}
