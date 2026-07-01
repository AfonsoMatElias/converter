package io.github.afonsomatelias.Core.Base;


import java.util.Map;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Options.MappingActions;

public abstract class BaseCore<Entry> {
	/**
	 * Inheritance Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param entry the {@link Entry} object
	 */
	public BaseCore(
		Converter converter, 
		ConverterShared shared, 
		Entry entry, 
		MappingActions localActionOptions,
		Map<String, Object> mappedObject
	) {
		this.converter = converter;
		this.shared = shared;
		this.entry = entry;
		this.localActionOptions = localActionOptions;
		this.mappedObject = mappedObject;
	}

	/**
	 * The current converter instance
	 */
	protected final Converter converter;

	public Converter getConverter() {
		return converter;
	}


	/**
	 * Stores all the shared public properties of the main {@link Converter} Class
	 */
	protected final ConverterShared shared;

	public ConverterShared getShared() {
		return shared;
	}


	/** The Source Object */
	protected final Entry entry;

	public Entry getEntry() {
		return entry;
	}


	/** Action Controller for this Processor */
	protected final MappingActions localActionOptions;

	public MappingActions getLocalActionOptions() {
		return localActionOptions;
	}


	/**
	 * Stores all the times that an object was mapped for to avoid mapping and object already mapped
	 */
	protected final Map<String, Object> mappedObject;

	public Map<String, Object> getMappedObject() {
		return mappedObject;
	}
}
