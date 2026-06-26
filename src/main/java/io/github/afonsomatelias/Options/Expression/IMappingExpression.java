package io.github.afonsomatelias.Options.Expression;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Callback.ICallbacks.I2Fn;
import io.github.afonsomatelias.Callback.ICallbacks.IGetterMethod;
import io.github.afonsomatelias.Callback.ICallbacks.ISetterMethod;

public interface IMappingExpression<S, D> {
	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param callback          the interception bahavior
	 */
	IMappingExpression<S, D> forMember(
		String destinationMember, 
		I1Fn<S, Object> callback
	);

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param callback          the interception bahavior
	 */
	IMappingExpression<S, D> forMember(
		String destinationMember, 
		I2Fn<S, MemberConfigExpression, Object> callback
	);

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param callback          the interception bahavior
	 */
	<R> IMappingExpression<S, D> forMember(
		IGetterMethod<D, R> getter, 
		I1Fn<S, Object> callback
	);

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param callback          the interception bahavior
	 */
	<R> IMappingExpression<S, D> forMember(
		IGetterMethod<D, R> getter, 
		I2Fn<S, MemberConfigExpression, Object> callback
	);

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param callback         the interception bahavior
	 */
	<P> IMappingExpression<S, D> forMember(
		ISetterMethod<D, P> setter, 
		I2Fn<S, MemberConfigExpression, Object> callback
	);
	
	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param transform         the interception bahavior
	 */
	<P> IMappingExpression<S, D> forMember(
		ISetterMethod<D, P> setterFunction, 
		I1Fn<S, Object> transform
	);
	
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
	IMappingExpression<S, D> beforeMap(I2Action<S, D> mappingAction);

	/**
	 * Subscribes a after map action for this {@link S} and {@link D}
	 * Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link IMappingExpression} for chaining
	 */
	IMappingExpression<S, D> afterMap(I2Action<S, D> mappingAction);

	/**
	 * Used to recreate the same mapping but in reverse order
	 * 
	 * @return {@link MappingExpression} for chaining
	 */
	IMappingExpression<D, S> reverseMap();
}