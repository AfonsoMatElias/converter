package io.java.Mapper;

import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Configurations.ConverterShared;
import io.java.Mapper.Interfaces.IObjectProcessor;
import io.java.Options.MappingActions;

@SuppressWarnings("unchecked")
public class ObjectProcessor<S> extends Processor<S> implements IObjectProcessor<S> {
	public ObjectProcessor(ConverterShared shared, Object source) {
		super(shared, (S) source);
	}

	/**
	 * Converts the {@link S} object to the destination class provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	public <D> D to(Class<D> clazz) {
		try {
			return (D) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts the {@link S} object to the destination class provided with a
	 * mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> D to(Class<D> clazz, IV1Callback<MappingActions<S, D>> modifier) {
		try {
			if (modifier != null) {
				// Assing to object to be able to trick the compiler
				Object modifierAsObject = actionOptions;

				modifier.call((MappingActions<S, D>) modifierAsObject);
			}
			return (D) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}
}
