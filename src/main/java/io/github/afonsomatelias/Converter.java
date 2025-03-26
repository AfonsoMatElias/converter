package io.github.afonsomatelias;

import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Mappers.ListMapper;
import io.github.afonsomatelias.Core.Mappers.ObjectMapper;
import io.github.afonsomatelias.Core.Mappers.Interfaces.IListMapper;
import io.github.afonsomatelias.Core.Mappers.Interfaces.IObjectMapper;
import io.github.afonsomatelias.Core.Projectors.ListProjector;
import io.github.afonsomatelias.Core.Projectors.ObjectProjector;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IListProjector;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IObjectProjector;


public abstract class Converter implements IConverter {
	/**
	 * Default Converter
	 */
	public Converter() {
		shared = new ConverterShared();
	}

	// All the public properties that will e shared between inner instances
	protected final ConverterShared shared;

	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    	the {@link S} Type
	 * @param entry 	the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	public <S> IObjectMapper<S> map(S entry) {
		return new ObjectMapper<>(shared, entry);
	}

	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    	the {@link S} Type
	 * @param entry 	the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <S> IListMapper<S> map(List<S> entry) {
		return new ListMapper<>(shared, entry);
	}

	/**
	 * Creates Mapping Projector for the {@link MapObject} Object, where the source must be
	 * a {@link Map} and the destination must also be a {@link Map}.
	 * 
	 * @param <MapObject>    the {@link MapObject} Type which must be a {@link Map}
	 * @param entry the {@link MapObject} Object to be mapped
	 * @return the Projector the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <MapObject extends Map<String, ? extends Object>> IObjectProjector<MapObject> project(MapObject entry) {
		return new ObjectProjector<>(shared, entry);
	}
	
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
	@Override
	public <MapObject extends List<Map<String, ? extends Object>>> IListProjector<MapObject> project(List<MapObject> entry) {
		return new ListProjector<>(shared, entry);
	}
}
