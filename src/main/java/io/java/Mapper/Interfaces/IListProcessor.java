package io.java.Mapper.Interfaces;

import java.util.List;

import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Options.MappingActions;

public interface IListProcessor<Source> extends IProcessor<Source> {
	/**
	 * Converts the list of {@link Source} objects to the list of destination class
	 * provided
	 * 
	 * @param <Destination> the {@link Destination} object type
	 * @param clazz         the {@link Destination} class type
	 * @return the object Converted
	 */
	<Destination> List<Destination> to(Class<Destination> clazz);

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
			IV1Callback<MappingActions<List<Source>, List<Destination>>> modifier);
}
