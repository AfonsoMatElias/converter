package io.github.afonsomatelias.Options.Expression;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I2Fn;
import io.github.afonsomatelias.Callback.ICallbacks.ISetterFunction;
import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Configurations.MappingConfig;
import io.github.afonsomatelias.Enums.EMemberType;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Helpers.FieldHelper;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.MemberMapping.FieldMemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.MemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.SetterMemberMapping;

@SuppressWarnings("unchecked")
public class MappingExpression<S, D> implements IMappingExpression<S, D> {
	public MappingExpression(
		Class<S> sourceClass,
		Class<D> destinationClass,
		ConverterShared converterShared
	) {
		this.sourceClass = sourceClass;
		this.destinationClass = destinationClass;
		this.shared = converterShared;
	}

	final Class<S> sourceClass;
	final Class<D> destinationClass;
	final ConverterShared shared;

	public String getMappingName() {
		return sourceClass.getName() + ":" + destinationClass.getName();
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param transform         the interception bahavior
	 */
	public MappingExpression<S, D> forMember(
		String destinationMember, 
		I1Fn<S, Object> transform
	) {
		final Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			$$.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		// Compiler trick
		shared.forMemberMapping.put(
			field, new FieldMemberMapping(destinationMember, transform, EMemberType.SINGLE_CALLBACK)
		);

		return this;
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param transform         the interception bahavior
	 */
	public MappingExpression<S, D> forMember(
		String destinationMember, 
		I2Fn<S, MemberMapping, Object> transform
	) {
		final Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			$$.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		shared.forMemberMapping.put(
			field, new FieldMemberMapping(destinationMember, transform, EMemberType.DOUBLE_CALLBACK)
		);

		return this;
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param setterPropertyMember the member that will be transformed
	 * @param transform            the interception bahavior
	 */
	public <U> MappingExpression<S, D> forMember(
		ISetterFunction<D, U> setterPropertyMember,
		I1Fn<S, Object> transform
	) {

		// Building the unique name of the action
		final String key = this.getMappingName();
		final List<SetterMemberMapping> setters = shared.forSetterMemberMapping
			.getOrDefault(key, new ArrayList<SetterMemberMapping>());

		setters.add(new SetterMemberMapping(setterPropertyMember, transform, EMemberType.SINGLE_CALLBACK));
		shared.forSetterMemberMapping.put(key, setters);
		return this;
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param setterPropertyMember the member that will be transformed
	 * @param transform            the interception bahavior
	 */
	public <U> MappingExpression<S, D> forMember(
		ISetterFunction<D, U> setterPropertyMember,
		I2Fn<S, MemberMapping, Object> transform
	) {
		// Building the unique name of the action
		final String key = this.getMappingName();
		final List<SetterMemberMapping> setters = shared.forSetterMemberMapping.getOrDefault(key,
				new ArrayList<SetterMemberMapping>());

		setters.add(new SetterMemberMapping(setterPropertyMember, transform, EMemberType.DOUBLE_CALLBACK));
		shared.forSetterMemberMapping.put(key, setters);
		return this;
	}

	/**
	 * skips or set null to the destination member provided
	 * 
	 * @param destinationMember the member that will be transformed
	 */
	public MappingExpression<S, D> skipMember(String destinationMember) {
		final Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			$$.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		I1Fn<Object, Object> fnVoid = (o) -> null;
		shared.forMemberMapping.put(field,
				new FieldMemberMapping(destinationMember, fnVoid, EMemberType.SINGLE_CALLBACK));

		return this;
	}

	/**
	 * Subscribes a before map action for this {@link S} and
	 * {@link D} Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link MappingExpression} for chaining
	 */
	public MappingExpression<S, D> beforeMap(I2Action<S, D> mappingAction) {
		this.getMapperActions().beforeMap((I2Action<Object, Object>) mappingAction);
		return this;
	}

	/**
	 * Subscribes a after map action for this {@link S} and {@link D}
	 * Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link MappingExpression} for chaining
	 */
	public MappingExpression<S, D> afterMap(I2Action<S, D> mappingAction) {
		this.getMapperActions().afterMap((I2Action<Object, Object>) mappingAction);
		return this;
	}

	/**
	 * Used to recreate the same mapping but in reverse order
	 * 
	 * @return {@link MappingExpression} for chaining
	 */
	public MappingExpression<D, S> reverseMap() {
		shared.configurations.put(destinationClass.getName(), new MappingConfig(destinationClass, sourceClass));
		return new MappingExpression<>(destinationClass, sourceClass, shared);
	}

	/**
	 * Gets the mapper actions for the {@link S} and {@link D} Types
	 */
	private MappingObjectActions<Object, Object> getMapperActions() {
		final Map<String, MappingObjectActions<Object, Object>> globalActionOptions = shared.globalActionOptions;

		// Building the unique name of the action
		final String fieldActionOptionName = this.getMappingName();

		MappingObjectActions<Object, Object> mappingActions = globalActionOptions.getOrDefault(fieldActionOptionName, null);

		if (mappingActions == null)
			globalActionOptions.put(fieldActionOptionName, (mappingActions = new MappingObjectActions<>()));

		return mappingActions;
	}
}