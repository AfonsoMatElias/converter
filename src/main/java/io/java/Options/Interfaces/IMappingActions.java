package io.java.Options.Interfaces;

import io.java.Callback.ICallbacks.CallbackV2;
import io.java.Enums.MappingActionsEnum;

public interface IMappingActions<S, D> {
	/**
	 * Subscribes {@link MappingActionsEnum} actions
	 * @param targetAction
	 * @param action
	 */
	void on(MappingActionsEnum targetAction, CallbackV2<S, D> action);

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
    void beforeMap(CallbackV2<S, D> modifier);
    
    /**
     * Subscribes {@link MappingActionsEnum.AFTER_MAP} action
     * @param modifier the delegate having the modification
     */
    void afterMap(CallbackV2<S, D> modifier);
}
