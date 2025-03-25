package io.github.afonsomatelias.Mapper;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackV1;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Mapper.Interfaces.IObjectProcessor;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.Interfaces.IMappingObjectActions;

@SuppressWarnings("unchecked")
public class ObjectProcessor<S> extends Processor<S> implements IObjectProcessor<S> {
	public ObjectProcessor(ConverterShared shared, Object source) {
		super(shared, (S) source);
	}

	/**
	 * Maps the source object to the destination class provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
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
	 * Maps the {@link S} object to the destination class provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	@Override
	public <D> D to(Class<D> clazz, CallbackV1<IMappingObjectActions<S, D>> modifier) {
		try {
			if (modifier != null) {
				MappingObjectActions<S, D> actions = new MappingObjectActions<>();
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
     * @param <D>      the {@link D} object type which extends {@link S}
     * @param modifier mapping options that will be applied on map
     * @return new object instance or null in case of an exception
     */
	@Override
	public <D extends S> S to(CallbackV1<IMappingObjectActions<S, D>> modifier) {
		try {
			if (modifier != null) {
				MappingObjectActions<S, D> actions = new MappingObjectActions<>();
				modifier.call(actions); localActionOptions.merge(actions); actions = null;
			}

			return (S) this.toDestination(source.getClass());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Maps or extracts values from the provided destination object back to the source object.
	 * 
	 * This method swaps the roles of the source and destination, effectively reversing the mapping process.
	 * 
	 * @param <D> the type of the destination object
	 * @param destination the destination object from which values are mapped to the source
	 * @return the source object with values mapped from the destination, or null if the destination is null
	 */
	@Override
	public <D> S from(D destination) {
		if (destination == null) {
			$$.out("Invalid destination object, it cannot be null.");
			return null;
		}

		try {
			return (S) super.fromDestination(destination);
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
	 * @param <D> the type of the destination object
	 * @param destination the destination object from which values are mapped to the source
	 * @param modifier a callback allowing custom mapping actions to be applied
	 * @return the source object with values mapped from the destination, or null if the destination is null
	 */
	@Override
	public <D> S from(D destination, CallbackV1<IMappingObjectActions<D, S>> modifier) {
		if (destination == null) {
			$$.out("Invalid destination object, it cannot be null.");
			return null;
		}

		try {
			if (modifier != null) {
				MappingObjectActions<D, S> actions = new MappingObjectActions<>();
				modifier.call(actions); localActionOptions.merge(actions); actions = null;
			}
			return (S) super.fromDestination(destination);
		} catch (Exception e) {
			return null;
		}
	}
}
