package io.github.afonsomatelias.Mapper;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackV1;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Helpers.Printer;
import io.github.afonsomatelias.Mapper.Interfaces.IObjectProcessor;
import io.github.afonsomatelias.Options.Interfaces.IMappingActions;
import io.github.afonsomatelias.Options.MappingActions;

@SuppressWarnings("unchecked")
public class ObjectProcessor<S> extends Processor<S> implements IObjectProcessor<S> {
	public ObjectProcessor(ConverterShared shared, Object source) {
		super(shared, (S) source);
	}

	/**
	 * Converts the {@link S} object to the destination class provided
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
	 * Converts the {@link S} object to the destination class provided with a
	 * mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	@Override
	public <D> D to(Class<D> clazz, CallbackV1<IMappingActions<S, D>> modifier) {
		try {
			if (modifier != null) {
				// Assing to object to be able to trick the compiler
				Object modifierAsObject = actionOptions;

				modifier.call((MappingActions<S, D>) modifierAsObject);
			}
			return (D) super.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Maps or Extracts values from the destination to the source
	 * 
	 * @param <D> the {@link D} object type
	 * @return the object Converted
	 */
	@Override
	public <D> S from(D destination) {
		if (destination == null) {
			Printer.out("Invalid destination object, it cannot be null.");
			return null;
		}

		try {
			return (S) super.fromDestination(destination);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Maps or Extracts values from the destination to the source
	 * with a
	 * mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	@Override
	public <D> S from(D destination, CallbackV1<IMappingActions<S, D>> modifier) {
		if (destination == null) {
			Printer.out("Invalid destination object, it cannot be null.");
			return null;
		}

		try {

			if (modifier != null) {
				// Assing to object to be able to trick the compiler
				Object modifierAsObject = actionOptions;
				modifier.call((MappingActions<S, D>) modifierAsObject);
			}
			return (S) super.fromDestination(destination);
		} catch (Exception e) {
			return null;
		}
	}
}
