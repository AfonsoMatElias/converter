package io.github.afonsomatelias.Mapper;

import static io.github.afonsomatelias.Helpers.FieldHelper.toMappedFields;
import static io.github.afonsomatelias.Helpers.Global.PRIMITIVES;
import static io.github.afonsomatelias.Helpers.Global.allMatch;
import static io.github.afonsomatelias.Helpers.Global.getListType;
import static io.github.afonsomatelias.Helpers.Global.isArray;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP2;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Configurations.MappingConfig;
import io.github.afonsomatelias.Enums.EMappingActions;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Mapper.Interfaces.IProcessor;
import io.github.afonsomatelias.Options.MappingActions;
import io.github.afonsomatelias.Options.MappingObjectActions;
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
		this.localActionOptions = new MappingActions();
		this.mappedObject = new HashMap<>();
	}

	/**
	 * Inheritance Constructor
	 * 
	 * @param parent the parent processor 
	 * @param source the source object
	 */
	public Processor(Processor<?> parent, S source) {
		this.mappedObject = parent.mappedObject;
		this.localActionOptions = parent.localActionOptions;
		this.shared = parent.shared;
		this.source = source;
	}

	/** Stores the {@link S} object */
	protected final S source;

	/**
	 * Stores all the shared public properties of the main {@link Converter} Class
	 */
	private final ConverterShared shared;

	/**
	 * Stores all the times that an object was mapped for to avoid mapping and object already mapped
	 */
	private final Map<String, Object> mappedObject;

	/** Action Controller for this Processor */
	protected final MappingActions localActionOptions;

	/** Collection types */
	protected enum ECollectionType {
		ARRAY, COLLECTION
	};
	
	
	/**
	 * Tries to create a new instance of a provided class
	 * 
	 * @param classToCreate 		the type of the field to instantiate
	 * @return the new instance of the destination type, or null if it fails
	 */
	private Object create(Class<?> classToCreate) {
		return this.create(classToCreate, "[Root]", classToCreate);
	}

	/**
	 * Tries to create a new instance of a provided class
	 * 
	 * @param classToCreate 		the type of the field to instantiate
	 * @param dstFieldName 			the name of the destination field
	 * @param dstClassType     		the class of the destination type
	 * @return the new instance of the destination type, or null if it fails
	 */
	private Object create(
		Class<?> classToCreate,
		String dstFieldName,
		Class<?> dstClassType
	) {
		try {
			return classToCreate.getConstructor().newInstance();
		} catch (Exception e) {
			final String cls = dstClassType == null ? "[Class]" : dstClassType.getName();
			final String clsFieldName = dstFieldName == null ? "[Field]" : dstFieldName;
			final String clsFieldType = classToCreate.getName();
			final String path = String.join(".", Arrays.asList(cls, "[" + clsFieldType + "]", clsFieldName ));

			$$.out("Error creating the destination type for " + path + ", try to use .addTransform(...) or .forMember(...) "
					+ "to intercept the member mapping, or .skip(...) in mapping options to ignore the field mapping. "+
					"\nException Details: " + e.getMessage() + "\n");
		}

		return null;
	}

	/**
	 * Maps properties from the source object to the destination object.
	 * 
	 * This function handles both direct field mappings and transformation
	 * mappings, while considering mapping configurations and actions.
	 * 
	 * @param objSource       	 the source object from which the properties
	 *                        	 are to be mapped
	 * @param clsObjDestination  the class type of the destination object
	 * @param objDestination  	 the destination object to which the properties
	 * 							 are to be mapped
	 * @return the mapped destination object, or null if mapping is not possible
	 */
	private Object mapObject(
		Object objSource,
		Class<?> clsObjDestination,
		Object objDestination
	) {
		if (objSource == null || objDestination == null)
			return null;

		// Beginning Mapping Process
		final Map<String, Field> fieldsSource = toMappedFields(objSource.getClass());
		final Map<String, Field> fieldsDestination = toMappedFields(clsObjDestination);
		final Map<String, MappingConfig> configurations = this.shared.configurations;

		// Helper Function to register Mapped Object
		final CallbackP1<Object, Object> fnRegisterMappedObj = (obj) -> {
			final String memoryAddress = obj.getClass().getSimpleName() 
					+ ":" + Integer.toHexString(System.identityHashCode(obj));

			// Getting the number of times that this object was mapped
			final Object mapped = mappedObject.getOrDefault(memoryAddress, null);

			if (mapped == null) {
				mappedObject.put(memoryAddress, objDestination);
				return null;
			}

			// Adding the number of mapping of an object
			return mapped;
		};

		// Sets a value to a field
		final CallbackV2<Field, Object> fnSetDstValue = (field, value) -> {
			try {
				if (field == null) return;
                field.setAccessible(true);
				field.set(objDestination, value);
			} catch (Exception e) {
				$$.out("Error setting value '"+ value +"' to field: " + field.getName(), e);
			}
		};

		final CallbackP2<Object, Field, Object> fnGetValue = (obj, field) -> {
			try {
				if (field == null) return null;
                field.setAccessible(true);
				return field.get(obj);
			} catch (Exception e) {
				return null;
			}
		};
		
		final boolean hasDiffTypes = (objSource.getClass() != objDestination.getClass());

		if (hasDiffTypes && shared.USE_MAPPING_CONFIG) {
			// Assigning the default values for the mapping process
			Class<?> configClsSource = objSource.getClass();
			Class<?> configClsDestination = clsObjDestination;

			final MappingConfig config = configurations.get(configClsSource.getName());

			// Checking the configuration for this source
			if (config == null) {
				$$.err("No mapping configuration found to map: " + source.getClass().getName() + " to "
					+ objDestination.getClass().getName());
				return null;
			}

			// Retrieving the source and destination of the encountered source configuration
			configClsSource = config.getSource();
			configClsDestination = config.getDestination();

			// If there isn't, just ignore the mapping
			if ((configClsSource != objSource.getClass()) || (configClsDestination != clsObjDestination)) {
				$$.err("No mapping configuration found to map: " + source.getClass().getName() + " to "
					+ objDestination.getClass().getName());
				return null;
			}
		}

		{ // Generic Scope
			// Testing Transformation Mapping
			final Object $mutatedObject = this.mapTransform(objSource, objSource.getClass(), clsObjDestination);
			if ($mutatedObject != null)
				return $mutatedObject;
		}

		// building the unique name 
		final String mapActionUniqueName = objSource.getClass().getName() + ":" + clsObjDestination.getName();

		// Retrieving the action for this field
		final MappingObjectActions<Object, Object> createdMapActionOption = shared.globalActionOptions
				.getOrDefault(mapActionUniqueName, null);

		if (createdMapActionOption != null) // Performing BEFORE_MAP action
			createdMapActionOption.emit(EMappingActions.BEFORE_MAP, objSource, null);

		final Object mappedObject = fnRegisterMappedObj.call(objSource);
		if (mappedObject != null)
			return mappedObject;

		fieldsDestination.forEach((fieldName, fieldDestination) -> {
			final Class<?> fieldTypeDestination = fieldDestination.getType();
			
			// # Ignore this field if it was marked to be skipped
			if (localActionOptions.isSkipMember(fieldDestination) || localActionOptions.isSkipMember(fieldName))
				return;
			

			// # Skip if the type needs to me ignored
			final Boolean isSkipTypeGlobal = shared.classTypesToIgnore.contains(fieldTypeDestination.getName()) ||
					shared.classTypesToIgnore.contains(fieldTypeDestination.getSimpleName());

			final Boolean isSkipTypeInline = localActionOptions.isSkipType(fieldTypeDestination.getSimpleName()) ||
					localActionOptions.isSkipType(fieldTypeDestination);

			if (isSkipTypeGlobal || isSkipTypeInline)
				return;


			// Try to get for member mapping for this field
			final FieldMemberMapping forMemberMapping = shared.forMemberMapping.getOrDefault(fieldDestination, null);

			if (forMemberMapping != null) {
				final Object memberMappingResult = forMemberMapping.call(objSource, objDestination, this);
				fnSetDstValue.call(fieldDestination, memberMappingResult);
				return; // Breaking the process as the member is already mapped
			}


			final Field fieldSource = fieldsSource.getOrDefault(fieldName, null);
			if (fieldSource == null) return;

			final Class<?> fieldTypeSource = fieldSource.getType();
			final String fieldSourceName = fieldSource.getName();
			final Object fieldSourceValue = fnGetValue.call(objSource, fieldSource);
			final Object fieldDestinationValue = fnGetValue.call(objDestination, fieldDestination);


			Object valueToSet = fieldSourceValue;


			// # Applying field transformation
			final Object $mutatedObject = this.mapTransform(valueToSet, fieldTypeSource, fieldTypeDestination);


			// # if the fields are equals (same types), just set it, but skip `PersistentBag`
			if (
				(fieldTypeDestination == fieldTypeSource) && 
				(valueToSet != null) && 
				!(valueToSet.getClass().getSimpleName().equals("PersistentBag"))
			) {
				fnSetDstValue.call(fieldDestination, ($mutatedObject != null ? $mutatedObject : valueToSet));
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
				final ECollectionType collectionType = fieldDestination.getClass().getComponentType() == null
						? ECollectionType.COLLECTION
						: ECollectionType.ARRAY;

				valueToSet = this.mapList(
					fieldSourceName, 
					fieldSourceValue, 
					getListType(fieldDestination), 
					clsObjDestination, 
					collectionType
				);
			} else

			// Checking object types
			if (fieldTypeDestination != fieldTypeSource) {
				// if there is already an instance of the destination field, use it
				final Object $objDestination = fieldDestinationValue != null ? fieldDestinationValue : create(
					fieldTypeDestination,
					fieldName, 
					clsObjDestination
				);

				// Mapping the object and assigning the value
				valueToSet = this.mapObject(
					fieldSourceValue, 
					fieldTypeDestination, 
					$objDestination
				);
			}

			if (valueToSet == null) return;
			
			// Setting the value
			fnSetDstValue.call(fieldDestination, valueToSet);
		});


		// # Performing all the setter of this class
		final List<SetterMemberMapping> setters = shared.forSetterMemberMapping.getOrDefault(
			mapActionUniqueName,
			Arrays.asList()
		);

		for (SetterMemberMapping setterMemberMapping : setters) {
			setterMemberMapping.call(objSource, objDestination, this);
		}


		if (createdMapActionOption != null) // Performing AFTER_MAP action
			createdMapActionOption.emit(EMappingActions.AFTER_MAP, objSource, objDestination);


		// If all the fields are null, nullify the destination object
		if (allMatch(fieldsDestination.values().stream().collect(Collectors.toList()), (x) -> {
			try {
				return x.get(objDestination) == null;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				return true;
			}
		}) == true)
			return null;

		return objDestination;
	}

	/**
	 * Maps the list of {@link S} objects to the list of destination class
	 * provided
	 * 
	 * @param fieldName            	the field name to be mapped
	 * @param valueSource          	the source value
	 * @param fieldTypeDestination 	the destination type
	 * @param clsDestination       	the class of the destination
	 * @param collectionType  		the type of the destination list
	 * @return the mapped list
	 */
	private Object mapList(
		String fieldName,
		Object valueSource,
		Class<?> fieldTypeDestination,		
		Class<?> clsDestination,
		ECollectionType collectionType
	) {
		return this.mapList(
			fieldName, 
			valueSource, 
			fieldTypeDestination, 
			clsDestination, 
			collectionType, 
			(_0, _1) -> {},
			(_0, _1) -> {}
		);
	};

	/**
	 * Maps the list of {@link S} objects to the list of destination class
	 * provided
	 * 
	 * @param fieldName            	the field name to be mapped
	 * @param valueSource          	the source value
	 * @param fieldTypeDestination 	the destination type
	 * @param clsDestination       	the class of the destination
	 * @param collectionType  	   	the type of the destination list
	 * @param localBeforeEachMap   	the local before map callback
	 * @param localAfterEachMap    	the local after map callback
	 * @return the mapped list
	 */
	private Object mapList(
		String fieldName,
		Object valueSource,
		Class<?> fieldTypeDestination,		
		Class<?> clsDestination,
		ECollectionType collectionType,
		CallbackV2<Object, Object> localBeforeEachMap,
		CallbackV2<Object, Object> localAfterEachMap
	) {
		// Creating a new instance of a generic list
		final ArrayList<Object> mappedList = new ArrayList<Object>();

		// Looping them
		/**
		 * NOTE: this cast to Iterable<T> can throw an exception
		 */
		for (final Object sourceItem : (Iterable<Object>) valueSource) {
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
					"Converter detected that items of the field  '" + 
					sourceItem.getClass().getName() +"->"+ fieldName + 
					"' is also an Collection type. Prefere using forMember to map this field."
				);
			} else {
				dstItem = this.mapObject(
					sourceItem, 
					fieldTypeDestination, 
					create(
						fieldTypeDestination,
						fieldName, 
						clsDestination
					)
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
	 * Transforms the source value to the destination type using a predefined transformation callback.
	 * 
	 * This method constructs a transformation key based on the source and destination types
	 * and retrieves the corresponding transformation callback from shared transformations.
	 * If a callback exists, it applies the transformation to the source value.
	 * 
	 * @param valueSource the source value to be transformed
	 * @param fieldTypeSource the class type of the source field
	 * @param fieldTypeDestination the class type of the destination field
	 * @return the transformed value, or null if no transformation callback is found
	 */
	private Object mapTransform(
		Object valueSource,
		Class<?> fieldTypeSource,
		Class<?> fieldTypeDestination
	) {
		// Building the transformationName
		final String name = fieldTypeSource.getName() + ":" + fieldTypeDestination.getName();

		// Getting the transformation callback for this mapping
		final CallbackP1<Object, Object> transform = shared.tranformations
				.getOrDefault(name, null);

		// Checking if there is a transformation for these two properties
		if (transform == null)
			return null;

		return transform.call(valueSource);
	}
	
	/**
	 * Maps the source object to the destination object, this method is called
	 * internally by the framework.
	 * 
	 * @param objSource the source object to be mapped
	 * @param clsDestination the class of the destination
	 * @param objDestination the destination object
	 * @return the mapped object
	 */
	private <D> Object mapper(Object objSource, Class<?> clsDestination, Object objDestination) {

		if (objSource == null || objDestination == null)
			return null;

		// 1. List Mapping...
		if (isArray(objSource)) {
			final ECollectionType listType = objSource.getClass().getComponentType() == null 
				? ECollectionType.COLLECTION
				: ECollectionType.ARRAY;

			// # Running the root object as Array/List
			// Note: this code is only executed when the mapper is called with an array,
			// 	There is not any other scenerio that this code is executed.

			// So we can perfom the calling of beforeEach and afterEach
			return this.mapList(
				null, 
				objSource, 
				clsDestination, 
				clsDestination, 
				listType, 
				(src, dst) -> localActionOptions.emit(EMappingActions.BEFORE_EACH_MAP, src, dst),
				(src, dst) -> localActionOptions.emit(EMappingActions.AFTER_EACH_MAP, src, dst)
			);
		}

		// 2. Object Mapping...
		return this.mapObject(
			objSource, 
			clsDestination, 
			objDestination
		);
	}
	
	/**
	 * Maps the source object to the destination class provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the {@link D} instance mapped from the {@link S}
	 *         instance
	 */
	public <D> Object toDestination(Class<?> clazz) {
		if (this.source == null)
			return null;

		try {
			Object $destination = create(clazz);

			// Performs the BEFORE_MAP action if the modifier is set
			localActionOptions.emit(EMappingActions.BEFORE_MAP, source, null);

			$destination = this.mapper(this.source, clazz, $destination);

			// Performs the AFTER_MAP action if the modifier is set
			localActionOptions.emit(EMappingActions.AFTER_MAP, source, $destination);

			return $destination;
		} catch (Exception e) {
			$$.err(
					"Error whiling mapping the from '" + source.getClass().getName() + "' to '" + clazz.getName() + "'",
					"Error details: " + e.getMessage(),
					e);
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
	 * @param destination the destination object from which values are mapped to the source
	 * @return the source object with values mapped from the destination, or null if the source is null
	 */
	public <D> Object fromDestination(D destination) {
		if (this.source == null)
			return null;

		// Swapped the roles of each object
		final Object _source = destination;
		final Object _destination = source;

		try {
			// Performs the BEFORE_MAP action if the modifier is set
			localActionOptions.emit(EMappingActions.BEFORE_MAP, _source, null);

			this.mapper(_source, _destination.getClass(), _destination);

			// Performs the AFTER_MAP action if the modifier is set
			localActionOptions.emit(EMappingActions.AFTER_MAP, _source, _destination);
			return _destination;
		} catch (Exception e) {
			$$.err(
					"Error whiling mapping the from '" + _source.getClass().getName() + "' to '" + _destination.getClass().getName() + "'",
					"Error details: " + e.getMessage(), 
					e);
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
	@Override
	public <D extends S> D to() {
		if (this.source == null)
			return null;

		try {
			return (D) this.toDestination(source.getClass());
		} catch (Exception e) {
			$$.err(
					"Error whiling making a copy of '" + source.getClass().getName(),
					"Error details: " + e.getMessage(),
					e);
			return null;
		}
	}
}