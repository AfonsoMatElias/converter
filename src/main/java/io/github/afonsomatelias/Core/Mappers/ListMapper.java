package io.github.afonsomatelias.Core.Mappers;

import java.util.List;

import io.github.afonsomatelias.Callback.ICallbacks.I1Action;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Mappers.Interfaces.IListMapper;
import io.github.afonsomatelias.Helpers.Printer;
import io.github.afonsomatelias.Options.MappingListActions;
import io.github.afonsomatelias.Options.Interfaces.IMappingListActions;

@SuppressWarnings("unchecked")
public class ListMapper<Entry> extends Mapper<Entry> implements IListMapper<Entry> {

	public ListMapper(ConverterShared shared, Object entry) {
		super(shared, (Entry) entry);
	}

	/**
	 * Maps the list of {@link Entry} objects to the list of destination class
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
	 * Maps the list of {@link Entry} objects to the list of destination class
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

	/**
	 * Creates a new instance of the object provided, just like copy and paste with
	 * a different memory address, applying mapping options if a modifier is provided.
	 *
	 * @param <D>      the {@link D} object type which extends {@link Entry}
	 * @param modifier mapping options that will be applied on map
	 * @return new object instance or null in case of an exception
	 */
	@Override
	public <D extends Entry> Entry to(I1Action<IMappingListActions<Entry, D>> modifier) {
		try {
			if (modifier != null) {
				MappingListActions<Entry, D> actions = new MappingListActions<>();
				modifier.call(actions); localActionOptions.merge(actions); actions = null;
			}

			return (Entry) this.toDestination(entry.getClass());
		} catch (Exception e) {
			return null;
		}
	}
}
