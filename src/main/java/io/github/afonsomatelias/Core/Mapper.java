package io.github.afonsomatelias.Core;


import static io.github.afonsomatelias.Helpers.Global.getListEnumType;
import static io.github.afonsomatelias.Helpers.Global.isAnInterface;
import static io.github.afonsomatelias.Helpers.Global.isArray;

import java.util.ArrayList;
import java.util.Map;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Base.BaseCore;
import io.github.afonsomatelias.Core.Mappers.ComunMapper;
import io.github.afonsomatelias.Core.Mappers.HashMapToModelMapper;
import io.github.afonsomatelias.Core.Mappers.InterfaceToModelMapper;
import io.github.afonsomatelias.Core.Mappers.ModelToInterfaceMapper;
import io.github.afonsomatelias.Enums.CollectionTypeEnum;
import io.github.afonsomatelias.Options.MappingActions;

public abstract class Mapper<Entry> extends BaseCore<Entry> {	
	/**
	 * Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param entry the {@link Entry} object
	 */
	public Mapper(
		Converter converter, 
		ConverterShared shared, 
		Entry entry, 
		MappingActions localActionOptions,
		Map<String, Object> mappedObject
	) {
		super(converter, shared, entry, localActionOptions, mappedObject);
	}

	@SuppressWarnings("unchecked")
	protected Object toDestination(Class<?> destinationClass) {
		Entry source = entry;

		if (source == null)
			return null;

		Class<?> sourceClass = null;

		// If it is an array
		if (isArray(source)) {
			// Get the first element
			Object firstItem = null;

			try {
				for (Object sourceItem : (Iterable<Object>) source)
					if ((firstItem = sourceItem) != null) break;
			} catch (Exception e) {}

			// If it is null, it means that it's empty
			if (firstItem == null) {
				return getListEnumType(source.getClass()) == CollectionTypeEnum.ARRAY 
					? new Object[0] 
					: new ArrayList<>();
			}

			sourceClass = firstItem != null ? firstItem.getClass() : source.getClass();
		} else {
			// Otherwise, get the default class 
			sourceClass = source.getClass();
		}

		// HashMapToModelClassMapper -> Project
		if (source instanceof Map<?, ?> && (destinationClass instanceof Object)) {
			return new HashMapToModelMapper<Entry>(this)
				.mapToDestination(destinationClass);
		}
		
		// InterfaceToModelMapper -> Project
		if (
			isAnInterface(sourceClass) && (destinationClass instanceof Object)
		) {
			return new InterfaceToModelMapper<Entry>(this)
				.mapToDestination(destinationClass);
		}
		
		// ModelToInterfaceMapper -> project
		if (source instanceof Object && isAnInterface(destinationClass)) {
			return new ModelToInterfaceMapper<Entry>(this)
				.mapToDestination(destinationClass);
		}

		// Map to Default
		return new ComunMapper<Entry>(this)
		.mapToDestination(destinationClass);
	}

	protected Object fromDestination(Object $source) {
		return new ComunMapper<>(this)
					.mapFromDestination($source);
	}
}
