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
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	public <S> IObjectProcessor<S> map(S source) {
		return new ObjectProcessor<S>(shared, source);
	}

	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	public <S> IListProcessor<S> map(List<S> source) {
		return new ListProcessor<S>(shared, source);
	}

	/**
	 * Creates a Mapping configuration for the Source ans Destination Object type
	 * 
	 * @param <S>         the {@link S} Type
	 * @param <D>         the {@link D} Type
	 * @param source      the {@link S} Class
	 * @param destination the {@link D} Class
	 */
	public <S, D> IMappingExpression<S, D> createMap(
			Class<S> source,
			Class<D> destination) {
		shared.configurations.put(source.getName(), new MapperConfig(source, destination));
		return new MappingExpression<>(source, destination, shared);
	}

	/**
	 * Creates a Mapping configuration for the Source ans Destination Object type
	 * 
	 * @param <S>         the {@link S} Type
	 * @param <D>         the {@link D} Type
	 * @param source      the {@link S} Class
	 * @param destination the {@link D} Class
	 */
	public <S, D> IMappingExpression<S, D> createMap(
			Class<S> source,
			Class<D> destination,
			IV1Callback<IMappingActions<S, D>> modifier) {
		this.createMap(source, destination);

		if (modifier != null) {
			// Building the unique name of the action
			String fieldActionOptionName = new StringBuilder().append(source.getName()).append("+")
					.append(destination.getName()).toString();

			// Registering the action
			MappingActions<Object, Object> actionOptions = new MappingActions<>();
			shared.globalActionOptions.put(fieldActionOptionName, actionOptions);

			// Assing to object to be able to trick the compiler
			Object modifierAsObject = actionOptions;

			modifier.call((MappingActions<S, D>) modifierAsObject);
		}

		return new MappingExpression<>(source, destination, shared);
	}

	/**
	 * Add tranformation to a mapping behavior for {@link TypeSource} Type to
	 * {@link TypeDestination} Type
	 * 
	 * @param <TypeSource>      the Type that needs to intercepted
	 * @param <TypeDestination> the Type to be converted to
	 * @param from              the {@link TypeSource} Class
	 * @param to                the {@link TypeSource} Class
	 * @param behavior          the interception bahavior
	 */
	public <TypeSource, TypeDestination> void addTransform(Class<TypeSource> from, Class<TypeDestination> to,
			I1Callback<TypeSource, TypeDestination> behavior) {
		String name = new StringBuilder().append(from.getName()).append("+").append(to.getName()).toString();
		shared.tranformations.put(name, (I1Callback<Object, Object>) behavior);
	}

	/**
	 * Gets all the configurations of the converter and return a Map of it
	 * 
	 * @return map of the configurations
	 */
	public Map<String, Object> getConfigs() {
		return new HashMap<String, Object>() {
			{
				put("USE_MAPPING_CONFIG", shared.USE_MAPPING_CONFIG);
				put("LIMIT_CYCLE_MAPPING", shared.LIMIT_CYCLE_MAPPING);
				put("GLOBAL_ACTIONOPTIONS", shared.globalActionOptions);
				put("CONFIGURATIONS", shared.configurations);
				put("TRANFORMATIONS", shared.tranformations);
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
	public void setUseMapConfiguration(boolean useMapConfig) {
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
