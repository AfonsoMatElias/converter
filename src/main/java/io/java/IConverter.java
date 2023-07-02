package io.java;

import java.util.List;
import java.util.Map;

import io.java.Callback.ICallbacks.I1Callback;
import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Mapper.Interfaces.IListProcessor;
import io.java.Mapper.Interfaces.IObjectProcessor;
import io.java.Options.Interfaces.IMappingActions;
import io.java.Options.Interfaces.IMappingExpression;

public interface IConverter {
	/**
	 * Creates Mapping Processor for the {@link Source} Object
	 * 
	 * @param <Source> the {@link Source} Type
	 * @param source   the {@link Source} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<Source> IObjectProcessor<Source> map(Source source);

	/**
	 * Creates Mapping Processor for the {@link Source} Object
	 * 
	 * @param <Source> the {@link Source} Type
	 * @param source   the {@link Source} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<Source> IListProcessor<Source> map(List<Source> source);

	/**
	 * Creates a Mapping configuration for the Source ans Destination Object type
	 * 
	 * @param <Source>      the {@link Source} Type
	 * @param <Destination> the {@link Destination} Type
	 * @param source        the {@link Source} Class
	 * @param destination   the {@link Destination} Class
	 */
	<Source, Destination> IMappingExpression<Source, Destination> createMap(
			Class<Source> source,
			Class<Destination> destination);

	/**
	 * Creates a Mapping configuration for the Source ans Destination Object type
	 * 
	 * @param <Source>      the {@link Source} Type
	 * @param <Destination> the {@link Destination} Type
	 * @param source        the {@link Source} Class
	 * @param destination   the {@link Destination} Class
	 */
	<Source, Destination> IMappingExpression<Source, Destination> createMap(
			Class<Source> source,
			Class<Destination> destination,
			IV1Callback<IMappingActions<Source, Destination>> modifier);

	/**
	 * Mutates mapping behavior for {@link From} Type to {@link To} Type
	 * 
	 * @param <From>   the Type that needs to intercepted
	 * @param <To>     the Type to be converted to
	 * @param from     the {@link From} Class
	 * @param to       the {@link From} Class
	 * @param behavior the interception bahavior
	 */
	<From, To> void mutateMapping(Class<From> from, Class<To> to, I1Callback<From, To> behavior);

	/**
	 * Gets all the configurations of the converter and return a Map of it
	 * 
	 * @return map of the configurations
	 */
	Map<String, Object> getConfig();

	/**
	 * Sets the configuration value that allow to use mapping configuration on map
	 * or not
	 * 
	 * @param useMapConfig a boolean value to indicates if the mapping configuration
	 *                     needs to be used
	 */
	void setMapWithConfig(boolean useMapConfig);

	/**
	 * Sets the limit while mapping self references objects
	 * 
	 * @param limit the number of cycle
	 * @see DefaultValue Default Value is `3`
	 */
	void setLimitCycleMapping(int limit);
}
