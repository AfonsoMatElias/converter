package io.github.afonsomatelias;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Mappers.ListMapper;
import io.github.afonsomatelias.Core.Mappers.ObjectMapper;
import io.github.afonsomatelias.Core.Mappers.Interfaces.IListMapper;
import io.github.afonsomatelias.Core.Mappers.Interfaces.IObjectMapper;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IListProjector;
import io.github.afonsomatelias.Core.Projectors.Interfaces.IObjectProjector;
import io.github.afonsomatelias.Core.Projectors.Map.MapListProjector;
import io.github.afonsomatelias.Core.Projectors.Map.MapObjectProjector;
import io.github.afonsomatelias.Core.Projectors.Object.ListProjector;
import io.github.afonsomatelias.Core.Projectors.Object.ObjectProjector;


public abstract class Converter implements IConverter {
	/**
	 * Default Converter
	 */
	public Converter() {
		this.shared = new ConverterShared();
	}

	// All the public properties that will e shared between inner instances
	protected final ConverterShared shared;

	/**
	 * Creates Mapping Processor for the {@link Entry} Object
	 * 
	 * @param <Entry>    	the {@link Entry} Type
	 * @param entry 		the {@link Entry} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	public <Entry> IObjectMapper<Entry> map(Entry entry) {
		return new ObjectMapper<>(shared, entry);
	}

	/**
	 * Creates Mapping Processor for the {@link Entry} Object
	 * 
	 * @param <Entry>    	the {@link Entry} Type
	 * @param entry 		the {@link Entry} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <Entry> IListMapper<Entry> map(List<Entry> entry) {
		return new ListMapper<>(shared, entry);
	}

	/**
	 * Creates Mapping Projector for the {@link MapObject} Object, where the source must be
	 * a {@link HashMap}.
	 * 
	 * @param <MapObject>    the {@link MapObject} Type which must be a {@link HashMap}
	 * @param entry			 the {@link MapObject} Object to be mapped
	 * @return the Projector the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <MapObject extends Map<String, ? extends Object>> IObjectProjector<MapObject> project(MapObject entry) {
		return new MapObjectProjector<>(shared, entry);
	}
	
	/**
	 * Creates Mapping Projector for the {@link MapObject} Object, where the source must be
	 * a {@link List} of {@link HashMap}.
	 * 
	 * @param <MapObject>    	the {@link MapObject} Type which must be a {@link List} of {@link HashMap}
	 * @param entry 			the {@link MapObject} Object to be mapped
	 * @return the Projector the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <MapObject extends Map<String, ? extends Object>> IListProjector<MapObject> project(List<MapObject> entry) {
		return new MapListProjector<>(shared, entry);
	}

	/**
	 * Creates Mapping Projector for the {@link IObject} Object, where the source must be
	 * a {@link InterfaceProjectionObject}
	 * 
	 * @param <IObject>    	the {@link IObject} Type which must be a {@link Map}
	 * @param entry 			the {@link IObject} Object to be mapped
	 * @return the Projector the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <IObject> IObjectProjector<IObject> project(IObject entry) {
		return new ObjectProjector<>(shared, entry);
	}
	
	/**
	 * Creates Mapping Projector for the {@link IObject} Object, where the source must be
	 * a {@link List} of {@link InterfaceProjectionObject}.
	 * 
	 * @param <IObject>    	the {@link IObject} Type which must be a {@link List} of {@link Map}
	 * @param entry 			the {@link IObject} Object to be mapped
	 * @return the Projector the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <IObject> IListProjector<IObject> project(Collection<IObject> entry) {
		return new ListProjector<>(shared, entry);
	}
}
