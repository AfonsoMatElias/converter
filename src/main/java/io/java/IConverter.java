package io.java;

import java.util.List;
import java.util.Map;

import io.java.Callback.ICallbacks.CallbackP1;
import io.java.Callback.ICallbacks.CallbackV1;
import io.java.Mapper.Interfaces.IListProcessor;
import io.java.Mapper.Interfaces.IObjectProcessor;
import io.java.Options.Interfaces.IMappingActions;
import io.java.Options.Interfaces.IMappingExpression;

public interface IConverter {
	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<S> IObjectProcessor<S> map(S source);

	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<S> IListProcessor<S> map(List<S> source);

	/**
	 * Creates a Mapping configuration for the Source ans Destination Object type
	 * 
	 * @param <S>      the {@link S} Type
	 * @param <D> the {@link D} Type
	 * @param source        the {@link S} Class
	 * @param destination   the {@link D} Class
	 */
	<S, D> IMappingExpression<S, D> createMap(
			Class<S> source,
			Class<D> destination);

	/**
	 * Creates a Mapping configuration for the Source ans Destination Object type
	 * 
	 * @param <S>         the {@link S} Type
	 * @param <D>         the {@link D} Type
	 * @param source      the {@link S} Class
	 * @param destination the {@link D} Class
	 */
	<S, D> IMappingExpression<S, D> createMap(
			Class<S> source,
			Class<D> destination,
			CallbackV1<IMappingActions<S, D>> modifier);

	/**
	 * Add tranformation to a mapping behavior for {@link From} Type to {@link To}
	 * Type
	 * 
	 * @param <From>   the Type that needs to intercepted
	 * @param <To>     the Type to be converted to
	 * @param from     the {@link From} Class
	 * @param to       the {@link From} Class
	 * @param behavior the interception bahavior
	 */
	<From, To> void addTransform(Class<From> from, Class<To> to, CallbackP1<From, To> behavior);

	/**
	 * Gets all the configurations of the converter and return a Map of it
	 * 
	 * @return map of the configurations
	 */
	Map<String, Object> getConfigs();

	/**
	 * Sets the configuration value that allow to use mapping configuration on map
	 * or not
	 * 
	 * @param useMapConfig a boolean value to indicates if the mapping configuration
	 *                     needs to be used
	 */
	void setUseMapConfiguration(boolean useMapConfig);

	/**
	 * Sets the limit while mapping self references objects
	 * 
	 * @param limit the number of cycle
	 * @see DefaultValue Default Value is `3`
	 */
	void setLimitCycleMapping(int limit);
}
