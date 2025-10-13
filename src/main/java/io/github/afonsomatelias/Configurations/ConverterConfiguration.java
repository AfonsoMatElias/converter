package io.github.afonsomatelias.Configurations;

import static io.github.afonsomatelias.Helpers.Global.DEFAULT_TYPES_RESOLVER;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Callback.ICallbacks.ITypeResolver;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.Expression.IMappingExpression;
import io.github.afonsomatelias.Options.Expression.MappingExpression;
import io.github.afonsomatelias.Options.Interfaces.IMappingObjectActions;

@SuppressWarnings("unchecked")
public class ConverterConfiguration extends Converter {
	public ConverterConfiguration() {
		// Initializing all the default configs
		super(); this.init();
	}
	
	public ConverterConfiguration(IOptions<ConverterConfiguration> configOptions) {
		// Initializing all the default configs
		super(); this.init();

		// Apply the configuration after the initialization
		configOptions.call(this);
	}

	@FunctionalInterface
	public interface IOptions<Config> { void call(Config config);}

	@SafeVarargs
	public final void addProfile(Class<? extends Profile>... profiles) {
		// Initializing all the profiles
		for (Class<? extends Profile> clsProfile : profiles) {
			try {
				// Initializing the profile
				Profile profile = clsProfile.newInstance();

				// adding the Converter instance
				profile.$super = this;

				// Calling the config initialization
				profile.init();
			} catch (Exception e) {
				$$.err("Profile: " + clsProfile.getName() + "not initialized");
			}
		}
	}
	
	/**
	 * Allows to use a specific type resolver for a given class type.
	 * 
	 * @param type    the class type that will be associated with the resolver
	 * @param resolver the resolver to be associated with the class type
	 */
	public final void use(Class<?> type, ITypeResolver resolver) {
		shared.typeResolvers.put(type, resolver);
	}

	/**
	 * Allows to use a set of type resolvers for the conversion.
	 * 
	 * @param types the classes of the type resolvers to be used
	 */
	@SafeVarargs
	public final void use(Class<? extends TypeResolver>... types) {
		// Initializing all the profiles
		for (Class<? extends TypeResolver> clsResolver : types) {
			try {
				// Initializing the profile
				TypeResolver typeResolver = clsResolver.newInstance();

				// Applying the type resolver
				shared.typeResolvers.put(typeResolver.type, (value) -> typeResolver.resolve(value));
			} catch (Exception e) {
				$$.err("TypeResolver: " + clsResolver.getName() + "not initialized");
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
		I1Action<IMappingObjectActions<S, D>> modifier
	) {
		IMappingExpression<S, D> mappingExpression = this.createMap(source, destination);

		if (modifier != null) {
			// Building the unique name of the action
			String fieldActionOptionName = source.getName() + ":" + destination.getName();

			// Registering the action
			MappingObjectActions<Object, Object> actionOptions = new MappingObjectActions<>();

			shared.globalActionOptions.put(fieldActionOptionName, actionOptions);

			// Assing to object to be able to trick the compiler
			Object modifierAsObject = actionOptions;

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
		I1Fn<TypeSource, TypeDestination> behavior
	) {
		String uniqueName = from.getName() + ":" + to.getName();
		shared.tranformations.put(uniqueName, (I1Fn<Object, Object>) behavior);
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

	public void init() {
		// Adding all the default types
		DEFAULT_TYPES_RESOLVER.forEach((key, value) -> this.use(key, value));
	}
}
