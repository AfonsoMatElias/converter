package io.github.afonsomatelias.Core.Mappers;

import static io.github.afonsomatelias.Helpers.FieldHelper.toMappedFields;
import static io.github.afonsomatelias.Helpers.Global.PRIMITIVES;
import static io.github.afonsomatelias.Helpers.Global.allMatch;
import static io.github.afonsomatelias.Helpers.Global.getListEnumType;
import static io.github.afonsomatelias.Helpers.Global.isArray;
import static io.github.afonsomatelias.Helpers.Global.toLower1Char;
import static io.github.afonsomatelias.Helpers.MethodHelper.toMappedMethods;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Callback.ICallbacks.ITypeResolver;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Mapper;
import io.github.afonsomatelias.Core.Base.TypeFactory;
import io.github.afonsomatelias.Enums.CollectionTypeEnum;
import io.github.afonsomatelias.Enums.MappingActionsEnum;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Options.Expression.MemberConfigExpression;
import io.github.afonsomatelias.Options.MemberMapping.FieldMemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.MethodMemberMapping;

@SuppressWarnings("unchecked")
public class InterfaceToModelMapper<Entry> extends Mapper<Entry> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param source the {@link Entry} object
	 */
	public InterfaceToModelMapper(Mapper<Entry> mapper) {
		super(
			mapper.getConverter(), 
			mapper.getShared(), 
			mapper.getEntry(), 
			mapper.getLocalActionOptions(),
			mapper.getMappedObject()
		);
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
		Object $source,
		Class<?> fieldClassType,
		Object $destination
	) {
		if ($source == null || $destination == null)
			return null;

		// Beginning Mapping Process
		Map<String, Field> fieldsDestination = toMappedFields(fieldClassType);
		Map<String, Method> methodsDestination = toMappedMethods($destination.getClass());

		Map<String, Method> methodsSource = toMappedMethods($source.getClass());

		// Gets a value to a field
		I1Fn<Method, Object> fnGetMethodValue = (method) -> {
			try {
				if (method == null) return null;
				method.setAccessible(true);
				return method.invoke($source, new Object[] {});
			} catch (Exception e) {
				$$.err("Error retrieving value from method: '"+ method.getName(), e);
				return null;
			}
		};

		// Sets a value to a field
		I2Action<Method, Object> fnSetMethodValue = (method, value) -> {
			try {
				if (method == null) return;
				method.setAccessible(true);
				method.invoke($destination, new Object[] { value });
			} catch (Exception e) {
				$$.err("Error setting value '"+ value +"' to field: " + method.getName(), e);
			}
		};

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

		methodsSource.forEach((getMethodName, getMethod) -> {
			String methodName = getMethodName.substring(3);

			// Getting the destination 'set' method
			Method setMethod = methodsDestination.getOrDefault( "set" + methodName, null);

			// if there is no destination 'set' method, just skip
			if (setMethod == null) return;

			// Getting the name of the destination 'set' method
			String setMethodName = setMethod.getName();

			// Getting the parameter type: set<fieldName>(<paramType>)
			Class<?> setMethodParamType = setMethod.getParameterTypes().length > 0 
				? setMethod.getParameterTypes()[0] 
				: null;

			// if there it no parameter type in this method, just skip
			if (setMethodParamType == null) {
				$$.out("The method '"+ setMethod.getName() +"' has no parameters, skipping processing method '"+ 
				getMethodName +"' to method: " + setMethod.getName());
				return;
			}

			// # Ignore this field if it was marked to be skipped
			if (localActionOptions.isSkipMember(setMethodName))
				return;

			// # Skip if the type needs to me ignored
			if (localActionOptions.isSkipType(setMethodParamType))
				return;


			MethodMemberMapping methodMemberMapping = methodsMemberMapping.getOrDefault(setMethod, null);
			if (methodMemberMapping != null) {
					methodMemberMapping.call(
					$source, 
					$destination, 
					new MemberConfigExpression(this)
				);
				return; // Breaking the process as the member is already mapped
			}

			final String mMethodName = toLower1Char(methodName);
			Field fieldDestination = fieldsDestination.getOrDefault(mMethodName, null);
			if (fieldDestination != null) {
				// Try to get for member mapping for this field
				FieldMemberMapping forMemberMapping = fieldsMemberMapping.getOrDefault(fieldDestination, null);
				if (forMemberMapping != null) {
					Object memberMappingResult = forMemberMapping.call(
						$source, 
						$destination, 
						new MemberConfigExpression(this)
					);
					fnSetMethodValue.call(setMethod, memberMappingResult);
					return; // Breaking the process as the member is already mapped
				}
			}
			
			// Getting value from source method | projection
			Object getMethodValue = fnGetMethodValue.call(getMethod);
			if (getMethodValue == null) return;

			// Getting the return type of the 
			Class<?> getMethodReturnType = getMethod.getReturnType();
			
			// Assigning default the value
			Object valueToSet = getMethodValue;

			// Checking if the source field is a primitive
			if (!PRIMITIVES.contains(getMethodReturnType)) {
				// Checking if the source field is an Array
				if (isArray(getMethodValue)) { /* There is no array mapping */ } 
				// Otherwise, use object mapping
				else {
					// Mapping the fieldValue as Object and setting the value
					valueToSet = this.mapObject(
						getMethodValue,
						setMethodParamType, 
						TypeFactory.create(
							setMethodParamType, 
							getMethodName, 
							$source.getClass()
						)
					);
				}
			}

			// Checking if the value to set is null
			if (valueToSet == null) return;

			// Checking if the value to set is not assignable to the destination field and the destination field is a String
			if (!setMethodParamType.isAssignableFrom(getMethodReturnType)) {
				// Getting the field type resolver
				ITypeResolver resolver =  shared.typeResolvers.getOrDefault(setMethodParamType, null);

				// if there isn't a resolver, alert and return
				if (resolver == null) {
					$$.err("No resolver found for type: " + setMethodParamType.getName());
					return;
				}

				try {
					// Resolving the value
					valueToSet = resolver.resolve(valueToSet);
				} catch (Exception e) {
					// Can not set java.time.LocalDate field io.github.afonsomatelias.Models.UserDto.bithdate to java.lang.String
					String msg = new StringBuilder()
						.append("Cant not set value '" + valueToSet + "' to method '")
						.append(fieldClassType.getName())
						.append("->")
						.append(setMethodParamType.getSimpleName())
						.append(" ")
						.append(setMethodName)
						.toString();

					$$.err(msg, "Try to use a CustomTypeResolver, or config.use(Type.class, (value) -> { ... }) to add a custom resolver.", e);	
					return;
				}
			}
			
			// Setting the value
			fnSetMethodValue.call(setMethod, valueToSet);
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
	 * @param $source 			the source object to be mapped
	 * @param clsDestination 	the class of the destination
	 * @param $destination 		the destination object
	 * @return the mapped object
	 */
	private <D> Object map(Object $source, Class<?> clsDestination, Object $destination) {

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
}