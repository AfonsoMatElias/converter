package io.github.afonsomatelias.Mapper;

import static io.github.afonsomatelias.Helpers.FieldHelper.fields;
import static io.github.afonsomatelias.Helpers.FieldHelper.toMappedFields;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP3;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP4;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Configurations.MapperConfig;
import io.github.afonsomatelias.Enums.MappingActionsEnum;
import io.github.afonsomatelias.Helpers.Printer;
import io.github.afonsomatelias.Mapper.Interfaces.IProcessor;
import io.github.afonsomatelias.Options.MappingActions;
import io.github.afonsomatelias.Options.MemberMapping.FieldMemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.SetterMemberMapping;

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

	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param source the {@link S} object
	 */
	public Processor(Processor<?> parent, S source) {
		this.mappedObject = parent.mappedObject;
		this.actionOptions = parent.actionOptions;
		this.shared = parent.shared;
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

			put(int.class, Integer.class);
			put(byte.class, Byte.class);
			put(char.class, Character.class);
			put(boolean.class, Boolean.class);
			put(double.class, Double.class);
			put(float.class, Float.class);
			put(long.class, Long.class);
			put(short.class, Short.class);
			put(void.class, Void.class);
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
	protected final S source;

	/**
	 * Stores all the shared public properties of the main {@link Converter} Class
	 */
	private final ConverterShared shared;

	/**
	 * Stores all the times that an object was mapped for to avoid Self Reference
	 * Cycle Mapping
	 */
	private Map<String, Object> mappedObject = new HashMap<>();

	/** Action Controller for this Processor */
	protected MappingActions<Object, Object> actionOptions = new MappingActions<>();

	protected enum ListTypeEnum {
		ARRAY,
		COLLECTION
	}

	class CreateInput {
		public Class<?> dstClazz;
		public String dstFieldName;
		public Class<?> fieldToInstance;
	}

	private <T> Boolean allMatch(List<T> source, CallbackP1<T, Boolean> callback) {
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
	private Object create(CreateInput input) {
		Class<?> clazz = input.fieldToInstance;

		try {
			return clazz.getConstructor().newInstance();
		} catch (Exception e) {
			final String cls = input.dstClazz == null ? "[Class]" : input.dstClazz.getName();
			final String clsFieldName = input.dstFieldName == null ? "[Field]" : input.dstFieldName;
			final String clsFieldType = clazz.getName();
			final String path = String.join(".", Arrays.asList(cls, clsFieldName + ":" + clsFieldType));

			Printer.err("Error creating the destination type for " + path + ", try to use .addTransform(...) or .forMember(...) "
				+ "to intercept the member mapping, or .skip(...) in mapping options to ignore the field mapping. Exception Details: "
				+ e.getMessage() + "\n");
		}

		return null;
	}

	/**
	 * Maps properties from the Source Object, to Destination Object
	 */
	private <D> Object mapper(Object source, Class<?> clsDestination, Object destination)
			throws IllegalArgumentException, IllegalAccessException {

		if (source == null || destination == null)
			return null;

		final Map<String, Field> fieldsDestination = toMappedFields(clsDestination);
		final Map<String, MapperConfig> configurations = this.shared.configurations;

		final boolean hasDiffTypes = (source.getClass() != destination.getClass());

		// Checking the class types
		if (hasDiffTypes && shared.USE_MAPPING_CONFIG) {
			Printer.err("No mapping configuration found to map: " + source.getClass().getName() + " to "
					+ destination.getClass().getName());
			return null;
		}

		// Helper Function to check if an object is an Array
		final CallbackP1<Object, Boolean> isArray = (in) -> (in.getClass().isArray() || (in instanceof List<?>));

		// Register Mapped Object
		final CallbackP1<Object, Object> registerMap = (obj) -> {

			final String memoryAddress = obj.getClass().getSimpleName() + ":"
					+ Integer.toHexString(System.identityHashCode(obj));

			// Getting the number of times that this object was mapped
			final Object mapped = mappedObject.getOrDefault(memoryAddress, null);

			if (mapped == null) {
				mappedObject.put(memoryAddress, destination);
				return null;
			}

			// Adding the number of mapping of an object
			return mapped;
		};

		// Helper Function that gets the type Argument of a List
		final CallbackP1<Field, Class<?>> getListType = (field) -> {
			Class<?> type = field.getType().getComponentType();

			if (type == null)
				type = (Class<?>) ((ParameterizedType) field.getGenericType())
						.getActualTypeArguments()[0];

			return type;
		};

		// Main Function to map an Object
		final CallbackP4<Object, Class<?>, Class<?>, String, Object> objMapper = (valueSource, fieldTypeSource,
				fieldTypeDestination, fieldName) -> {

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
				final Class<?> _configClsDestination = configClsDestination;
				// Mapping the object, and assigning the value to set in the property
				tReturn = this.mapper(valueSource, configClsDestination, create(new CreateInput() {
					{
						dstClazz = clsDestination;
						dstFieldName = fieldName;
						fieldToInstance = _configClsDestination;
					}
				}));
			} catch (Exception e) {
				Printer.err("Error while mapping Source: " + fieldTypeSource.getName() + "; to Destination: "
						+ fieldTypeDestination.getName(), e);
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
		final CallbackP4<Object, ListTypeEnum, Class<?>, String, Object> listMapper = (valueSource, destinationListType,
				fieldTypeDestination, fieldName) -> {

			// Creating a new instance of a generic list
			final ArrayList<Object> tReturn = new ArrayList<Object>();

			// Looping them
			/**
			 * NOTE: this cast to Iterable<T> can throw an exception
			 */
			for (Object item : (Iterable<Object>) valueSource) {
				try {
					// If the item is a primitive, just add, do not map
					if (PRIMITIVES.contains(item.getClass())) {
						tReturn.add(item);
						continue;
					}

					final Object result = this.mapper(item, fieldTypeDestination, create(new CreateInput() {
						{
							dstClazz = clsDestination;
							dstFieldName = fieldName;
							fieldToInstance = fieldTypeDestination;
						}
					}));
					if (result == null)
						continue;

					tReturn.add(result);
				} catch (Exception e) {
					Printer.err("Error while mapping to Destination: " + fieldTypeDestination.getName(), e);
				}
			}

			if (ListTypeEnum.ARRAY == destinationListType) {
				return tReturn.toArray(new Object[tReturn.size()]);
			}

			// assigning the value to set in the property
			return tReturn;
		};

		// Sets a value to a field
		final CallbackV2<Field, Object> fieldSetter = (field, value) -> {
			try {
				field.set(destination, value);
			} catch (Exception e) {
				Printer.err("Error setting value to field: " + field.getName(), e);
			}
		};

		{ // Generic Scope
			// Testing Transformation Mapping
			final Object transformResult = transformMapper.call(source, source.getClass(), clsDestination);
			if (transformResult != null)
				return transformResult;
		}

		final String mapActionUniqueName = source.getClass().getName() + ":" + clsDestination.getName();

		// Retrieving the action for this field
		final MappingActions<Object, Object> createdMapActionOption = shared.globalActionOptions
				.getOrDefault(mapActionUniqueName, null);

		if (createdMapActionOption != null) {
			// Performing BEFORE_MAP action
			createdMapActionOption.call(MappingActionsEnum.BEFORE_MAP, source, null);
			// Performing BEFORE_EACH_MAP action
			createdMapActionOption.call(MappingActionsEnum.BEFORE_EACH_MAP, source, null);
		}

		if (isArray.call(source)) {
			final ListTypeEnum listType = source.getClass().getComponentType() == null ? ListTypeEnum.COLLECTION
					: ListTypeEnum.ARRAY;

			return listMapper.call(source, listType, clsDestination, null);
		}

		final Object mappedObject = registerMap.call(source);
		if (mappedObject != null)
			return mappedObject;

		// Looping all the source fields
		fields(source, (fieldNameSource, fieldValueSource, fieldSource, fieldTypeSource) -> {
			Object valueToSet = fieldValueSource;

			if (actionOptions.isSkipMember(fieldSource) || actionOptions.isSkipMember(fieldNameSource))
				return;

			// Skip if the type needs to me ignored
			final Boolean isSkipTypeGlobal = shared.classTypesToIgnore.contains(fieldSource.getType().getName()) || 
				shared.classTypesToIgnore.contains(fieldSource.getType().getSimpleName());

			final Boolean isSkipTypeInline = actionOptions.isSkipType(fieldTypeSource.getSimpleName()) || 
				actionOptions.isSkipType(fieldTypeSource);

			if (isSkipTypeGlobal || isSkipTypeInline)
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
			final FieldMemberMapping forMemberMapping = shared.forMemberMapping.getOrDefault(fieldDestination, null);

			if (forMemberMapping != null) {
				Object memberMappingResult = forMemberMapping.call(source, destination, this);
				fieldSetter.call(fieldDestination, memberMappingResult);
				return; // Breaking the process as the member is already mapped
			}

			final Class<?> fieldTypeDestination = fieldDestination.getType();

			final Object transformationResult = transformMapper.call(fieldValueSource, fieldTypeSource,
					fieldTypeDestination);

			// if the fields are equals, just set it, but skip `PersistentBag`
			if ((fieldTypeDestination == fieldTypeSource)
					&& !(valueToSet.getClass().getSimpleName().equals("PersistentBag"))) {
				fieldSetter.call(fieldDestination, valueToSet);
				return;
			}

			// Checking if there is a transformation for these two properties and assign it
			// to the Value To Set
			if (transformationResult != null) {
				// Just some randon empty block as the setting is happening in the if expression
				valueToSet = transformationResult;
			} else
			// Checking object types
			if (fieldTypeDestination != fieldTypeSource) {
				// Mapping the object and assigning the value
				valueToSet = objMapper.call(fieldValueSource, fieldTypeSource, fieldTypeDestination, fieldNameSource);
			} else
			// Checking if the value is an array
			if (isArray.call(valueToSet)) {
				final ListTypeEnum destinationListType = fieldDestination.getClass().getComponentType() == null
						? ListTypeEnum.COLLECTION
						: ListTypeEnum.ARRAY;
				// Mapping the list and assigning the value
				valueToSet = listMapper.call(fieldValueSource, destinationListType, getListType.call(fieldDestination),
						fieldNameSource);
			}

			fieldSetter.call(fieldDestination, valueToSet);
		});

		// Performing all the setter of this class
		final List<SetterMemberMapping> setters = shared.forSetterMemberMapping.getOrDefault(mapActionUniqueName,
				Arrays.asList());
		for (SetterMemberMapping setterMemberMapping : setters) {
			setterMemberMapping.call(source, destination, this);
		}

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
	public <D> Object toDestination(Class<?> clazz) {
		if (this.source == null)
			return null;

		try {
			// Performs the BEFORE_MAP action if the modifier is set
			actionOptions.call(MappingActionsEnum.BEFORE_MAP, source, null);
			actionOptions.call(MappingActionsEnum.BEFORE_EACH_MAP, source, null);

			final Object $destination = this.mapper(this.source, clazz, create(new CreateInput() {
				{
					dstClazz = clazz;
					dstFieldName = "[Root]";
					fieldToInstance = clazz;
				}
			}));

			// Performs the AFTER_MAP action if the modifier is set
			actionOptions.call(MappingActionsEnum.AFTER_MAP, source, $destination);
			actionOptions.call(MappingActionsEnum.AFTER_EACH_MAP, source, $destination);

			return $destination;
		} catch (Exception e) {
			Printer.err(
					"Error whiling mapping the from '" + source.getClass().getName() + "' to '" + clazz.getName() + "'",
					"Error details: " + e.getMessage(),
					e);
			return null;
		}
	}

	public <D> Object fromDestination(D destination) {
		if (this.source == null)
			return null;

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
	public <D extends S> D to() {
		if (this.source == null)
			return null;

		try {
			return (D) this.toDestination(source.getClass());
		} catch (Exception e) {
			return null;
		}
	}
}