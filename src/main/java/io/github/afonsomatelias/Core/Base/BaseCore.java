package io.github.afonsomatelias.Core.Base;


import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Options.MappingActions;

public abstract class BaseCore<Entry> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param entry the {@link Entry} object
	 */
	public BaseCore(ConverterShared shared, Entry entry) {
		this.shared = shared;
		this.entry = entry;
		this.localActionOptions = new MappingActions();
	}

	/**
	 * Inheritance Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param entry the {@link Entry} object
	 */
	public BaseCore(ConverterShared shared, Entry entry, MappingActions localActionOptions) {
		this.shared = shared;
		this.entry = entry;
		this.localActionOptions = localActionOptions;
	}

	/**
	 * Stores all the shared public properties of the main {@link Converter} Class
	 */
	protected final ConverterShared shared;

	/** The Source Object */
	protected final Entry entry;

	/** Action Controller for this Processor */
	protected final MappingActions localActionOptions;
}
