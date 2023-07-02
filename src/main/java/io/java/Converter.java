package io.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.java.Callback.ICallbacks.I1Callback;
import io.java.Callback.ICallbacks.IV1Callback;
import io.java.Configurations.ConverterShared;
import io.java.Configurations.MapperConfig;
import io.java.Mapper.ListProcessor;
import io.java.Mapper.ObjectProcessor;
import io.java.Mapper.Interfaces.IListProcessor;
import io.java.Mapper.Interfaces.IObjectProcessor;
import io.java.Options.MappingActions;
import io.java.Options.MappingExpression;
import io.java.Options.Interfaces.IMappingActions;
import io.java.Options.Interfaces.IMappingExpression;

@SuppressWarnings("unchecked")
public class Converter implements IConverter {
	/**
	 * Default Converter
	 */
	public Converter() {
		shared = new ConverterShared();
	}

	// All the public properties that will e shared between inner instances
	private ConverterShared shared;

	/**
	 * Creates Mapping Processor for the {@link Source} Object
	 * 
	 * @param <Source> the {@link Source} Type
	 * @param source   the {@link Source} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	public <Source> IObjectProcessor<Source> map(Source source) {
		return new ObjectProcessor<Source>(shared, source);
	}

	/**
	 * Creates Mapping Processor for the {@link Source} Object
	 * 
	 * @param <Source> the {@link Source} Type
	 * @param source   the {@link Source} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	public <Source> IListProcessor<Source> map(List<Source> source) {
		return new ListProcessor<Source>(shared, source);
	}

	/**
	 * Creates a Mapping configuration for the Source ans Destination Object type
	 * 
	 * @param <Source>      the {@link Source} Type
	 * @param <Destination> the {@link Destination} Type
	 * @param source        the {@link Source} Class
	 * @param destination   the {@link Destination} Class
	 */
	public <Source, Destination> IMappingExpression<Source, Destination> createMap(
			Class<Source> source,
			Class<Destination> destination) {
		shared.configurations.put(source.getName(), new MapperConfig(source, destination));
		return new MappingExpression<>(source, destination, shared);
	}

	/**
	 * Creates a Mapping configuration for the Source ans Destination Object type
	 * 
	 * @param <Source>      the {@link Source} Type
	 * @param <Destination> the {@link Destination} Type
	 * @param source        the {@link Source} Class
	 * @param destination   the {@link Destination} Class
	 */
	public <Source, Destination> IMappingExpression<Source, Destination> createMap(
			Class<Source> source,
			Class<Destination> destination,
			IV1Callback<IMappingActions<Source, Destination>> modifier) {
		this.createMap(source, destination);

		if (modifier != null) {

			// Building the unique name of the action
			String fieldActionOptionName = source.getName() + ":" + destination.getName();

			// Registering the action
			MappingActions<Object, Object> actionOptions = new MappingActions<>();
			shared.globalActionOptions.put(fieldActionOptionName, actionOptions);

			// Assing to object to be able to trick the compiler
			Object modifierAsObject = actionOptions;

			modifier.call((MappingActions<Source, Destination>) modifierAsObject);
		}

		return new MappingExpression<>(source, destination, shared);
	}

	/**
	 * Mutates mapping behavior for {@link From} Type to {@link To} Type
	 * 
	 * @param <From>   the Type that needs to intercepted
	 * @param <To>     the Type to be converted to
	 * @param from     the {@link From} Class
	 * @param to       the {@link From} Class
	 * @param behavior the interception bahavior
	 */
	public <From, To> void mutateMapping(Class<From> from, Class<To> to, I1Callback<From, To> behavior) {
		String mutationName = from.getName() + ":" + to.getName();
		shared.mutations.put(mutationName, (I1Callback<Object, Object>) behavior);
	}

	/**
	 * Gets all the configurations of the converter and return a Map of it
	 * 
	 * @return map of the configurations
	 */
	public Map<String, Object> getConfig() {
		return new HashMap<String, Object>() {
			{
				put("USE_MAPPING_CONFIG", shared.USE_MAPPING_CONFIG);
				put("LIMIT_CYCLE_MAPPING", shared.LIMIT_CYCLE_MAPPING);
				put("GLOBAL_ACTIONOPTIONS", shared.globalActionOptions);
				put("CONFIGURATIONS", shared.configurations);
				put("MUTATIONS", shared.mutations);
			}
		};
	}

	/**
	 * Sets the configuration value that allow to use mapping configuration on map
	 * or not
	 * 
	 * @param useMapConfig a boolean value to indicates if the mapping configuration
	 *                     needs to be used
	 */
	public void setMapWithConfig(boolean useMapConfig) {
		shared.USE_MAPPING_CONFIG = useMapConfig;
	}

	/**
	 * Sets the limit while mapping self references objects
	 * 
	 * @param limit the number of cycle
	 * @see DefaultValue Default Value is `3`
	 */
	public void setLimitCycleMapping(int limit) {
		shared.LIMIT_CYCLE_MAPPING = limit;
	}
}
