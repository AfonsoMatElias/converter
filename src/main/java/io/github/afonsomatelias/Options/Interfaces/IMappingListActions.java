package io.github.afonsomatelias.Options.Interfaces;

import java.util.List;

import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Enums.MappingActionsEnum;

public interface IMappingListActions<S, D> extends IMappingActions {
	/**
     * Subscribes {@link MappingActionsEnum.BEFORE_MAP} action
	 * 
     * @param modifier the delegate having the modification
     */
    void beforeMap(I2Action<List<S>, List<D>> modifier);
    
    /**
     * Subscribes {@link MappingActionsEnum.AFTER_MAP} action
	 * 
     * @param modifier the delegate having the modification
     */
    void afterMap(I2Action<List<S>, List<D>> modifier);


    /**
	 * Subscribes {@link MappingActionsEnum.BEFORE_EACH_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void beforeEachMap(I2Action<S, D> modifier);

	/**
	 * Subscribes {@link MappingActionsEnum.AFTER_EACH_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void afterEachMap(I2Action<S, D> modifier);
}
