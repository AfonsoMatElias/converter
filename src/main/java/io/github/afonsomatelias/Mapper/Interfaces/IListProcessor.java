package io.github.afonsomatelias.Mapper.Interfaces;

import java.util.List;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackV1;
import io.github.afonsomatelias.Options.Interfaces.IMappingActions;
import io.github.afonsomatelias.Options.Interfaces.IMappingListActions;

public interface IListProcessor<S> extends IProcessor<S> {
	/**
	 * Converts the list of {@link S} objects to the list of destination class
	 * provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz);

	/**
	 * Creates a new instance of object provided, just like copy and paste with
	 * different memory address
	 * 
	 * @param modifier mapping options that will be applied on map
	 * @return new object instance
	 */
	public <D extends S> S to(CallbackV1<IMappingActions<S, D>> modifier);

	/**
	 * Converts the list of {@link S} objects to the list of destination class
	 * provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz, CallbackV1<IMappingListActions<S, D>> modifier);
}
