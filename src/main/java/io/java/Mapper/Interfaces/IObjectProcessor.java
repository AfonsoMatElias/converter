package io.java.Mapper.Interfaces;

import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Options.MappingActions;

public interface IObjectProcessor<Source> extends IProcessor<Source> {
	/**
	 * Converts the {@link Source} object to the destination class provided
	 * 
	 * @param <Destination> the {@link Destination} object type
	 * @param clazz         the {@link Destination} class type
	 * @return the object Converted
	 */
	public <Destination> Destination to(Class<Destination> clazz);

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
			IV1Callback<MappingActions<Source, Destination>> modifier);
}
