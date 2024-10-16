package io.github.afonsomatelias.Mapper;

import static io.github.afonsomatelias.Helpers.FieldHelper.fields;
import static io.github.afonsomatelias.Helpers.FieldHelper.toMappedFields;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP2;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP3;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Configurations.MapperConfig;
import io.github.afonsomatelias.Enums.MappingActionsEnum;
import io.github.afonsomatelias.Helpers.Printer;
import io.github.afonsomatelias.Mapper.Interfaces.IProcessor;
import io.github.afonsomatelias.Options.MappingActions;

@SuppressWarnings("unchecked")
public class Processor<S> implements IProcessor<S> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param source the {@link S} object
	 */
	public Processor(ConverterShared shared, S source) {
		this.shared = shared;
		this.source = source;
	}

	public final Map<Class<?>, Class<?>> PRIMITIVE_MAPPER = new HashMap<Class<?>, Class<?>>() {
		{
			put(Integer.class, int.class);
			put(Byte.class, byte.class);
			put(String.class, String.class);
			put(Character.class, char.class);
			put(Boolean.class, boolean.class);
			put(Double.class, double.class);
			put(Float.class, float.class);
			put(Long.class, long.class);
			put(Short.class, short.class);
			put(Void.class, void.class);
		}
	};

	public final Set<Class<?>> PRIMITIVES = new HashSet<Class<?>>() {
		{
			add(Integer.class);
			add(Byte.class);
			add(String.class);
			add(Character.class);
			add(Boolean.class);
			add(Double.class);
			add(Float.class);
			add(Long.class);
			add(Short.class);
			add(Void.class);

			add(int.class);
			add(byte.class);
			add(char.class);
			add(boolean.class);
			add(double.class);
			add(float.class);
			add(long.class);
			add(short.class);
			add(void.class);
		}
	};

	/** Stores the {@link S} object */
	private final S source;

	/**
	 * Stores all the shared public properties of the main {@link Converter} Class
	 */
	private final ConverterShared shared;

	/**
	 * Stores all the times that an object was mapped for to avoid Self Reference
	 * Cycle Mapping
	 */
	private final Map<String, Integer> objectCycleMappingCounter = new HashMap<>();

	/** Action Controller for this Processor */
	protected final MappingActions<Object, Object> actionOptions = new MappingActions<>();

	<T> Boolean allMatch(List<T> source, CallbackP1<T, Boolean> callback) {
		List<Boolean> allMatching = new ArrayList<>();

		for (T t : source)
			allMatching.add(callback.call(t));

		return allMatching.isEmpty() ? false : allMatching.stream().allMatch(x -> x);
	};

	/**
	 * Creates a new instance of the provided class
	 * 
	 * @param clazz the class to create instace of
	 * @return the instance object
	 */
	private Object create(Class<?> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (Exception e) {
			Printer.err(
					"Error creating the destination type '" + clazz.getName()
							+ "', try to use .addTransform(...) or .forMember(...) to intercept the member mapping",
					"Error details: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Maps properties from the Source Object, to Destination Object
	 */
	private <D> Object mapper(Object source, Class<?> clsDestination, Object destination)
			throws IllegalArgumentException, IllegalAccessException {

		final Map<String, Field> fieldsDestination = toMappedFields(clsDestination);
		final Map<String, MapperConfig> configurations = this.shared.configurations;

		if (source == null || destination == null)
			return null;

		final boolean hasDiffTypes = (source.getClass() != destination.getClass());

		// Checking the class types
		if (hasDiffTypes && shared.USE_MAPPING_CONFIG) {
			Printer.err("No mapping configuration found to map: " + source.getClass().getName() + " to "
					+ destination.getClass().getName());
			return null;
		}

		// Helper Function to check if an object is an Array
		final CallbackP1<Object, Boolean> isArray = (in) -> (in.getClass().isArray() || (in instanceof List<?>));

		// Checks if an object is reached the limit o cycle
		final CallbackP1<Object, Boolean> isObjInLimitCycle = (obj) -> {
			final String memoryAddress = Integer.toHexString(obj.hashCode());

			// Getting the number of times that this object was mapped
			final int numberOfMapping = objectCycleMappingCounter.getOrDefault(memoryAddress, 0) + 1;

			// Adding the number of mapping of an object
			objectCycleMappingCounter.put(memoryAddress, numberOfMapping);

			// If it is above the LIMIT defined, do not map
			if (numberOfMapping > shared.LIMIT_CYCLE_MAPPING)
				return true;

			return false;
		};

		// Helper Function that gets the type Argument of a List
		final CallbackP1<Field, Class<?>> getListType = (
				field) -> (Class<?>) ((ParameterizedType) field.getGenericType())
						.getActualTypeArguments()[0];

		// Main Function to map an Object
		final CallbackP3<Object, Class<?>, Class<?>, Object> objMapper = (valueSource, fieldTypeSource,
				fieldTypeDestination) -> {

			Object tReturn = null;
			Class<?> configClsSource = null;
			Class<?> configClsDestination = null;

			if (isObjInLimitCycle.call(valueSource))
				return tReturn;

			if (shared.USE_MAPPING_CONFIG) {
				// Checking the configuration for this source
				if (!configurations.containsKey(fieldTypeSource.getName())) {
					Printer.err("No mapping configuration found to map: " + source.getClass().getName() + " to "
							+ destination.getClass().getName());
					return null;
				}

				// Retrieving the source and destination of the encountered source configuration
				configClsSource = configurations.get(fieldTypeSource.getName()).getSource();
				configClsDestination = configurations.get(fieldTypeSource.getName()).getDestination();

				// If there isn't, just ignore the mapping
				if ((configClsSource != fieldTypeSource) || (configClsDestination != fieldTypeDestination)) {
					return null;
				}
			} else {
				configClsSource = valueSource.getClass();
				configClsDestination = fieldTypeDestination;
			}

			try {
				// Mapping the object, and assigning the value to set in the property
				tReturn = this.mapper(valueSource, configClsDestination, create(configClsDestination));
			} catch (Exception e) {
				Printer.err(e);
			}

			return tReturn;
		};

		// Main Function to map object using the tranformation
		final CallbackP3<Object, Class<?>, Class<?>, Object> transformMapper = (valueSource, fieldTypeSource,
				fieldTypeDestination) -> {
			// Building the transformationName
			final String name = new StringBuilder().append(fieldTypeSource.getName()).append(":")
					.append(fieldTypeDestination.getName()).toString();

			// Getting the transformation callback for this mapping
			final CallbackP1<Object, Object> transform = shared.tranformations
					.getOrDefault(name, null);

			// Checking if there is a transformation for these two properties
			if (transform == null)
				return null;

			return transform.call(valueSource);
		};

		// Function to map a List Of Object
		final CallbackP2<Object, Class<?>, Object> listMapper = (valueSource, fieldType) -> {
			// Creating a new instance of a generic list
			final ArrayList<Object> tReturn = new ArrayList<Object>();

			if (isObjInLimitCycle.call(valueSource))
				return tReturn;

			// Looping them
			/**
			 * NOTE: this cast to Iterable<T> can throw an exception
			 */
			for (Object item : (Iterable<Object>) valueSource) {
				try {
					final Object result = this.mapper(item, fieldType, create(fieldType));
					if (result == null)
						break;
					tReturn.add(result);
				} catch (Exception e) {
					Printer.err(e);
				}
			}

			// assigning the value to set in the property
			return tReturn;
		};

		// Sets a value to a field
		final CallbackV2<Field, Object> fieldSetter = (field, value) -> {
			try {
				field.set(destination, value);
			} catch (Exception e) {
				Printer.err(e);
			}
		};

		{ // Generic Scope
			// Testing Transformation Mapping
			final Object transformResult = transformMapper.call(source, source.getClass(), clsDestination);
			if (transformResult != null)
				return transformResult;
		}

		final String createdMapActionOptionName = source.getClass().getName() + ":" + clsDestination.getName();

		// Retrieving the action for this field
		final MappingActions<Object, Object> createdMapActionOption = shared.globalActionOptions
				.getOrDefault(createdMapActionOptionName, null);

		if (createdMapActionOption != null) {
			// Performing BEFORE_MAP action
			createdMapActionOption.call(MappingActionsEnum.BEFORE_MAP, source, null);
			// Performing BEFORE_EACH_MAP action
			createdMapActionOption.call(MappingActionsEnum.BEFORE_EACH_MAP, source, null);
		}

		if (isArray.call(source)) {
			return listMapper.call(source, clsDestination);
		}

		// Looping all the source fields
		fields(source, (fieldNameSource, fieldValueSource, field, fieldTypeSource) -> {
			Object valueToSet = fieldValueSource;

			if (actionOptions.isSkipMember(field) || actionOptions.isSkipMember(fieldNameSource))
				return;

			// If there is no value set in the property just ignore
			if (valueToSet == null)
				return;

			// Getting the equivalent field in the destination Fields
			final Field fieldDestination = fieldsDestination.getOrDefault(fieldNameSource, null);

			// Just ignore if the field is was not found
			if (fieldDestination == null)
				return;

			// Try to get for member mapping for this field
			final CallbackP1<Object, Object> forMemberMapping = shared.forMemberMapping.getOrDefault(fieldDestination,
					null);

			if (forMemberMapping != null) {
				fieldSetter.call(fieldDestination, forMemberMapping.call(source));
				return; // Breaking the process as the member is already mapped
			}

			final Class<?> fieldTypeDestination = fieldDestination.getType();

			final Object transformationResult = transformMapper.call(fieldValueSource, fieldTypeSource,
					fieldTypeDestination);

			// Checking if there is a transformation for these two properties and assign it
			// to the Value To Set
			if (transformationResult != null) {
				// Just some randon empty block as the setting is happening in the if expression
				valueToSet = transformationResult;
			} else
			// Checking object types
			if (fieldTypeDestination != fieldTypeSource) {
				// Mapping the object and assigning the value
				valueToSet = objMapper.call(fieldValueSource, fieldTypeSource, fieldTypeDestination);
			} else
			// Checking if the value is an array
			if (isArray.call(valueToSet)) {
				// Mapping the list and assigning the value
				valueToSet = listMapper.call(fieldValueSource, getListType.call(fieldDestination));
			}

			fieldSetter.call(fieldDestination, valueToSet);
		});

		if (createdMapActionOption != null) {
			// Performing AFTER_MAP action
			createdMapActionOption.call(MappingActionsEnum.AFTER_MAP, source, destination);
			createdMapActionOption.call(MappingActionsEnum.AFTER_EACH_MAP, source, destination);
		}

		// If all the fields are null, nullify the destination object
		if (allMatch(fieldsDestination.values().stream().collect(Collectors.toList()), (x) -> {
			try {
				return x.get(destination) == null;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return true;
			}
		}) == true)
			return null;

		return destination;
	}

	/**
	 * The main point of mapping
	 * 
	 * @param <D>      the {@link D} Type
	 * @param clazz    the {@link Class} of the destination object
	 * @param modifier a modifier for the mapping process
	 * @return the {@link D} instance mapped from the {@link S}
	 *         instance
	 */
	protected <D> Object toDestination(Class<?> clazz) {
		try {
			// Performs the BEFORE_MAP action if the modifier is set
			actionOptions.call(MappingActionsEnum.BEFORE_MAP, source, null);
			actionOptions.call(MappingActionsEnum.BEFORE_EACH_MAP, source, null);

			final Object $destination = this.mapper(this.source, clazz, this.create(clazz));

			// Performs the AFTER_MAP action if the modifier is set
			actionOptions.call(MappingActionsEnum.AFTER_MAP, source, $destination);
			actionOptions.call(MappingActionsEnum.AFTER_EACH_MAP, source, $destination);

			return $destination;
		} catch (Exception e) {
			Printer.err(
					"Error whiling mapping the from '" + source.getClass().getName() + "' to '" + clazz.getName() + "'",
					"Error details: " + e.getMessage(), e);
			return null;
		}
	}

	protected <D> Object fromDestination(D destination) {
		// Swapped the roles of each object
		final Object _source = destination;
		final Object _destination = source;

		try {
			// Performs the BEFORE_MAP action if the modifier is set
			actionOptions.call(MappingActionsEnum.BEFORE_MAP, _source, null);
			actionOptions.call(MappingActionsEnum.BEFORE_EACH_MAP, _source, null);

			this.mapper(_source, _destination.getClass(), _destination);

			// Performs the AFTER_MAP action if the modifier is set
			actionOptions.call(MappingActionsEnum.AFTER_MAP, _source, _destination);
			actionOptions.call(MappingActionsEnum.AFTER_EACH_MAP, _source, _destination);

			return _destination;
		} catch (Exception e) {
			Printer.err(
					"Error whiling mapping the from '" + _source.getClass().getName() + "' to '"
							+ _destination.getClass().getName() + "'",
					"Error details: " + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Creates a new instance of object provided, just like copy and paste with
	 * different memory address
	 * 
	 * @return new object instance
	 */
	@Override
	public S to() {
		try {
			return (S) this.toDestination(source.getClass());
		} catch (Exception e) {
			return null;
		}
	}
}
