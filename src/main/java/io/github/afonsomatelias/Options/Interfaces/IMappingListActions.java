package io.github.afonsomatelias.Options.Interfaces;

import java.util.List;

import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Enums.EMappingActions;

public interface IMappingListActions<S, D> extends IMappingActions {
	/**
     * Subscribes {@link EMappingActions.BEFORE_MAP} action
	 * 
     * @param modifier the delegate having the modification
     */
    void beforeMap(I2Action<List<S>, List<D>> modifier);
    
    /**
     * Subscribes {@link EMappingActions.AFTER_MAP} action
	 * 
     * @param modifier the delegate having the modification
     */
    void afterMap(I2Action<List<S>, List<D>> modifier);


    /**
	 * Subscribes {@link EMappingActions.BEFORE_EACH_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void beforeEachMap(I2Action<S, D> modifier);

	/**
	 * Subscribes {@link EMappingActions.AFTER_EACH_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void afterEachMap(I2Action<S, D> modifier);
}
