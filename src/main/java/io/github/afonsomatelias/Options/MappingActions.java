package io.github.afonsomatelias.Options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Enums.MappingActionsEnum;
import io.github.afonsomatelias.Options.Interfaces.IMappingActions;

@SuppressWarnings("unchecked")
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
	protected Map<MappingActionsEnum, List<CallbackV2<Object, Object>>> actions = new HashMap<>();

	/**
	 * Subscribes {@link MappingActionsEnum} actions
	 * 
	 * @param targetAction
	 * @param action
	 */
	public void on(MappingActionsEnum targetAction, CallbackV2<Object, Object> action) {
		// Defining the default List of Actions
		List<CallbackV2<Object, Object>> mActions = new ArrayList<>();

		if (!actions.containsKey(targetAction))
			actions.put(targetAction, mActions);

		// Setting the actual list of action
		mActions = actions.get(targetAction);

		// Adding the new action
		mActions.add(action);
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
		final List<CallbackV2<Object, Object>> mActions = actions.getOrDefault(targetAction, Arrays.asList());

		// Helper Function to check if an object is an Array
		final CallbackP1<Object, Boolean> isArray = (in) -> (in.getClass().isArray() || (in instanceof List<?>));

		// Setting the actual list of action
		for (int i = 0; i < mActions.size(); i++) {
			final CallbackV2<Object, Object> action = mActions.get(i);
			final Iterator<Object> srcIterator = ((Iterable<Object>) src).iterator();

			// BEFORE_EACH_MAP Config
			if (targetAction == MappingActionsEnum.BEFORE_EACH_MAP && isArray.call(src)) {
				while (srcIterator.hasNext()) {
					// Calling the action for each item
					action.call(srcIterator.next(), null);
				}

				continue;
			}

			final boolean isEachAction = (targetAction == MappingActionsEnum.AFTER_EACH_MAP
					|| targetAction == MappingActionsEnum.BEFORE_EACH_MAP);
			final Iterator<Object> dstIterator = ((Iterable<Object>) dst).iterator();

			// AFTER_EACH_MAP Config
			if (isEachAction && isArray.call(src)) {
				while (srcIterator.hasNext() && dstIterator.hasNext()) {
					// Calling the action for each item
					action.call(srcIterator.next(), dstIterator.next());
				}

				continue;
			}

			// Normal Options Config action
			// Calling the action for each item
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
		this.on(MappingActionsEnum.BEFORE_MAP, (CallbackV2<Object, Object>) modifier);
	}

	/**
	 * Subscribes {@link MappingActionsEnum.AFTER_MAP} action
	 * 
	 * @param modifier the delegate having the modification
	 */
	public void afterMap(CallbackV2<S, D> modifier) {
		this.on(MappingActionsEnum.AFTER_MAP, (CallbackV2<Object, Object>) modifier);
	}
}
