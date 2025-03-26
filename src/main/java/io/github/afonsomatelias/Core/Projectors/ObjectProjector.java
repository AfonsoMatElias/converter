package io.github.afonsomatelias.Core.Projectors;

import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.Interfaces.IMappingObjectActions;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IObjectProjector;

@SuppressWarnings("unchecked")
public class ObjectProjector<S extends Map<String, ? extends Object>> extends Projector<S> implements IObjectProjector<S> {
	public ObjectProjector(ConverterShared shared, S source) {
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
	public <D> D to(Class<D> clazz, I1Action<IMappingObjectActions<S, D>> modifier) {
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
}
