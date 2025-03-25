package io.github.afonsomatelias.Configurations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackV1;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.Expression.IMappingExpression;
import io.github.afonsomatelias.Options.Expression.MappingExpression;
import io.github.afonsomatelias.Options.Interfaces.IMappingObjectActions;

@SuppressWarnings("unchecked")
public class ConverterConfiguration extends Converter {
	public ConverterConfiguration() {
		super();
	}
	
	public ConverterConfiguration(IOptions<ConverterConfiguration> configOptions) {
		super();
		configOptions.call(this);
	}

	public interface IOptions<Config> { void call(Config config);}

	@SafeVarargs
	public final void addProfile(Class<? extends Profile>... profiles) {

		for (Class<? extends Profile> clsProfile : profiles) {
			try {
				final Profile profile = clsProfile.newInstance();
				profile.$super = this;
				profile.init();
			} catch (Exception e) {
				$$.err("Profile: " + clsProfile.getName() + "not initialized");
			}
		}
	}

	/**
	 * Creates a Mapping configuration for the Source and Destination Object type
	 * 
	 * @param <S>         the {@link S} Type
	 * @param <D>         the {@link D} Type
	 * @param source      the {@link S} Class
	 * @param destination the {@link D} Class
	 */
	public <S, D> IMappingExpression<S, D> createMap(
		Class<S> source,
		Class<D> destination
	) {
		shared.configurations.put(source.getName(), new MappingConfig(source, destination));
		return new MappingExpression<>(source, destination, shared);
	}

	/**
	 * Creates a Mapping configuration for the Source and Destination Object type
	 * 
	 * @param <S>         the {@link S} Type
	 * @param <D>         the {@link D} Type
	 * @param source      the {@link S} Class
	 * @param destination the {@link D} Class
	 */
	public <S, D> IMappingExpression<S, D> createMap(
		Class<S> source,
		Class<D> destination,
		CallbackV1<IMappingObjectActions<S, D>> modifier
	) {
		final IMappingExpression<S, D> mappingExpression = this.createMap(source, destination);

		if (modifier != null) {
			// Building the unique name of the action
			final String fieldActionOptionName = source.getName() + ":" + destination.getName();

			// Registering the action
			final MappingObjectActions<Object, Object> actionOptions = new MappingObjectActions<>();

			shared.globalActionOptions.put(fieldActionOptionName, actionOptions);

			// Assing to object to be able to trick the compiler
			final Object modifierAsObject = actionOptions;

			modifier.call((MappingObjectActions<S, D>) modifierAsObject);
		}

		return mappingExpression;
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
	public <TypeSource, TypeDestination> void addTransform(
		Class<TypeSource> from, 
		Class<TypeDestination> to,
		CallbackP1<TypeSource, TypeDestination> behavior
	) {
		final String uniqueName = from.getName() + ":" + to.getName();
		shared.tranformations.put(uniqueName, (CallbackP1<Object, Object>) behavior);
	}

	/**
	 * Gets all the configurations of the converter and return a Map of it
	 * 
	 * @return map of the configurations
	 */
	public Map<String, Object> getConfigs() {
		return new HashMap<String, Object>() {{
			for (Field config : shared.getClass().getDeclaredFields()) {
				try { put(config.getName().toUpperCase(), config.get(shared)); } 
				catch (Exception e) {}
			}
		}};
	}

	/**
	 * Sets Silent for all the System.out.println of the converter (Global)
	 * 
	 * @param silentLogs a boolean value to avoid warning logs on map
	 */
	public void setSilentLogs(boolean silentLogs) {
		$$.isSilent = shared.SILENT_LOGS = silentLogs;
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
	 * Register classes name that needs to ignored (This one is more precise)
	 * 
	 * @param classes the classes that need to be ignored globally
	 */
	public void skipTypes(Class<?>... classes) {
		for (Class<?> cls : classes)
			shared.classTypesToIgnore.add(cls.getName());
	}

	/**
	 * Register classes name that needs to ignored
	 * 
	 * @param classNames the classes name that need to be ignored globally
	 */
	public void skipTypes(String... classNames) {
		for (String clsName : classNames)
			shared.classTypesToIgnore.add(clsName);
	}

	public Converter createConverter() {
		return this;
	}
}
