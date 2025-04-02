package io.github.afonsomatelias.Core.Projectors.Interfaces;

import java.util.List;

import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Options.Interfaces.IMappingListActions;

public interface IListProjector<Entry> {
	/**
	 * Maps the source object to the destination class provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz);
	
	/**
	 * Maps the {@link Entry} object to the destination class provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz, I1Action<IMappingListActions<Entry, D>> modifier);
}
