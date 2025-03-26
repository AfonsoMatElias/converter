package io.github.afonsomatelias;

import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Core.Mappers.Interfaces.IListMapper;
import io.github.afonsomatelias.Core.Mappers.Interfaces.IObjectMapper;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IListProjector;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IObjectProjector;

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

	/**
	 * Creates Mapping Projector for the {@link MapObject} Object, where the source must be
	 * a {@link Map} and the destination must also be a {@link Map}.
	 * 
	 * @param <MapObject>    	the {@link MapObject} Type which must be a {@link Map}
	 * @param entry 			the {@link MapObject} Object to be mapped
	 * @return the Projector the where having all the all the different methods to
	 *         perform
	 */
	<MapObject extends Map<String, ? extends Object>> IObjectProjector<MapObject> project(MapObject entry);

	/**
	 * Creates Mapping Projector for the {@link MapObject} Object, where the source must be
	 * a {@link List} of {@link Map} and the destination must also be a
	 * {@link List} of {@link Map}.
	 * 
	 * @param <MapObject>    	the {@link MapObject} Type which must be a {@link List} of {@link Map}
	 * @param entry 			the {@link MapObject} Object to be mapped
	 * @return the Projector the where having all the all the different methods to
	 *         perform
	 */
	<MapObject extends List<Map<String, ? extends Object>>> IListProjector<MapObject> project(List<MapObject> entry);
}
