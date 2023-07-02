package io.java.Mapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.java.Converter;
import io.java.Callback.ICallbacks.I1Callback;
import io.java.Callback.ICallbacks.I2Callback;
import io.java.Callback.ICallbacks.I3Callback;
import io.java.Callback.ICallbacks.IV2Callback;
import io.java.Configurations.ConverterShared;
import io.java.Configurations.MapperConfig;
import io.java.Enums.MappingActionsEnum;
import io.java.Helpers.FieldHelper;
import io.java.Helpers.Printer;
import io.java.Mapper.Interfaces.IProcessor;
import io.java.Options.MappingActions;

@SuppressWarnings("unchecked")
public class Processor<Source> implements IProcessor<Source> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param source the {@link Source} object
	 */
	public Processor(ConverterShared shared, Source source) {
		this.shared = shared;
		this.source = source;
		this.actionOptions = new MappingActions<>();
	}

	/** Stores the {@link Source} object */
	private Source source;

	/**
	 * Stores all the shared public properties of the main {@link Converter} Class
	 */
	private ConverterShared shared;

	/**
	 * Stores all the times that an object was mapped for to avoid Self Reference
	 * Cycle Mapping
	 */
	private Map<Object, Integer> objectCycleMappingCounter = new HashMap<Object, Integer>();

	/** Action Controller for this Processor */
	protected MappingActions<Object, Object> actionOptions;

	/**
	 * Helper Function to check if an object is an Array
	 * 
	 * @param in the input object
	 * @return boolean value true/false
	 */
	private boolean isArray(Object in) {
		return (in.getClass().isArray() || (in instanceof List<?>));
	}

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
			Printer.err("Error creating the destination type", "Error details: " + e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Maps properties from the Source Object, to Destination Object
	 */
	private <Destination> Object mapper(Object source, Class<?> clsDestination, Object destination)
			throws IllegalArgumentException, IllegalAccessException {

		final Map<String, Field> fieldsDestination = FieldHelper.getMappedFieldsFor(clsDestination);
		final Map<String, MapperConfig> configurations = this.shared.configurations;

		final boolean hasDiffTypes = (source.getClass() != destination.getClass());

		// Checking the class types
		if (hasDiffTypes && shared.USE_MAPPING_CONFIG) {
			Printer.err("No mapping configuration found to map: " + source.getClass().getName() + " to "
					+ destination.getClass().getName());
			return null;
		}

		// Helper Function that gets the type Argument of a List
		final I1Callback<Field, Class<?>> getListType = (
				field) -> (Class<?>) ((ParameterizedType) field.getGenericType())
						.getActualTypeArguments()[0];

		// Main Function to map an Object
		final I3Callback<Object, Class<?>, Class<?>, Object> objMapper = (valueSource, fieldTypeSource,
				fieldTypeDestination) -> {

			// Getting the number of times that this object was mapped
			final int numberOfMapping = objectCycleMappingCounter.getOrDefault(valueSource, 0) + 1;

			// Adding the number of mapping of an object
			objectCycleMappingCounter.put(source, numberOfMapping);

			// If it is above the LIMIT defined, do not map
			if (numberOfMapping > shared.LIMIT_CYCLE_MAPPING)
				return null;

			Object tReturn = null;
			Class<?> configClsSource = null;
			Class<?> configClsDestination = null;

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

		// Main Function to map object using the mutation
		final I3Callback<Object, Class<?>, Class<?>, Object> mutationMapper = (valueSource, fieldTypeSource,
				fieldTypeDestination) -> {
			// Building the MutationName
			final String mutationName = fieldTypeSource.getName() + ":"
					+ fieldTypeDestination.getName();

			// Getting the mutation for this mapping
			final I1Callback<Object, Object> mutation = shared.mutations
					.getOrDefault(mutationName, null);

			// Checking if there is a mutation for these two properties
			if (mutation == null)
				return null;

			return mutation.call(valueSource);
		};

		// Function to map a List Of Object
		final I2Callback<Object, Class<?>, Object> listMapper = (value, fieldType) -> {
			// Creating a new instance of a generic list
			ArrayList<Object> tReturn = (ArrayList<Object>) create(new ArrayList<Object>().getClass());

			// Looping them
			for (Object item : (List<Object>) value) {
				try {
					Object result = this.mapper(item, fieldType, create(fieldType));
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
		final IV2Callback<Field, Object> fieldSetter = (field, value) -> {
			try {
				field.set(destination, value);
			} catch (Exception e) {
				Printer.err(e);
			}
		};

		{ // Generic Scope

			// Testing Mutation Mapping
			Object mutationResult = mutationMapper.call(source, source.getClass(), clsDestination);
			if (mutationResult != null)
				return mutationResult;
		}

		if (isArray(source)) {
			return listMapper.call(source, clsDestination);
		}

		String createdMapActionOptionName = source.getClass().getName() + ":" + clsDestination.getName();

		// Retrieving the action for this field
		MappingActions<Object, Object> createdMapActionOption = shared.globalActionOptions
				.getOrDefault(createdMapActionOptionName, null);

		if (createdMapActionOption != null)
			// Performing BEFORE_MAP action
			createdMapActionOption.call(MappingActionsEnum.BEFORE_MAP, source, null);

		// Looping all the source fields
		FieldHelper.fields(source, (fieldNameSource, fieldValueSource, fieldTypeSource) -> {
			Object valueToSet = fieldValueSource;

			// If there is no value set in the property just ignore
			if (valueToSet == null)
				return;

			// Getting the equivalent field in the destination Fields
			final Field fieldDestination = fieldsDestination.getOrDefault(fieldNameSource, null);

			// Just ignore if the field is was not found
			if (fieldDestination == null)
				return;

			// Try to get for member mapping for this field
			final I1Callback<Object, Object> forMemberMapping = shared.forMemberMapping.getOrDefault(fieldDestination,
					null);

			if (forMemberMapping != null) {
				fieldSetter.call(fieldDestination, forMemberMapping.call(source));
				return; // Breaking the process as the member is already mapped
			}

			final Class<?> fieldTypeDestination = fieldDestination.getType();

			Object mutationResult = mutationMapper.call(fieldValueSource, fieldTypeSource, fieldTypeDestination);
			// Checking if there is a mutation for these two properties and assign it to the
			// Value To Set
			if (mutationResult != null) {
				// Just some randon empty block as the setting is happening in the if expression
				valueToSet = mutationResult;
			} else
			// Checking object types
			if (fieldTypeDestination != fieldTypeSource) {
				// Mapping the object and assigning the value
				valueToSet = objMapper.call(fieldValueSource, fieldTypeSource, fieldTypeDestination);
			} else
			// Checking if the value is an array
			if (isArray(valueToSet)) {
				// Mapping the list and assigning the value
				valueToSet = listMapper.call(fieldValueSource, getListType.call(fieldDestination));
			}

			fieldSetter.call(fieldDestination, valueToSet);
		});

		if (createdMapActionOption != null)
			// Performing AFTER_MAP action
			createdMapActionOption.call(MappingActionsEnum.AFTER_MAP, source, destination);

		return destination;
	}

	/**
	 * The main point of mapping
	 * 
	 * @param <Destination> the {@link Destination} Type
	 * @param clazz         the {@link Class} of the destination object
	 * @param modifier      a modifier for the mapping process
	 * @return the {@link Destination} instance mapped from the {@link Source}
	 *         instance
	 */
	protected <Destination> Object toDestination(Class<?> clazz) {
		try {
			// Performs the BEFORE_MAP action if the modifier is set
			actionOptions.call(MappingActionsEnum.BEFORE_MAP, source, null);

			Object result = this.mapper(this.source, clazz, this.create(clazz));

			// Performs the AFTER_MAP action if the modifier is set
			actionOptions.call(MappingActionsEnum.AFTER_MAP, source, result);

			return result;
		} catch (Exception e) {
			Printer.err(
					"Error whiling mapping the from '" + source.getClass().getName() + "' to '" + clazz.getName() + "'",
					"Error details: " + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Creates a new instance of object provided, just like copy and paste with
	 * different memory address
	 * 
	 * @param <Destination> the {@link Destination} Type
	 * @return new object instance
	 */
	public Source build() {
		try {
			return (Source) this.toDestination(source.getClass());
		} catch (Exception e) {
			return null;
		}
	}
}
