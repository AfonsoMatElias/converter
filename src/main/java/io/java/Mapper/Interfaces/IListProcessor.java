package io.java.Mapper.Interfaces;

import java.util.List;

import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Options.MappingActions;

public interface IListProcessor<S> extends IProcessor<S> {
	/**
	 * Converts the list of {@link S} objects to the list of destination class
	 * provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	<D> List<D> to(Class<D> clazz);

	/**
	 * Converts the list of {@link S} objects to the list of destination class
	 * provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz, IV1Callback<MappingActions<List<S>, List<D>>> modifier);
}
