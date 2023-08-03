package io.github.afonsomatelias.Options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Enums.MappingActionsEnum;
import io.github.afonsomatelias.Options.Interfaces.IMappingActions;

public class MappingActions<S, D> implements IMappingActions<S, D> {

	public MappingActions() {
	}

	public MappingActions(MappingActions<S, D> outActionOptions) {

		// Merging the configurations
		for (Entry<MappingActionsEnum, List<CallbackV2<Object, Object>>> entry : outActionOptions.actions.entrySet()) {
			this.actions.put(entry.getKey(), entry.getValue());
		}
	}

	// Stores all the actions according to the type
	private Map<MappingActionsEnum, List<CallbackV2<Object, Object>>> actions = new HashMap<>();

	/**
	 * Subscribes {@link MappingActionsEnum} actions
	 * 
	 * @param targetAction
	 * @param action
	 */
	@SuppressWarnings("unchecked")
	public void on(MappingActionsEnum targetAction, CallbackV2<S, D> action) {
		// Defining the default List of Actions
		List<CallbackV2<Object, Object>> mActions = new ArrayList<>();

		if (!actions.containsKey(targetAction))
			actions.put(targetAction, mActions);

		// Setting the actual list of action
		mActions = actions.get(targetAction);

		// Adding the new action
		mActions.add((CallbackV2<Object, Object>) action);
	}

	/**
	 * Action caller
	 * 
	 * @param targetAction the target action to be called
	 * @param src          the source object
	 * @param dst          the destination object
	 */
	public void call(MappingActionsEnum targetAction, S src, D dst) {
		// Defining the default List of Actions
		List<CallbackV2<Object, Object>> mActions = actions.getOrDefault(targetAction, Arrays.asList());

		// Setting the actual list of action
		for (int i = 0; i < mActions.size(); i++) {
			CallbackV2<Object, Object> action = mActions.get(i);

			// Calling the action
			action.call(src, dst);
		}
	}

	/**
	 * Subscribes {@link MappingActionsEnum.BEFORE_MAP} action
	 * 
	 * @implNote The destination argument will be null
	 * @param modifier the delegate having the modification
	 */
	public void beforeMap(CallbackV2<S, D> modifier) {
		this.on(MappingActionsEnum.BEFORE_MAP, modifier);
	}

	/**
	 * Subscribes {@link MappingActionsEnum.AFTER_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void afterMap(CallbackV2<S, D> modifier) {
		this.on(MappingActionsEnum.AFTER_MAP, modifier);
	}
}
