package io.github.afonsomatelias.Core.Projectors;

import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Helpers.Printer;
import io.github.afonsomatelias.Options.MappingListActions;
import io.github.afonsomatelias.Options.Interfaces.IMappingListActions;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IListProjector;

@SuppressWarnings("unchecked")
public class ListProjector<Entry extends List<Map<String, ? extends Object>>> extends Projector<Entry> implements IListProjector<Entry> {

	public ListProjector(ConverterShared shared, Object entry) {
		super(shared, (Entry) entry);
	}
	
	/**
	 * Projects the list of {@link Entry} objects to the list of destination class
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
	 * Projects the list of {@link Entry} objects to the list of destination class
	 * provided with a mapper modifier
	 * 
	 * @param <D>      the {@link D} object type
	 * @param clazz    the {@link D} class type
	 * @param modifier mapping options that will be applied on map
	 * @return the object Converted
	 */
	@Override
	public <D> List<D> to(Class<D> clazz, I1Action<IMappingListActions<Entry, D>> modifier) {
		try {
			if (modifier != null) {
				MappingListActions<Entry, D> actions = new MappingListActions<>();
				modifier.call(actions); localActionOptions.merge(actions); actions = null;
			}

			return (List<D>) this.toDestination(clazz);
		} catch (Exception e) {
			Printer.err(e);
			return null;
		}
	}
}
