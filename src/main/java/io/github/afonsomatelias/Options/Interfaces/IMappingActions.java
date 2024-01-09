package io.github.afonsomatelias.Options.Interfaces;

import java.lang.reflect.Field;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Enums.MappingActionsEnum;

public interface IMappingActions<S, D> {
	/**
	 * Subscribes {@link MappingActionsEnum} actions
	 * @param targetAction
	 * @param action
	 */
	void on(MappingActionsEnum targetAction, CallbackV2<Object, Object> action);

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

    /**
	 * skips member that do not need to be mapped
	 * 
	 * @param members the member that will be skipped
	 */
	void skipMembers(String... members);

    /**
	 * skips member that do not need to be mapped
	 * 
	 * @param members the member that will be skipped
	 */
	void skipMembers(Field... members);
}
