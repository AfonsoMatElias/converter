package io.github.afonsomatelias;

import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Core.Mappers.Interfaces.IListMapper;
import io.github.afonsomatelias.Core.Mappers.Interfaces.IObjectMapper;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IListProjector;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IObjectProjector;

public interface IConverter {
	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<S> IObjectMapper<S> map(S source);

	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<S> IListMapper<S> map(List<S> source);

	/**
	 * Creates Mapping Projector for the {@link MapObject} Object, where the source must be
	 * a {@link Map} and the destination must also be a {@link Map}.
	 * 
	 * @param <MapObject>    the {@link MapObject} Type which must be a {@link Map}
	 * @param source the {@link MapObject} Object to be mapped
	 * @return the Projector the where having all the all the different methods to
	 *         perform
	 */
	<MapObject extends Map<String, ? extends Object>> IObjectProjector<MapObject> project(MapObject source);

	/**
	 * Creates Mapping Projector for the {@link MapObject} Object, where the source must be
	 * a {@link List} of {@link Map} and the destination must also be a
	 * {@link List} of {@link Map}.
	 * 
	 * @param <MapObject>    the {@link MapObject} Type which must be a {@link List} of
	 *                       {@link Map}
	 * @param source the {@link MapObject} Object to be mapped
	 * @return the Projector the where having all the all the different methods to
	 *         perform
	 */
	<MapObject extends List<Map<String, ? extends Object>>> IListProjector<MapObject> project(List<MapObject> source);
}
