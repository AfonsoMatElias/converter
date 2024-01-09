package io.github.afonsomatelias.Options.Interfaces;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Options.MappingExpression;

public interface IMappingExpression<S, D> {
	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param transform         the interception bahavior
	 */
	<U> IMappingExpression<S, D> forMember(ISetterFunction<D, U> setterFunction, CallbackP1<S, Object> transform);
	
	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param transform         the interception bahavior
	 */
	IMappingExpression<S, D> forMember(String destinationMember, CallbackP1<S, Object> transform);
	
	/**
	 * skips or set null to the destination member provided
	 * 
	 * @param destinationMember the member that will be transformed
	 */
	IMappingExpression<S, D> skipMember(String destinationMember);

	/**
	 * Subscribes a before map action for this {@link S} and
	 * {@link D} Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link IMappingExpression} for chaining
	 */
	IMappingExpression<S, D> beforeMap(CallbackV2<S, D> mappingAction);

	/**
	 * Subscribes a after map action for this {@link S} and {@link D}
	 * Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link IMappingExpression} for chaining
	 */
	IMappingExpression<S, D> afterMap(CallbackV2<S, D> mappingAction);

	/**
	 * Used to recreate the same mapping but in reverse order
	 * 
	 * @return {@link MappingExpression} for chaining
	 */
	IMappingExpression<D, S> reverseMap();
}