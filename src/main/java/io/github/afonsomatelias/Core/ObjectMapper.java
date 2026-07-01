package io.github.afonsomatelias.Core;

import java.util.HashMap;
import java.util.Map;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Interfaces.IObjectMapper;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Options.MappingActions;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.Interfaces.IMappingObjectActions;

@SuppressWarnings("unchecked")
public class ObjectMapper<Entry> extends Mapper<Entry> implements IObjectMapper<Entry> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param entry the {@link Entry} object
	 */
	public ObjectMapper(
		Converter converter, 
		ConverterShared shared, 
		Object entry
	) {
		super(
			converter, 
			shared, 
			(Entry) entry, 
			new MappingActions(),
			new HashMap<String, Object>()
		);
	}

	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param entry the {@link Entry} object
	 */
	public ObjectMapper(
		Converter converter, 
		ConverterShared shared, 
		Object entry,
		MappingActions mappingActions,
		Map<String, Object> mappedObject
	) {
		super(
			converter, 
			shared, 
			(Entry) entry, 
			mappingActions,
			mappedObject
		);
	}

	/**
	 * Maps the source object to the destination class provided
	 * 
	 * @param <D>   	the {@link D} object type
	 * @param clazz 	the {@link D} class type
	 * @return the object Converted
	 */
	@Override
	public <D> D to(Class<D> clazz) {
		try {
			return (D) super.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Maps the {@link Entry} object to the destination class provided with a mapper modifier
	 * 
	 * @param <D>     	the {@link D} object type
	 * @param clazz    	the {@link D} class type
	 * @param modifier 	mapping options that will be applied on map
	 * @return the object Converted
	 */
	@Override
	public <D> D to(Class<D> clazz, I1Action<IMappingObjectActions<Entry, D>> modifier) {
		try {
			if (modifier != null) {
				MappingObjectActions<Entry, D> actions = new MappingObjectActions<>();
				modifier.call(actions); localActionOptions.merge(actions); actions = null;
			}
			return (D) super.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}

    /**
     * Creates a new instance of the source object with a different memory address,
     * applying mapping options if a modifier is provided.
     *
     * @param <D>     	the {@link D} object type which extends {@link Entry}
     * @param modifier	mapping options that will be applied on map
     * @return new object instance or null in case of an exception
     */
	@Override
	public <D extends Entry> Entry to(I1Action<IMappingObjectActions<Entry, D>> modifier) {
		try {
			if (modifier != null) {
				MappingObjectActions<Entry, D> actions = new MappingObjectActions<>();
				modifier.call(actions); localActionOptions.merge(actions); actions = null;
			}

			return (Entry) super.toDestination(entry.getClass());
		} catch (Exception e) {
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
	public <D extends Entry> D to() {
		if (this.entry == null)
			return null;

		try {
			return (D) super.toDestination(entry.getClass());
		} catch (Exception e) {
			$$.err(
				"Error whiling making a copy of '" + entry.getClass().getName(),
				"Error details: " + e.getMessage(),	e
			);
			return null;
		}
	}

	/**
	 * Maps or extracts values from the provided destination object back to the source object.
	 * 
	 * This method swaps the roles of the source and destination, effectively reversing the mapping process.
	 * 
	 * @param <D> 		the type of the destination object
	 * @param $source 	the destination object from which values are mapped to the source
	 * @return the source object with values mapped from the destination, or null if the destination is null
	 */
	@Override
	public <D> Entry from(D $source) {
		if ($source == null) {
			$$.out("Invalid destination object, it cannot be null.");
			return null;
		}

		try {
			return (Entry) this.fromDestination($source);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Maps or extracts values from the provided destination object back to the source object,
	 * with the option to apply a mapping modifier.
	 * 
	 * This method swaps the roles of the source and destination, effectively reversing the mapping process.
	 * It triggers BEFORE_MAP and AFTER_MAP actions if modifiers are set. The modifier allows additional
	 * actions or transformations to be applied during the mapping process.
	 * 
	 * @param <D> 		the type of the destination object
	 * @param $source 	the destination object from which values are mapped to the source
	 * @param modifier 	a callback allowing custom mapping actions to be applied
	 * @return the source object with values mapped from the destination, or null if the destination is null
	 */
	@Override
	public <D> Entry from(D $source, I1Action<IMappingObjectActions<D, Entry>> modifier) {
		if ($source == null) {
			$$.out("Invalid destination object, it cannot be null.");
			return null;
		}

		try {
			if (modifier != null) {
				MappingObjectActions<D, Entry> actions = new MappingObjectActions<>();
				modifier.call(actions); localActionOptions.merge(actions); actions = null;
			}

			return (Entry) super.fromDestination($source);
		} catch (Exception e) {
			return null;
		}
	}
}
