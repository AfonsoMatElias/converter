package io.github.cnvtr.Options;

import java.util.List;

import io.github.cnvtr.Callback.ICallbacks.CallbackV2;
import io.github.cnvtr.Enums.MappingActionsEnum;
import io.github.cnvtr.Options.Interfaces.IMappingListActions;

@SuppressWarnings("unchecked")
public class MappingListActions<S, D> extends MappingActions<List<S>, List<D>> implements IMappingListActions<S, D> {

	public MappingListActions() {
		super();
	}

	MappingActions<S, D> parent;

	public MappingListActions(MappingActions<S, D> outActionOptions) {
		super((MappingActions<List<S>, List<D>>) ((Object) outActionOptions));
		this.parent = outActionOptions;
	}

	/**
	 * Subscribes {@link MappingActionsEnum.BEFORE_EACH_MAP} action
	 * 
	 * @implNote The destination argument will be null
	 * @param modifier the delegate having the modification
	 */
	public void beforeEachMap(CallbackV2<S, D> modifier) {
		this.on(MappingActionsEnum.BEFORE_EACH_MAP, (CallbackV2<Object, Object>) modifier);

		// If there is a parent, also register to the parent because this instance will
		// be dispose
		if (parent != null)
			parent.on(MappingActionsEnum.BEFORE_EACH_MAP, (CallbackV2<Object, Object>) modifier);
	}

	/**
	 * Subscribes {@link MappingActionsEnum.AFTER_EACH_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void afterEachMap(CallbackV2<S, D> modifier) {
		this.on(MappingActionsEnum.AFTER_EACH_MAP, (CallbackV2<Object, Object>) modifier);

		// If there is a parent, also register to the parent because this instance will
		// be dispose
		if (parent != null)
			parent.on(MappingActionsEnum.AFTER_EACH_MAP, (CallbackV2<Object, Object>) modifier);
	}

	public static <S, D> IMappingListActions<S, D> toBase(MappingActions<Object, Object> actionOptions) {
		return new MappingListActions<>((MappingActions<S, D>) ((Object) actionOptions));
	}
}
