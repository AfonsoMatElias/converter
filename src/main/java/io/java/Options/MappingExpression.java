package io.java.Options;

import java.lang.reflect.Field;
import java.util.Map;

import io.java.Callback.ICallbacks.I1Callback;
import io.java.Callback.ICallbacks.IV2Callback;
import io.java.Configurations.ConverterShared;
import io.java.Helpers.FieldHelper;
import io.java.Helpers.Printer;
import io.java.Options.Interfaces.IMappingExpression;

@SuppressWarnings("unchecked")
public class MappingExpression<S, D> implements IMappingExpression<S, D> {
	public MappingExpression(
			Class<S> sourceClass,
			Class<D> destinationClass,
			ConverterShared converterShared) {
		this.sourceClass = sourceClass;
		this.destinationClass = destinationClass;
		this.shared = converterShared;
	}

	Class<S> sourceClass;
	Class<D> destinationClass;
	ConverterShared shared;

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param transform          the interception bahavior
	 */
	public MappingExpression<S, D> forMember(String destinationMember,
			I1Callback<S, Object> transform) {
		Field field = FieldHelper.getMappedFieldsFor(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			Printer.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		// Compiler trick
		final Object transformAsObject = transform;
		shared.forMemberMapping.put(field, (I1Callback<Object, Object>) transformAsObject);

		return this;
	}

	/**
	 * Gets the mapper actions for the {@link S} and {@link D} Types
	 */
	private MappingActions<Object, Object> getMapperActions() {
		final Map<String, MappingActions<Object, Object>> globalActionOptions = shared.globalActionOptions;

		// Building the unique name of the action
		final String fieldActionOptionName = sourceClass.getName() + ":" + destinationClass.getName();

		MappingActions<Object, Object> mappingActions = globalActionOptions.getOrDefault(fieldActionOptionName, null);

		if (mappingActions == null)
			globalActionOptions.put(fieldActionOptionName, (mappingActions = new MappingActions<>()));

		return mappingActions;
	}

	/**
	 * Subscribes a before map action for this {@link S} and
	 * {@link D} Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link MappingExpression} for chaining
	 */
	public MappingExpression<S, D> beforeMap(IV2Callback<S, D> mappingAction) {
		this.getMapperActions().beforeMap((IV2Callback<Object, Object>) mappingAction);
		return this;
	}

	/**
	 * Subscribes a after map action for this {@link S} and {@link D}
	 * Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link MappingExpression} for chaining
	 */
	public MappingExpression<S, D> afterMap(IV2Callback<S, D> mappingAction) {
		this.getMapperActions().afterMap((IV2Callback<Object, Object>) mappingAction);
		return this;
	}

}