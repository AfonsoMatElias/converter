package io.github.afonsomatelias.Core.Mappers.Interfaces;

import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Options.Interfaces.IMappingObjectActions;

public interface IObjectMapper<S> extends IMapper<S> {
	/**
	 * Maps the source object to the destination class provided
	 * 
	 * @param <D>   	the {@link D} object type
	 * @param clazz 	the {@link D} class type
	 * @return the object Converted
	 */
	public <D> D to(Class<D> clazz);

	/**
	 * Maps the {@link S} object to the destination class provided with a mapper modifier
	 * 
	 * @param <D>      	the {@link D} object type
	 * @param clazz    	the {@link D} class type
	 * @param modifier 	mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> D to(Class<D> clazz, I1Action<IMappingObjectActions<S, D>> modifier);
	
	/**
     * Creates a new instance of the source object with a different memory address,
     * applying mapping options if a modifier is provided.
     *
     * @param <D>      	the {@link D} object type which extends {@link S}
     * @param modifier 	mapping options that will be applied on map
     * @return 			new object instance or null in case of an exception
     */
	public <D extends S> S to(I1Action<IMappingObjectActions<S, D>> modifier);

	/**
	 * Maps or extracts values from the provided destination object back to the source object.
	 * 
	 * This method swaps the roles of the source and destination, effectively reversing the mapping process.
	 * 
	 * @param <D> 		the type of the destination object
	 * @param $source 	the destination object from which values are mapped to the source
	 * @return the source object with values mapped from the destination, or null if the destination is null
	 */
	public <D> S from(D $source);

	/**
	 * Maps or extracts values from the provided destination object back to the source object,
	 * with the option to apply a mapping modifier.
	 * 
	 * This method swaps the roles of the source and destination, effectively reversing the mapping process.
	 * It triggers BEFORE_MAP and AFTER_MAP actions if modifiers are set. The modifier allows additional
	 * actions or transformations to be applied during the mapping process.
	 * 
	 * @param <D> 		the type of the destination object
	 * @param $source 	the destination object from which values are mapped to the source
	 * @param modifier 	a callback allowing custom mapping actions to be applied
	 * @return the source object with values mapped from the destination, or null if the destination is null
	 */
	public <D> S from(D $source, I1Action<IMappingObjectActions<D, S>> modifier);
}
