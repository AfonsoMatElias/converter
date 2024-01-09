package io.github.cnvtr.Mapper;

import java.util.List;

import io.github.cnvtr.Callback.ICallbacks.CallbackV1;
import io.github.cnvtr.Configurations.ConverterShared;
import io.github.cnvtr.Mapper.Interfaces.IListProcessor;
import io.github.cnvtr.Options.MappingListActions;
import io.github.cnvtr.Options.Interfaces.IMappingListActions;

@SuppressWarnings("unchecked")
public class ListProcessor<S> extends Processor<S> implements IListProcessor<S> {

	public ListProcessor(ConverterShared shared, Object source) {
		super(shared, (S) source);
	}

	/**
	 * Converts the list of {@link S} objects to the list of destination class
	 * provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} class type
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz) {
		try {
			return (List<D>) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts the list of {@link S} objects to the list of destination class
	 * provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	public <D> List<D> to(Class<D> clazz, CallbackV1<IMappingListActions<S, D>> modifier) {
		try {
			if (modifier != null) {
				// Calling the function
				modifier.call(MappingListActions.toBase(actionOptions));
			}

			return (List<D>) this.toDestination(clazz);
		} catch (Exception e) {
			return null;
		}
	}
}
