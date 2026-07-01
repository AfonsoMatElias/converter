package io.github.afonsomatelias.Options;

import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Enums.MappingActionsEnum;
import io.github.afonsomatelias.Options.Interfaces.IMappingObjectActions;

@SuppressWarnings("unchecked")
public class MappingObjectActions<S, D> extends MappingActions implements IMappingObjectActions<S, D> {

	/**
	 * Subscribes {@link MappingActionsEnum.BEFORE_MAP} action
	 * 
	 * @implNote The destination argument will be null
	 * @param modifier the delegate having the modification
	 */
	public void beforeMap(I2Action<S, D> modifier) {
		super.on(MappingActionsEnum.BEFORE_MAP, (I2Action<Object, Object>) modifier);
	}

	/**
	 * Subscribes {@link MappingActionsEnum.AFTER_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void afterMap(I2Action<S, D> modifier) {
		super.on(MappingActionsEnum.AFTER_MAP, (I2Action<Object, Object>) modifier);
	}
}
