package io.java.Options.Interfaces;

import io.java.Callback.ICallbacks.IV2Callback;
import io.java.Enums.MappingActionsEnum;

public interface IMappingActions<S, D> {
	/**
	 * Subscribes {@link MappingActionsEnum} actions
	 * @param targetAction
	 * @param action
	 */
	void on(MappingActionsEnum targetAction, IV2Callback<S, D> action);

	/**
	 * Action caller
	 * @param targetAction the target action to be called
	 * @param src the source object
	 * @param dst the destination object
	 */
	void call(MappingActionsEnum targetAction, S src, D dst);

	/**
     * Subscribes {@link MappingActionsEnum.BEFORE_MAP} action
     * @implNote The destination argument will be null
     * @param modifier the delegate having the modification
     */
    void beforeMap(IV2Callback<S, D> modifier);
    
    /**
     * Subscribes {@link MappingActionsEnum.AFTER_MAP} action
     * @param modifier the delegate having the modification
     */
    void afterMap(IV2Callback<S, D> modifier);
}
