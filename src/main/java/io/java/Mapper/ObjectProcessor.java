package io.java.Mapper;

import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Configurations.ConverterShared;
import io.java.Mapper.Interfaces.IObjectProcessor;
import io.java.Options.MappingActions;

@SuppressWarnings("unchecked")
public class ObjectProcessor<Source> extends Processor<Source> implements IObjectProcessor<Source> {
	public ObjectProcessor(ConverterShared shared, Object source) {
		super(shared, (Source) source);
	}

	/**
	 * Converts the {@link Source} object to the destination class provided
	 * 
	 * @param <Destination> the {@link Destination} object type
	 * @param clazz         the {@link Destination} class type
	 * @return the object Converted
	 */
	public <Destination> Destination to(Class<Destination> clazz) {
		try {
			return (Destination) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts the {@link Source} object to the destination class provided with a
	 * mapper modifier
	 * 
	 * @param <Destination> the {@link Destination} object type
	 * @param clazz         the {@link Destination} class type
	 * @param modifier      mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <Destination> Destination to(Class<Destination> clazz,
			IV1Callback<MappingActions<Source, Destination>> modifier) {
		try {
			if (modifier != null) {
				// Assing to object to be able to trick the compiler
				Object modifierAsObject = actionOptions;

				modifier.call((MappingActions<Source, Destination>) modifierAsObject);
			}
			return (Destination) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}
}
