package io.java.Mapper;

import java.util.List;

import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Configurations.ConverterShared;
import io.java.Mapper.Interfaces.IListProcessor;
import io.java.Options.MappingActions;

@SuppressWarnings("unchecked")
public class ListProcessor<S> extends Processor<S> implements IListProcessor<S> {

	public ListProcessor(ConverterShared shared, Object source) {
		super(shared, (S) source);
	}

	/**
	 * Converts the list of {@link S} objects to the list of destination class
	 * provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz) {
		try {
			return (List<D>) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts the list of {@link S} objects to the list of destination class
	 * provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz, IV1Callback<MappingActions<List<S>, List<D>>> modifier) {
		try {
			if (modifier != null) {
				// Assing to object to be able to trick the compiler
				Object modifierAsObject = actionOptions;

				// Calling the function
				modifier.call((MappingActions<List<S>, List<D>>) modifierAsObject);
			}

			return (List<D>) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}
}
