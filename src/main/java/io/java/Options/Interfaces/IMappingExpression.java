package io.java.Options.Interfaces;

import io.java.Callback.ICallbacks.I1Callback;
import io.java.Callback.ICallbacks.IV2Callback;

public interface IMappingExpression<Source, Destination> {
	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param mutation          the interception bahavior/mutation
	 */
	IMappingExpression<Source, Destination> forMember(String destinationMember,
			I1Callback<Source, Object> mutation);

	/**
	 * Subscribes a before map action for this {@link Source} and
	 * {@link Destination} Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link IMappingExpression} for chaining
	 */
	IMappingExpression<Source, Destination> beforeMap(IV2Callback<Source, Destination> mappingAction);

	/**
	 * Subscribes a after map action for this {@link Source} and {@link Destination}
	 * Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link IMappingExpression} for chaining
	 */
	IMappingExpression<Source, Destination> afterMap(IV2Callback<Source, Destination> mappingAction);

}