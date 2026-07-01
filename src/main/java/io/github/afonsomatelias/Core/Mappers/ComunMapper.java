package io.github.afonsomatelias.Core.Mappers;

import static io.github.afonsomatelias.Helpers.FieldHelper.toMappedFields;
import static io.github.afonsomatelias.Helpers.Global.PRIMITIVES;
import static io.github.afonsomatelias.Helpers.Global.allMatch;
import static io.github.afonsomatelias.Helpers.Global.getListEnumType;
import static io.github.afonsomatelias.Helpers.Global.getListType;
import static io.github.afonsomatelias.Helpers.Global.isArray;
import static io.github.afonsomatelias.Helpers.Global.toUpper1Char;
import static io.github.afonsomatelias.Helpers.MethodHelper.toMappedMethods;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Callback.ICallbacks.I2Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I3Fn;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Configurations.MappingConfig;
import io.github.afonsomatelias.Core.Mapper;
import io.github.afonsomatelias.Core.Base.TypeFactory;
import io.github.afonsomatelias.Enums.CollectionTypeEnum;
import io.github.afonsomatelias.Enums.MappingActionsEnum;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.Expression.MemberConfigExpression;
import io.github.afonsomatelias.Options.MemberMapping.FieldMemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.MethodMemberMapping;

@SuppressWarnings("unchecked")
public class ComunMapper<Entry> extends Mapper<Entry> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param entry the {@link Entry} object
	 */
	public ComunMapper(Mapper<Entry> mapper) {
		super(
			mapper.getConverter(), 
			mapper.getShared(), 
			mapper.getEntry(), 
			mapper.getLocalActionOptions(),
			mapper.getMappedObject()
		);
	}

	private HashSet<String> DEFAULT_SKIP_TYPE_NAME = new HashSet<>(Arrays.asList(
		"PersistentBag"
	));

	/**
	 * Maps properties from the source object to the destination object.
	 * 
	 * This function handles both direct field mappings and transformation
	 * mappings, while considering mapping configurations and actions.
	 * 
	 * @param $source       	 the source object from which the properties
	 *                        	 are to be mapped
	 * @param fieldClassType  	 the class type of the destination object
	 * @param $destination  	 the destination object to which the properties
	 * 							 are to be mapped
	 * @return the mapped destination object, or null if mapping is not possible
	 */
	private Object mapObject(
		Object $source,
		Class<?> fieldClassType,
		Object $destination
	) {
		if ($source == null || $destination == null)
			return null;

		// Beginning Mapping Process
		Map<String, Field> fieldsSource = toMappedFields($source.getClass());
		Map<String, Field> fieldsDestination = toMappedFields(fieldClassType);
		Map<String, Method> methodsDestination = toMappedMethods($destination.getClass());

		Map<String, MappingConfig> configurations = this.shared.configurations;

		// Helper Function to register Mapped Object
		I1Fn<Object, Object> fnRegisterMappedObj = (obj) -> {
			String memoryAddress = obj.getClass().getSimpleName() 
					+ ":" + Integer.toHexString(System.identityHashCode(obj));

			// Getting the number of times that this object was mapped
			Object mapped = mappedObject.getOrDefault(memoryAddress, null);

			if (mapped == null) {
				mappedObject.put(memoryAddress, $destination);
				return null;
			}

			// Adding the number of mapping of an object
			return mapped;
		};

		/**
		 * Transforms the source value to the destination type using a predefined transformation callback.
		 * 
		 * This method constructs a transformation key based on the source and destination types
		 * and retrieves the corresponding transformation callback from shared transformations.
		 * If a callback exists, it applies the transformation to the source value.
		 */
		I3Fn<Object, Class<?>, Class<?>, Object> fnMapTransform = (
			Object _source,
			Class<?> fieldTypeSource,
			Class<?> fieldTypeDestination
		) -> {
			// Building the transformationName
			String name = fieldTypeSource.getName() + ":" + fieldTypeDestination.getName();

			// Getting the transformation callback for this mapping
			I1Fn<Object, Object> transform = shared.tranformations
					.getOrDefault(name, null);

			// Checking if there is a transformation for these two properties
			if (transform == null)
				return null;

			return transform.call(_source);
		};

		// Sets a value to a field
		I2Action<Field, Object> fnSetFieldValue = (field, value) -> {
			try {
				if (field == null) return;
                field.setAccessible(true);
				field.set($destination, value);
			} catch (Exception e) {
				$$.err("Error setting value '"+ value +"' to field: " + field.getName(), e);
			}
		};

		I2Fn<Object, Field, Object> fnGetFieldValue = (obj, field) -> {
			try {
				if (field == null) return null;
                field.setAccessible(true);
				return field.get(obj);
			} catch (Exception e) {
				return null;
			}
		};
		
		boolean hasDiffTypes = ($source.getClass() != $destination.getClass());

		if (hasDiffTypes && shared.USE_MAPPING_CONFIG) {
			// Assigning the default values for the mapping process
			Class<?> configClsSource = $source.getClass();
			Class<?> configClsDestination = fieldClassType;

			MappingConfig config = configurations.get(configClsSource.getName());

			// Checking the configuration for this source
			if (config == null) {
				$$.err("No mapping configuration found to map: " + entry.getClass().getName() + " to "
					+ $destination.getClass().getName());
				return null;
			}

			// Retrieving the source and destination of the encountered source configuration
			configClsSource = config.getSource();
			configClsDestination = config.getDestination();

			// If there isn't, just ignore the mapping
			if ((configClsSource != $source.getClass()) || (configClsDestination != fieldClassType)) {
				$$.err("No mapping configuration found to map: " + entry.getClass().getName() + " to "
					+ $destination.getClass().getName());
				return null;
			}
		}

		{ // Generic Scope
			// Testing Transformation Mapping
			Object $mutatedObject = fnMapTransform.call($source, $source.getClass(), fieldClassType);
			if ($mutatedObject != null)
				return $mutatedObject;
		}

		// building the unique name 
		String mapActionUniqueName = $source.getClass().getName() + ":" + fieldClassType.getName();

		// # Retrieve all the fields member mapping
		Map<Field, FieldMemberMapping> fieldsMemberMapping = shared.forMemberFieldMapping.getOrDefault(
			mapActionUniqueName,
			new HashMap<>()
		);

		// # Retrieve all the methods member mapping
		Map<Method, MethodMemberMapping> methodsMemberMapping = shared.forMemberMethodMapping.getOrDefault(
			mapActionUniqueName,
			new HashMap<>()
		);

		// Retrieving the action for this field
		MappingObjectActions<Object, Object> createdMapActionOption = shared.globalActionOptions
				.getOrDefault(mapActionUniqueName, null);

		if (createdMapActionOption != null) // Performing BEFORE_MAP action
			createdMapActionOption.emit(MappingActionsEnum.BEFORE_MAP, $source, null);

		Object mappedObject = fnRegisterMappedObj.call($source);
		if (mappedObject != null)
			return mappedObject;

		fieldsDestination.forEach((fieldName, fieldDestination) -> {
			Class<?> fieldTypeDestination = fieldDestination.getType();
			
			// # Ignore this field if it was marked to be skipped
			if (localActionOptions.isSkipMember(fieldDestination) || localActionOptions.isSkipMember(fieldName))
				return;
			

			// # Skip if the type needs to me ignored
			boolean isSkipTypeGlobal = shared.classTypesToIgnore.contains(fieldTypeDestination.getName()) ||
					shared.classTypesToIgnore.contains(fieldTypeDestination.getSimpleName());

			boolean isSkipTypeInline = localActionOptions.isSkipType(fieldTypeDestination.getSimpleName()) ||
					localActionOptions.isSkipType(fieldTypeDestination);

			if (isSkipTypeGlobal || isSkipTypeInline)
				return;


			// Try to get for member mapping for this field
			FieldMemberMapping forMemberMapping = fieldsMemberMapping.getOrDefault(fieldDestination, null);
			if (forMemberMapping != null) {
				Object memberMappingResult = forMemberMapping.call(
					$source, 
					$destination, 
					new MemberConfigExpression(this)
				);
				fnSetFieldValue.call(fieldDestination, memberMappingResult);
				return; // Breaking the process as the member is already mapped
			}

			// Goal: name to Name
			final String mFielName = toUpper1Char(fieldName);
			Method methodDestination = 
				// Try to find the set method
				methodsDestination.getOrDefault("set" + mFielName, 
					// Otherwise, find the get method
					methodsDestination.getOrDefault("get" + mFielName, null)
			);

			if (methodDestination != null) {
				MethodMemberMapping methodMemberMapping = methodsMemberMapping.getOrDefault(
					methodDestination, null
				);
				if (methodMemberMapping != null) {
					Object memberMappingResult = methodMemberMapping.call(
						$source, 
						$destination, 
						new MemberConfigExpression(this)
					);
					fnSetFieldValue.call(fieldDestination, memberMappingResult);
					return; // Breaking the process as the member is already mapped
				}
			}


			Field fieldSource = fieldsSource.getOrDefault(fieldName, null);
			if (fieldSource == null) return;

			Class<?> fieldTypeSource = fieldSource.getType();
			String fieldSourceName = fieldSource.getName();
			Object fieldSourceValue = fnGetFieldValue.call($source, fieldSource);
			Object fieldDestinationValue = fnGetFieldValue.call($destination, fieldDestination);


			Object valueToSet = fieldSourceValue;


			// # Applying field transformation
			Object $mutatedObject = fnMapTransform.call(valueToSet, fieldTypeSource, fieldTypeDestination);


			// # if the fields are equals (same types), just set it, but skip it if matches DEFAULT_SKIP_TYPE_NAME
			if (
				(fieldTypeDestination == fieldTypeSource) && 
				(valueToSet != null) && 
				!(DEFAULT_SKIP_TYPE_NAME.contains(valueToSet.getClass().getSimpleName()))
			) {
				fnSetFieldValue.call(fieldDestination, ($mutatedObject != null ? $mutatedObject : valueToSet));
				return;
			}


			// Checking if there is a transformation for these two properties and assign it
			// to the Value To Set
			if ($mutatedObject != null) {
				// Just some randon empty block as the setting is happening in the if expression
				valueToSet = $mutatedObject;
			} else
			
			// Checking if the value is an array
			if (isArray(valueToSet)) {
				valueToSet = this.mapList(
					fieldSourceName, 
					fieldSourceValue, 
					getListType(fieldDestination), 
					fieldClassType, 
					getListEnumType(fieldDestination.getClass()),
					(_0, _1) -> {},
					(_0, _1) -> {}
				);
			} else

			// Checking object types
			if (fieldTypeDestination != fieldTypeSource) {
				// if there is already an instance of the destination field, use it
				Object $objDestination = fieldDestinationValue != null 
					? fieldDestinationValue 
					: TypeFactory.create(fieldTypeDestination, fieldName, fieldClassType);

				// Mapping the object and assigning the value
				valueToSet = this.mapObject(fieldSourceValue, fieldTypeDestination, $objDestination);
			}

			if (valueToSet == null) return;
			
			// Setting the value
			fnSetFieldValue.call(fieldDestination, valueToSet);
		});

		if (createdMapActionOption != null) // Performing AFTER_MAP action
			createdMapActionOption.emit(MappingActionsEnum.AFTER_MAP, $source, $destination);


		// If all the fields are null, nullify the destination object
		if (allMatch(fieldsDestination.values().stream().collect(Collectors.toList()), (x) -> {
			try {
				return x.get($destination) == null;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return true;
			}
		}) == true)
			return null;

		return $destination;
	}

	/**
	 * Maps the list of {@link Entry} objects to the list of destination class
	 * provided
	 * 
	 * @param fieldName            	the field name to be mapped
	 * @param $source          		the source value
	 * @param fieldListType 		the destination type
	 * @param fieldParentType       the class of the destination
	 * @param collectionType  	   	the type of the destination list
	 * @param localBeforeEachMap   	the local before map callback
	 * @param localAfterEachMap    	the local after map callback
	 * @return the mapped list
	 */
	private Object mapList(
		String fieldName,
		Object $source,
		Class<?> fieldListType,		
		Class<?> fieldParentType,
		CollectionTypeEnum collectionType,
		I2Action<Object, Object> localBeforeEachMap,
		I2Action<Object, Object> localAfterEachMap
	) {
		// Creating a new instance of a generic list
		ArrayList<Object> mappedList = new ArrayList<Object>();

		// Looping them
		/**
		 * NOTE: this cast to Iterable<T> can throw an exception
		 */
		for (Object sourceItem : (Iterable<Object>) $source) {
			// If the item is a primitive, just add, do not map
			if (PRIMITIVES.contains(sourceItem.getClass())) {
				// Calling beforeEachMap
				localBeforeEachMap.call(sourceItem, null);

				mappedList.add(sourceItem);

				// Calling beforeEachMap
				localAfterEachMap.call(sourceItem, sourceItem);
				continue;
			}

			Object dstItem = null;

			// Calling beforeMap
			localBeforeEachMap.call(sourceItem, null);

			if (isArray(sourceItem)) {
				$$.out(
					"Converter detected that the item of the Collection Type field '" + 
					sourceItem.getClass().getName() +"->"+ fieldName + 
					"' is also an Collection type. Prefere using forMember to map this field."
				);
			} else {
				dstItem = this.mapObject(
					sourceItem, 
					fieldListType, 
					TypeFactory.create(fieldListType, fieldName, fieldParentType)
				);
			}

			// Performing the map
			if (dstItem == null) continue;

			mappedList.add(dstItem);

			// Calling afterEachMap
			localAfterEachMap.call(sourceItem, dstItem);
		}

		if (CollectionTypeEnum.ARRAY == collectionType) {
			return mappedList.toArray(new Object[mappedList.size()]);
		}

		// assigning the value to set in the property
		return mappedList;
	}
	
	/**
	 * Maps the source object to the destination object, this method is called
	 * internally by the framework.
	 * 
	 * @param $source the source object to be mapped
	 * @param clsDestination the class of the destination
	 * @param $destination the destination object
	 * @return the mapped object
	 */
	private <D> Object map(Object $source, Class<?> clsDestination, Object $destination) {

		if ($source == null || $destination == null)
			return null;

		// 1. List Mapping...
		if (isArray($source)) {
			// # Running the root object as Array/List
			// Note: this code is only executed when the mapper is called with an array,
			// 	There is not any other scenerio that this code is executed.

			// So we can perfom the calling of beforeEach and afterEach
			return this.mapList(
				null, 
				$source, 
				clsDestination, 
				clsDestination, 
				getListEnumType($source.getClass()), 
				(src, dst) -> localActionOptions.emit(MappingActionsEnum.BEFORE_EACH_MAP, src, dst),
				(src, dst) -> localActionOptions.emit(MappingActionsEnum.AFTER_EACH_MAP, src, dst)
			);
		}

		// 2. Object Mapping...
		return this.mapObject(
			$source, 
			clsDestination, 
			$destination
		);
	}

	/**
	 * Maps the source object to the destination class provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the {@link D} instance mapped from the {@link Entry}
	 *         instance
	 */
	public <D> Object mapToDestination(Class<?> clazz) {
		try {
			Object $destination = TypeFactory.create(clazz);

			// Performs the BEFORE_MAP action if the modifier is set
			localActionOptions.emit(MappingActionsEnum.BEFORE_MAP, entry, null);

			$destination = this.map(this.entry, clazz, $destination);

			// Performs the AFTER_MAP action if the modifier is set
			localActionOptions.emit(MappingActionsEnum.AFTER_MAP, entry, $destination);

			return $destination;
		} catch (Exception e) {
			$$.err(
				"Error whiling mapping the from '" + entry.getClass().getName() + "' to '" + clazz.getName() + "'",
				"Error details: " + e.getMessage(), e
			);
			return null;
		}
	}

	/**
	 * Maps or extracts values from the provided destination object back to the source object.
	 * 
	 * This method swaps the roles of the source and destination, effectively reversing the mapping process.
	 * It triggers BEFORE_MAP and AFTER_MAP actions if modifiers are set.
	 * 
	 * @param <D> the type of the destination object
	 * @param $source the destination object from which values are mapped to the source
	 * @return the source object with values mapped from the destination, or null if the source is null
	 */
	public <D> Object mapFromDestination(D $source) {
		if (this.entry == null)
			return null;

		// Swapped the roles of each object
		Object _source = $source;
		Object _destination = entry;

		try {
			// Performs the BEFORE_MAP action if the modifier is set
			localActionOptions.emit(MappingActionsEnum.BEFORE_MAP, _source, null);

			this.map(_source, _destination.getClass(), _destination);

			// Performs the AFTER_MAP action if the modifier is set
			localActionOptions.emit(MappingActionsEnum.AFTER_MAP, _source, _destination);
			return _destination;
		} catch (Exception e) {
			$$.err(
				"Error whiling mapping the from '" + _source.getClass().getName() + "' to '" + _destination.getClass().getName() + "'",
				"Error details: " + e.getMessage(), e
			);
			return null;
		}
	}

	/**
	 * Creates a new instance of the source object, effectively acting like a 
	 * copy operation but with a different memory address.
	 * 
	 * @param <D> the type of the object extending the source type
	 * @return a new instance of the source object, or null if the source is null
	 */
	public <D extends Entry> D mapToDestination() {
		if (this.entry == null)
			return null;

		try {
			return (D) this.mapToDestination(entry.getClass());
		} catch (Exception e) {
			$$.err(
				"Error whiling making a copy of '" + entry.getClass().getName(),
				"Error details: " + e.getMessage(),	e
			);
			return null;
		}
	}
}