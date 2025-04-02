package io.github.afonsomatelias.Core.Projectors.Map;

import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IObjectProjector;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.Interfaces.IMappingObjectActions;

@SuppressWarnings("unchecked")
public class MapObjectProjector<Entry> extends MapProjector<Entry> implements IObjectProjector<Entry> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param entry the {@link Entry} object
	 */
	public MapObjectProjector(ConverterShared shared, Entry entry) {
		super(shared, (Entry) entry);
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
	 * Maps the {@link Entry} object to the destination class provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
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
}
