package io.github.afonsomatelias.Options.Interfaces;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Enums.EMappingActions;

public interface IMappingObjectActions<S, D> extends IMappingActions {

	/**
     * Subscribes {@link EMappingActions.BEFORE_MAP} action
	 * 
     * @param modifier the delegate having the modification
     */
    void beforeMap(CallbackV2<S, D> modifier);
    
    /**
     * Subscribes {@link EMappingActions.AFTER_MAP} action
	 * 
     * @param modifier the delegate having the modification
     */
    void afterMap(CallbackV2<S, D> modifier);
}
