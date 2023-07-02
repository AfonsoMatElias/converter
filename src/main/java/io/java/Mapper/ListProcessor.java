package io.java.Mapper;

import java.util.List;

import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Configurations.ConverterShared;
import io.java.Mapper.Interfaces.IListProcessor;
import io.java.Options.MappingActions;

@SuppressWarnings("unchecked")
public class ListProcessor<Source> extends Processor<Source>  implements IListProcessor<Source> {

	public ListProcessor(ConverterShared shared, Object source) {
		super(shared, (Source) source);
	}

	/**
	 * Converts the list of {@link Source} objects to the list of destination class
	 * provided
	 * 
	 * @param <Destination> the {@link Destination} object type
	 * @param clazz         the {@link Destination} class type
	 * @return the object Converted
	 */
	public <Destination> List<Destination> to(Class<Destination> clazz) {
		try {
			return (List<Destination>) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts the list of {@link Source} objects to the list of destination class
	 * provided with a mapper modifier
	 * 
	 * @param <Destination> the {@link Destination} object type
	 * @param clazz         the {@link Destination} class type
	 * @param modifier      mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <Destination> List<Destination> to(Class<Destination> clazz,
			IV1Callback<MappingActions<List<Source>, List<Destination>>> modifier) {
		try {
			if (modifier != null) {
				// Assing to object to be able to trick the compiler
				Object modifierAsObject = actionOptions;

				// Calling the function
				modifier.call((MappingActions<List<Source>, List<Destination>>) modifierAsObject);
			}

			return (List<Destination>) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}
}
