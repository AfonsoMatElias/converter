package io.github.afonsomatelias.Options;

import java.util.List;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Enums.EMappingActions;
import io.github.afonsomatelias.Options.Interfaces.IMappingListActions;

@SuppressWarnings("unchecked")
public class MappingListActions<S, D> extends MappingActions implements IMappingListActions<S, D> {
	/**
	 * Subscribes {@link EMappingActions.BEFORE_MAP} action
	 * 
	 * @implNote The destination argument will be null
	 * @param modifier the delegate having the modification
	 */
	public void beforeMap(CallbackV2<List<S>, List<D>> modifier) {
		super.on(EMappingActions.BEFORE_MAP, (CallbackV2<Object, Object>)((Object)modifier));
	}

	/**
	 * Subscribes {@link EMappingActions.AFTER_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void afterMap(CallbackV2<List<S>, List<D>> modifier) {
		super.on(EMappingActions.AFTER_MAP, (CallbackV2<Object, Object>)((Object)modifier));
	}
	
	/**
	 * Subscribes {@link EMappingActions.BEFORE_EACH_MAP} action
	 * 
	 * @implNote The destination argument will be null
	 * @param modifier the delegate having the modification
	 */
	public void beforeEachMap(CallbackV2<S, D> modifier) {
		super.on(EMappingActions.BEFORE_EACH_MAP, (CallbackV2<Object, Object>) modifier);
	}

	/**
	 * Subscribes {@link EMappingActions.AFTER_EACH_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void afterEachMap(CallbackV2<S, D> modifier) {
		super.on(EMappingActions.AFTER_EACH_MAP, (CallbackV2<Object, Object>) modifier);
	}
}
