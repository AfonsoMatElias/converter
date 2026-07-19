package io.github.afonsomatelias.Options;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Enums.MappingActionsEnum;
import io.github.afonsomatelias.Options.Interfaces.IMappingActions;

public class MappingActions implements IMappingActions {
	public static class MappingEvent {
		public Class<?> targetType;
		public I2Action<Object, Object> event;

		public MappingEvent(Class<?> targetType, I2Action<Object, Object> event) {
			this.targetType = targetType;
			this.event = event;
		}
	}
	
	// Stores all the actions according to the type
	protected Map<MappingActionsEnum, List<MappingEvent>> actions = new HashMap<>();

	// Stores all the actions according to the type
	protected Map<String, Field> inlineSkippingMembers = new HashMap<>();

	// Stores all the actions according to the type
	protected Set<String> inlineSkippingTypes = new HashSet<>();

	
	/**
	 * Subscribes {@link MappingActionsEnum} actions
	 * 
	 * @param targetAction the target action to be called
	 * @param event       the action that need to be performed when the target
	 *                     matches
	 */
	public void on(MappingActionsEnum targetAction, I2Action<Object, Object> event) {
		// Defining the default List of Actions
		List<MappingEvent> mActions = new ArrayList<>();

		if (!actions.containsKey(targetAction))
			actions.put(targetAction, mActions);

		// Setting the actual list of action
		mActions = actions.get(targetAction);

		// Adding the new action
		mActions.add(new MappingEvent(null, event));
	}
	
	/**
	 * Subscribes {@link MappingActionsEnum} actions
	 * 
	 * @param targetAction the target action to be called
	 * @param event       the action that need to be performed when the target
	 *                     matches
	 */
	public void on(Class<?> target, MappingActionsEnum targetAction, I2Action<Object, Object> event) {
		// Defining the default List of Actions
		List<MappingEvent> mActions = new ArrayList<>();

		if (!actions.containsKey(targetAction))
			actions.put(targetAction, mActions);

		// Setting the actual list of action
		mActions = actions.get(targetAction);

		// Adding the new action
		mActions.add(new MappingEvent(null, event));
	}

	/**
	 * Subscribes {@link MappingActionsEnum.BEFORE_MAP} action
	 * 
	 * @implNote The destination argument will be null
	 * @param modifier the delegate having the modification
	 */
	@SuppressWarnings("unchecked")
	public <O, T> void onMemberMap(Class<T> target, I2Action<O, T> modifier) {
		this.on(target, MappingActionsEnum.MEMBER_MAP, (I2Action<Object, Object>) modifier);
	}

	/**
	 * Action caller
	 * 
	 * @param targetAction the target action to be called
	 * @param src          the source object
	 * @param dst          the destination object
	 */
	public void emit(MappingActionsEnum targetAction, Object src, Object dst) {
		this.emit(null, targetAction, src, dst);
	}

	/**
	 * Action caller
	 * 
	 * @param targetAction the target action to be called
	 * @param src          the source object
	 * @param dst          the destination object
	 */
	public void emit(Class<?> target, MappingActionsEnum targetAction, Object src, Object dst) {
		if (actions.size() == 0) return;

		// Defining the default List of Actions
		List<MappingEvent> targetActions = actions.getOrDefault(targetAction, Arrays.asList());

		// Setting the actual list of action
		for (int i = 0; i < targetActions.size(); i++) {
			MappingEvent action = targetActions.get(i);
			action.event.call(src, dst);
		}
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
	 * Subscribes all the types that need to be skipped in current mapping
	 * 
	 * @param types the types that needs to be skipped
	 */
	public void skipTypes(String... types) {
		for (String type : types)
			inlineSkippingTypes.add(type);
	}

	/**
	 * Subscribes all the types that need to be skipped in current mapping
	 * 
	 * @param types the types that needs to be skipped
	 */
	public void skipTypes(Class<?>... types) {
		for (Class<?> type : types)
			inlineSkippingTypes.add(type.getName());
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

	/**
	 * Checks if the type provided is registed as type to be skipped
	 * 
	 * @param type the type to be checked
	 * @return true / false
	 */
	public boolean isSkipType(String typeName) {
		return inlineSkippingTypes.contains(typeName);
	}

	/**
	 * Checks if the type provided is registed as type to be skipped
	 * 
	 * @param type the type to be checked
	 * @return true / false
	 */
	public boolean isSkipType(Class<?> type) {
		return inlineSkippingTypes.contains(type.getName());
	}
	
	public void merge(MappingActions mappingActions) {
		mappingActions.actions.forEach((key, value) -> {
			if (!this.actions.containsKey(key))
				this.actions.put(key, new ArrayList<MappingEvent>());

			this.actions.get(key).addAll(value);
		});

		mappingActions.inlineSkippingMembers.forEach((key, value) -> {
			this.inlineSkippingMembers.put(key, value);
		});
		
		mappingActions.inlineSkippingTypes.forEach((key) -> {
			this.inlineSkippingTypes.add(key);
		});
	}
}
