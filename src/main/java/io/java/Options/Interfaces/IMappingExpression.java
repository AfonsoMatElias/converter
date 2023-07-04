package io.java.Options.Interfaces;

import io.java.Callback.ICallbacks.I1Callback;
import io.java.Callback.ICallbacks.IV2Callback;
import io.java.Options.MappingExpression;

public interface IMappingExpression<S, D> {
	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param transform         the interception bahavior
	 */
	IMappingExpression<S, D> forMember(String destinationMember, I1Callback<S, Object> transform);
	
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
	IMappingExpression<S, D> beforeMap(IV2Callback<S, D> mappingAction);

	/**
	 * Subscribes a after map action for this {@link S} and {@link D}
	 * Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link IMappingExpression} for chaining
	 */
	IMappingExpression<S, D> afterMap(IV2Callback<S, D> mappingAction);

	/**
	 * Used to recreate the same mapping but in reverse order
	 * 
	 * @return {@link MappingExpression} for chaining
	 */
	IMappingExpression<D, S> reverseMap();
}