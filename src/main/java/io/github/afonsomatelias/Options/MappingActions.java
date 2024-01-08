package io.github.afonsomatelias.Options;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Enums.MappingActionsEnum;
import io.github.afonsomatelias.Helpers.Printer;
import io.github.afonsomatelias.Options.Interfaces.IMappingActions;

@SuppressWarnings("unchecked")
public class MappingActions<S, D> implements IMappingActions<S, D> {

	public MappingActions() {
	}

	public MappingActions(MappingActions<S, D> outActionOptions) {
		// Merging the configurations
		outActionOptions.actions.entrySet().forEach(entry -> this.actions.put(entry.getKey(), entry.getValue()));
	}

	// Stores all the actions according to the type
	protected Map<MappingActionsEnum, List<CallbackV2<Object, Object>>> actions = new HashMap<>();

	// Stores all the actions according to the type
	protected Map<String, Field> inlineSkippingMembers = new HashMap<>();

	/**
	 * Subscribes {@link MappingActionsEnum} actions
	 * 
	 * @param targetAction the target action to be called
	 * @param action       the action that need to be performed when the target
	 *                     matches
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

			switch (targetAction) {
				case BEFORE_MAP:
				case AFTER_MAP:

					// Normal Options Config action
					// Calling the action for each item
					action.call(src, dst);
					continue;

				case BEFORE_EACH_MAP:
				case AFTER_EACH_MAP:

					// If the source is not array, show a message and ignore the mapping
					if (!isArray.call(src)) {
						Printer.out("The Source Object is not an array to be applied the '" + targetAction.name()
								+ "' action.");
						continue;
					}

					// Casting the object to an iterable one
					final Iterator<Object> srcIterator = ((Iterable<Object>) src).iterator();

					// BEFORE_EACH_MAP Config
					if (targetAction == MappingActionsEnum.BEFORE_EACH_MAP && isArray.call(src)) {
						while (srcIterator.hasNext()) {
							// Calling the action for each item
							action.call(srcIterator.next(), null);
						}

						continue;
					}

					// Casting the object to an iterable one
					final Iterator<Object> dstIterator = ((Iterable<Object>) dst).iterator();

					// AFTER_EACH_MAP Config
					if (isArray.call(src)) {
						while (srcIterator.hasNext() && dstIterator.hasNext()) {
							// Calling the action for each item
							action.call(srcIterator.next(), dstIterator.next());
						}

						continue;
					}

					continue;
			}
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

	/**
	 * Subscribes all the members that need to be skipped in current mapping
	 * 
	 * @param members the members that needs to be skipped
	 */
	public void skipMembers(String... members) {
		for (String member : members)
			inlineSkippingMembers.put(member, null);
	}

	/**
	 * Subscribes all the members that need to be skipped in current mapping
	 * 
	 * @param members the members that needs to be skipped
	 */
	public void skipMembers(Field... members) {
		for (Field member : members)
			inlineSkippingMembers.put(member.getName(), member);
	}

	/**
	 * Checks if the member provided is registed as member to be skipped
	 * 
	 * @param member the member to be checked
	 * @return true / false
	 */
	public boolean isSkipMember(String member) {
		return inlineSkippingMembers.containsKey(member);
	}

	/**
	 * Checks if the member provided is registed as member to be skipped
	 * 
	 * @param member the member to be checked
	 * @return true / false
	 */
	public boolean isSkipMember(Field member) {
		return inlineSkippingMembers.containsValue(member);
	}
}
