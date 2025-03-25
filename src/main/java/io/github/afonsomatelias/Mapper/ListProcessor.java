package io.github.afonsomatelias.Mapper;

import java.util.List;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackV1;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Helpers.Printer;
import io.github.afonsomatelias.Mapper.Interfaces.IListProcessor;
import io.github.afonsomatelias.Options.MappingListActions;
import io.github.afonsomatelias.Options.Interfaces.IMappingListActions;

@SuppressWarnings("unchecked")
public class ListProcessor<S> extends Processor<S> implements IListProcessor<S> {

	public ListProcessor(ConverterShared shared, Object source) {
		super(shared, (S) source);
	}

	/**
	 * Maps the list of {@link S} objects to the list of destination class
	 * provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	@Override
	public <D> List<D> to(Class<D> clazz) {
		try {
			return (List<D>) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Maps the list of {@link S} objects to the list of destination class
	 * provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	@Override
	public <D> List<D> to(Class<D> clazz, CallbackV1<IMappingListActions<S, D>> modifier) {
		try {
			if (modifier != null) {
				MappingListActions<S, D> actions = new MappingListActions<>();
				modifier.call(actions); localActionOptions.merge(actions); actions = null;
			}

			return (List<D>) this.toDestination(clazz);
		} catch (Exception e) {
			Printer.err(e);
			return null;
		}
	}

	/**
	 * Creates a new instance of the object provided, just like copy and paste with
	 * a different memory address, applying mapping options if a modifier is provided.
	 *
	 * @param <D>      the {@link D} object type which extends {@link S}
	 * @param modifier mapping options that will be applied on map
	 * @return new object instance or null in case of an exception
	 */
	@Override
	public <D extends S> S to(CallbackV1<IMappingListActions<S, D>> modifier) {
		try {
			if (modifier != null) {
				MappingListActions<S, D> actions = new MappingListActions<>();
				modifier.call(actions); localActionOptions.merge(actions); actions = null;
			}

			return (S) this.toDestination(source.getClass());
		} catch (Exception e) {
			return null;
		}
	}
}
