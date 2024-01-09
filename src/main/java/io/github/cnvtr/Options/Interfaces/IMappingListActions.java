package io.github.cnvtr.Options.Interfaces;

import java.util.List;

import io.github.cnvtr.Callback.ICallbacks.CallbackV2;
import io.github.cnvtr.Enums.MappingActionsEnum;

public interface IMappingListActions<S, D> extends IMappingActions<List<S>, List<D>> {
    /**
	 * Subscribes {@link MappingActionsEnum.BEFORE_EACH_MAP} action
	 * 
	 * @implNote The destination argument will be null
	 * @param modifier the delegate having the modification
	 */
	public void beforeEachMap(CallbackV2<S, D> modifier);

	/**
	 * Subscribes {@link MappingActionsEnum.AFTER_EACH_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void afterEachMap(CallbackV2<S, D> modifier);
}
