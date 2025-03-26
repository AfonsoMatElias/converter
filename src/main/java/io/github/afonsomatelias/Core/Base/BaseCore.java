package io.github.afonsomatelias.Core.Base;

import java.util.Arrays;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Helpers.$$;
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

	/**
	 * Tries to create a new instance of a provided class
	 * 
	 * @param classToCreate 		the type of the field to instantiate
	 * @return the new instance of the destination type, or null if it fails
	 */
	protected Object create(Class<?> classToCreate) {
		return this.create(classToCreate, "[Root]", classToCreate);
	}

	/**
	 * Tries to create a new instance of a provided class
	 * 
	 * @param clazz 				the type of the field to instantiate
	 * @param fieldName 			the name of the destination field
	 * @param fieldParentType     	the class of the destination type
	 * @return the new instance of the destination type, or null if it fails
	 */
	protected Object create(
		Class<?> clazz,
		String fieldName,
		Class<?> fieldParentType
	) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (Exception e) {
			final String cls = fieldParentType == null ? "[Class]" : fieldParentType.getName();
			final String clsFieldName = fieldName == null ? "[Field]" : fieldName;
			final String clsFieldType = clazz.getSimpleName();
			final String path = String.join(":", Arrays.asList(cls, "[" + clsFieldType + "]", clsFieldName ));

			$$.out("Error creating the destination type for " + path + ", try to use .addTransform(...) or .forMember(...) "
					+ "to intercept the member mapping, or .skip(...) in mapping options to ignore the field mapping. "+
					"\nException Details: " + e.getMessage() + "\n");
		}

		return null;
	}

}
