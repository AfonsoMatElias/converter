package io.github.afonsomatelias.Core.Projectors;

import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Base.BaseCore;
import io.github.afonsomatelias.Core.Base.ProxyInterface;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IInterfaceProjector;

public class InterfaceProjector<Entry> extends BaseCore<Entry> implements IInterfaceProjector<Entry> {
	/**
	 * The Default Constructor
	 * 
	 * @param shared the {@link ConverterShared} instance
	 * @param entry the {@link Entry} object
	 */
	public InterfaceProjector(ConverterShared shared, Entry entry) {
		super(shared, (Entry) entry);
	}

	/**
	 * Maps the source object to the destination interface provided
	 * 
	 * @param <D>   the {@link D} object type
	 * @param clazz the {@link D} interface class type
	 * @return the object Converted
	 */
	@Override
	public <D> D to(Class<D> clazz) {
		try {
			return new ProxyInterface<Entry>(shared, entry, clazz)
				.build();
		} catch (Exception e) {
			return null;
		}
	}
}
