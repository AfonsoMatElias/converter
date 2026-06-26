package io.github.afonsomatelias;

import java.util.List;

import io.github.afonsomatelias.Core.Interfaces.IListMapper;
import io.github.afonsomatelias.Core.Interfaces.IObjectMapper;

public interface IConverter {
	/**
	 * Creates Mapping Processor for the {@link Entry} Object
	 * 
	 * @param <Entry>    	the {@link Entry} Type
	 * @param entry 		the {@link Entry} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<Entry> IObjectMapper<Entry> map(Entry entry);

	/**
	 * Creates Mapping Processor for the {@link Entry} Object
	 * 
	 * @param <Entry>    	the {@link Entry} Type
	 * @param entry 		the {@link Entry} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<Entry> IListMapper<Entry> map(List<Entry> entry);
}
