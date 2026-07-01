package io.github.afonsomatelias.Core.Interfaces;

import java.util.List;

import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Options.Interfaces.IMappingListActions;

public interface IListMapper<S> {
	/**
	 * Maps the list of {@link S} objects to the list of destination class
	 * provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz);
	
	/**
	 * Maps the list of {@link S} objects to the list of destination class
	 * provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz, I1Action<IMappingListActions<S, D>> modifier);

	/**
	 * Creates a new instance of the object provided, just like copy and paste with
	 * a different memory address, applying mapping options if a modifier is provided.
	 *
	 * @param <D>      the {@link D} object type which extends {@link S}
	 * @param modifier mapping options that will be applied on map
	 * @return new object instance or null in case of an exception
	 */
	public <D extends S> S to(I1Action<IMappingListActions<S, D>> modifier);
}
