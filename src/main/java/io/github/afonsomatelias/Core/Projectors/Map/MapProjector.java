package io.github.afonsomatelias.Core.Projectors.Map;

import static io.github.afonsomatelias.Helpers.FieldHelper.toMappedFields;
import static io.github.afonsomatelias.Helpers.Global.PRIMITIVES;
import static io.github.afonsomatelias.Helpers.Global.allMatch;
import static io.github.afonsomatelias.Helpers.Global.getListEnumType;
import static io.github.afonsomatelias.Helpers.Global.getListType;
import static io.github.afonsomatelias.Helpers.Global.isArray;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Callback.ICallbacks.ITypeResolver;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Base.BaseCore;
import io.github.afonsomatelias.Core.Base.TypeFactory;
import io.github.afonsomatelias.Enums.ECollectionType;
import io.github.afonsomatelias.Enums.EMappingActions;
import io.github.afonsomatelias.Helpers.$$;

@SuppressWarnings("unchecked")
public class MapProjector<Entry> extends BaseCore<Entry> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param source the {@link Entry} object
	 */
	public MapProjector(ConverterShared shared, Entry source) {
		super(shared, source);
	}

	/**
	 * Maps properties from the source map to the destination object.
	 * 
	 * This function handles both primitive and non-primitive fields, including
	 * arrays and maps, by checking their types and performing necessary
	 * transformations. It also considers mapping configurations and actions,
	 * such as skipping fields or types, and resolving values using type resolvers
	 * when necessary.
	 * 
	 * @param $source          		the source map with field names and values to be mapped
	 * @param fieldClassType   		the class type of the destination object
	 * @param $destination     		the destination object to which the properties
	 *                         		are to be mapped
	 * @return the mapped destination object, or null if mapping is not possible
	 */
	private Object mapObject(
		Map<String, ? extends Object> $source,
		Class<?> fieldClassType,
		Object $destination
	) {
		if ($source == null || $destination == null)
			return null;

		// Beginning Mapping Process
		Map<String, Field> fieldsDestination = toMappedFields(fieldClassType);

		// Sets a value to a field
		I2Action<Field, Object> fnSetDstValue = (field, value) -> {
			try {
				if (field == null) return;
                field.setAccessible(true);
				field.set($destination, value);
			} catch (Exception e) {
				$$.out("Error setting value '"+ value +"' to field: " + field.getName(), e);
			}
		};

		// Looping all the source fields
		$source.forEach((fieldName, fieldValue) -> {

			Field fieldDestination = fieldsDestination.getOrDefault(fieldName, null);
			if (fieldDestination == null) return;
			Class<?> fieldTypeDestination = fieldDestination.getType();

			// # Ignore this field if it was marked to be skipped
			if (localActionOptions.isSkipMember(fieldDestination) || localActionOptions.isSkipMember(fieldName))
				return;

			// # Skip if the type needs to me ignored
			boolean isSkipTypeInline = localActionOptions.isSkipType(fieldTypeDestination.getSimpleName()) ||
					localActionOptions.isSkipType(fieldTypeDestination);

			if (isSkipTypeInline)
				return;
			
			// Assigning default the value
			Object valueToSet = fieldValue;

			// Checking if the source field is a primitive
			if (!PRIMITIVES.contains(fieldValue.getClass())) {
				// Checking if the source field is an Array
				if (isArray(fieldValue)) {
					
					// Checking if the destintation field is also an Array
					if (Collection.class.isAssignableFrom(fieldTypeDestination)) {
						// Mapping the fieldValue as List and setting the value
						valueToSet = this.mapList(
							fieldName, 
							fieldValue, 
							getListType(fieldDestination),
							fieldTypeDestination,
							getListEnumType(fieldDestination.getClass())
						);
					}
				
				} else
				// Checking if the source field is a Map
				if ((fieldValue instanceof Map<?, ?>)) {
					// Mapping the fieldValue as Object and setting the value
					valueToSet = this.mapObject(
						(Map<String, ? extends Object>) fieldValue,
						fieldTypeDestination, 
						TypeFactory.create(fieldTypeDestination, fieldName, fieldTypeDestination)
					);
				}
			}

			// Checking if the value to set is null
			if (valueToSet == null) return;

			// Getting the value type
			Class<?> valueToSetType = valueToSet.getClass();

			// Checking if the value to set is not assignable to the destination field and the destination field is a String
			if (!fieldTypeDestination.isAssignableFrom(valueToSetType)) {
				// Getting the field type resolver
				ITypeResolver resolver =  shared.typeResolvers.getOrDefault(fieldTypeDestination, null);

				// if there isn't a resolver, alert and return
				if (resolver == null) {
					$$.err("No resolver found for type: " + fieldTypeDestination.getName());
					return;
				}

				try {
					// Resolving the value
					valueToSet = resolver.resolve(valueToSet);
				} catch (Exception e) {
					// Can not set java.time.LocalDate field io.github.afonsomatelias.Models.UserDto.bithdate to java.lang.String
					$$.out("Error parsing value '" + valueToSet + "' to field '" + 
					String.join(":", Arrays.asList(
						fieldClassType.getName(),
						fieldTypeDestination.getSimpleName(),
						fieldName
					)), "Try to use a CustomTypeResolver, or config.use(Type.class, (value) -> { ... }) to add a custom resolver.", e);	

					return;
				}
			}
			
			// Setting the value
			fnSetDstValue.call(fieldDestination, valueToSet);
		});

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
	 * @param collectionType  		the type of the destination list
	 * @return the mapped list
	 */
	private Object mapList(
		String fieldName,
		Object $source,
		Class<?> fieldListType,		
		Class<?> fieldParentType,
		ECollectionType collectionType
	) {
		I2Action<Object, Object> fnEmpty = (_0, _1) -> {}; 
		return this.mapList(
			fieldName, 
			$source, 
			fieldListType, 
			fieldParentType, 
			collectionType, 
			fnEmpty,
			fnEmpty
		);
	};

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
		ECollectionType collectionType,
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
					(Map<String, ?>) sourceItem, 
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

		if (ECollectionType.ARRAY == collectionType) {
			return mappedList.toArray(new Object[mappedList.size()]);
		}

		// assigning the value to set in the property
		return mappedList;
	}

	/**
	 * Maps the source object to the destination object, this method is called
	 * internally by the framework.
	 * 
	 * @param $source 			the source object to be mapped
	 * @param clsDestination 	the class of the destination
	 * @param $destination 		the destination object
	 * @return the mapped object
	 */
	private <D> Object mapper(Object $source, Class<?> clsDestination, Object $destination) {

		if ($source == null || $destination == null)
			return null;

		// 1. List Mapping...
		if (isArray($source)) {
			// # Running the root object as Array/List
			// Note: this code is only executed when the mapper is called with an array,
			// There is not any other scenerio that this code is executed.

			// So we can perfom the calling of beforeEach and afterEach
			return this.mapList(
				null, 
				$source, 
				clsDestination, 
				clsDestination, 
				getListEnumType($source.getClass()), 
				(src, dst) -> localActionOptions.emit(EMappingActions.BEFORE_EACH_MAP, src, dst),
				(src, dst) -> localActionOptions.emit(EMappingActions.AFTER_EACH_MAP, src, dst)
			);
		}

		// 2. Object Mapping...
		return this.mapObject(
			(Map<String, ?>) $source, 
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
	public <D> Object toDestination(Class<?> clazz) {
		if (this.entry == null)
			return null;

		try {
			Object $destination = TypeFactory.create(clazz);

			// Performs the BEFORE_MAP action if the modifier is set
			localActionOptions.emit(EMappingActions.BEFORE_MAP, entry, null);

			$destination = this.mapper(this.entry, clazz, $destination);

			// Performs the AFTER_MAP action if the modifier is set
			localActionOptions.emit(EMappingActions.AFTER_MAP, entry, $destination);

			return $destination;
		} catch (Exception e) {
			$$.err(
				"Error whiling mapping the from '" + entry.getClass().getName() + "' to '" + clazz.getName() + "'",
				"Error details: " + e.getMessage(), e
			);
			return null;
		}
	}
	
}