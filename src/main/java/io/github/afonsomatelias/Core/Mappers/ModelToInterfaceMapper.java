package io.github.afonsomatelias.Core.Mappers;

import static io.github.afonsomatelias.Helpers.Global.PRIMITIVES;
import static io.github.afonsomatelias.Helpers.Global.getListEnumType;
import static io.github.afonsomatelias.Helpers.Global.isArray;

import java.util.ArrayList;

import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Mapper;
import io.github.afonsomatelias.Core.Base.ProxyInterface;
import io.github.afonsomatelias.Enums.CollectionTypeEnum;
import io.github.afonsomatelias.Helpers.$$;

@SuppressWarnings("unchecked")
public class ModelToInterfaceMapper<Entry> extends Mapper<Entry> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param source the {@link Entry} object
	 */
	public ModelToInterfaceMapper(Mapper<Entry> mapper) {
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
		Class<?> fieldClassType
	) {
		if ($source == null)
			return null;

		Object $destination = new ProxyInterface<>(
			this.converter,
			this.shared, 
			$source, 
			fieldClassType
		).build();

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
		CollectionTypeEnum collectionType
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
				mappedList.add(sourceItem);
				continue;
			}

			Object dstItem = null;

			if (isArray(sourceItem)) {
				$$.out(
					"Converter detected that the item of the Collection Type field '" + 
					sourceItem.getClass().getName() +"->"+ fieldName + 
					"' is also an Collection type. Prefere using forMember to map this field."
				);
			} else {
				dstItem = this.mapObject(
					sourceItem, 
					fieldListType
				);
			}

			// Performing the map
			if (dstItem == null) continue;

			mappedList.add(dstItem);
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
	private <D> Object map(Object $source, Class<?> clsDestination) {

		if ($source == null)
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
				getListEnumType($source.getClass())
			);
		}

		// 2. Object Mapping...
		return this.mapObject(
			$source, 
			clsDestination
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

			Object $destination = this.map(this.entry, clazz);

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
