package io.github.afonsomatelias.Configurations;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackV1;
import io.github.afonsomatelias.Options.Expression.IMappingExpression;
import io.github.afonsomatelias.Options.Interfaces.IMappingObjectActions;

public abstract class Profile {
	protected ConverterConfiguration $super;

	/**
	 * Creates a Mapping configuration for the Source and Destination Object type
	 * 
	 * @param <S>         the {@link S} Type
	 * @param <D>         the {@link D} Type
	 * @param source      the {@link S} Class
	 * @param destination the {@link D} Class
	 */
	public <S, D> IMappingExpression<S, D> createMap(
		Class<S> source,
		Class<D> destination
	) {
		return $super.createMap(source, destination);
	}

	/**
	 * Creates a Mapping configuration for the Source and Destination Object type
	 * 
	 * @param <S>         the {@link S} Type
	 * @param <D>         the {@link D} Type
	 * @param source      the {@link S} Class
	 * @param destination the {@link D} Class
	 */
	public <S, D> IMappingExpression<S, D> createMap(
		Class<S> source,
		Class<D> destination,
		CallbackV1<IMappingObjectActions<S, D>> modifier
	) {
		return $super.createMap(source, destination, modifier);
	}

	
	/**
	 * Add tranformation to a mapping behavior for {@link TypeSource} Type to
	 * {@link TypeDestination} Type
	 * 
	 * @param <TypeSource>      the Type that needs to intercepted
	 * @param <TypeDestination> the Type to be converted to
	 * @param from              the {@link TypeSource} Class
	 * @param to                the {@link TypeSource} Class
	 * @param behavior          the interception bahavior
	 */
	public <TypeSource, TypeDestination> void addTransform(
		Class<TypeSource> from, 
		Class<TypeDestination> to,
		CallbackP1<TypeSource, TypeDestination> behavior
	) {
		$super.addTransform(from, to, behavior);
	}

	public void init() {}
}
