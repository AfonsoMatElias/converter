package io.github.afonsomatelias.Options.Interfaces;

import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Enums.MappingActionsEnum;

public interface IMappingObjectActions<S, D> extends IMappingActions {

	/**
     * Subscribes {@link MappingActionsEnum.BEFORE_MAP} action
	 * 
     * @param modifier the delegate having the modification
     */
    void beforeMap(I2Action<S, D> modifier);
    
    /**
     * Subscribes {@link MappingActionsEnum.AFTER_MAP} action
	 * 
     * @param modifier the delegate having the modification
     */
    void afterMap(I2Action<S, D> modifier);
}
