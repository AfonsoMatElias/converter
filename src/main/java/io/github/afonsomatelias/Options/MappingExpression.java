package io.github.afonsomatelias.Options;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP2;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackV2;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Configurations.MapperConfig;
import io.github.afonsomatelias.Enums.MemberTypeEnum;
import io.github.afonsomatelias.Helpers.FieldHelper;
import io.github.afonsomatelias.Helpers.Printer;
import io.github.afonsomatelias.Options.Interfaces.IMappingExpression;
import io.github.afonsomatelias.Options.Interfaces.ISetterFunction;
import io.github.afonsomatelias.Options.MemberMapping.FieldMemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.MemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.SetterMemberMapping;

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

	public String getMappingName() {
		return new StringBuilder()
				.append(sourceClass.getName())
				.append(":")
				.append(destinationClass.getName())
				.toString();
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param transform         the interception bahavior
	 */
	public MappingExpression<S, D> forMember(String destinationMember, CallbackP1<S, Object> transform) {
		final Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			Printer.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		// Compiler trick
		shared.forMemberMapping.put(field,
				new FieldMemberMapping(destinationMember, transform, MemberTypeEnum.DOUBLE_CALLBACK));

		return this;
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param transform         the interception bahavior
	 */
	public MappingExpression<S, D> forMember(String destinationMember, CallbackP2<S, MemberMapping, Object> transform) {
		final Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			Printer.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		// Compiler trick
		shared.forMemberMapping.put(field,
				new FieldMemberMapping(destinationMember, transform, MemberTypeEnum.DOUBLE_CALLBACK));

		return this;
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param setterPropertyMember the member that will be transformed
	 * @param transform            the interception bahavior
	 */
	public <U> MappingExpression<S, D> forMember(ISetterFunction<D, U> setterPropertyMember,
			CallbackP1<S, Object> transform) {

		// Building the unique name of the action
		final String key = this.getMappingName();
		final List<SetterMemberMapping> setters = shared.forSetterMemberMapping.getOrDefault(key,
				new ArrayList<SetterMemberMapping>());

		setters.add(new SetterMemberMapping(setterPropertyMember, transform, MemberTypeEnum.SINGLE_CALLBACK));
		shared.forSetterMemberMapping.put(key, setters);
		return this;
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param setterPropertyMember the member that will be transformed
	 * @param transform            the interception bahavior
	 */
	public <U> MappingExpression<S, D> forMember(ISetterFunction<D, U> setterPropertyMember,
			CallbackP2<S, MemberMapping, Object> transform) {

		// Building the unique name of the action
		final String key = this.getMappingName();
		final List<SetterMemberMapping> setters = shared.forSetterMemberMapping.getOrDefault(key,
				new ArrayList<SetterMemberMapping>());

		setters.add(new SetterMemberMapping(setterPropertyMember, transform, MemberTypeEnum.DOUBLE_CALLBACK));
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
			Printer.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		CallbackP1<Object, Object> fnVoid = (o) -> null;
		shared.forMemberMapping.put(field,
				new FieldMemberMapping(destinationMember, fnVoid, MemberTypeEnum.SINGLE_CALLBACK));

		return this;
	}

	/**
	 * Gets the mapper actions for the {@link S} and {@link D} Types
	 */
	private MappingActions<Object, Object> getMapperActions() {
		final Map<String, MappingActions<Object, Object>> globalActionOptions = shared.globalActionOptions;

		// Building the unique name of the action
		final String fieldActionOptionName = this.getMappingName();

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
	public MappingExpression<S, D> beforeMap(CallbackV2<S, D> mappingAction) {
		this.getMapperActions().beforeMap((CallbackV2<Object, Object>) mappingAction);
		return this;
	}

	/**
	 * Subscribes a after map action for this {@link S} and {@link D}
	 * Types
	 * 
	 * @param mappingAction the expression that will be performed
	 * @return {@link MappingExpression} for chaining
	 */
	public MappingExpression<S, D> afterMap(CallbackV2<S, D> mappingAction) {
		this.getMapperActions().afterMap((CallbackV2<Object, Object>) mappingAction);
		return this;
	}

	/**
	 * Used to recreate the same mapping but in reverse order
	 * 
	 * @return {@link MappingExpression} for chaining
	 */
	public MappingExpression<D, S> reverseMap() {
		shared.configurations.put(destinationClass.getName(), new MapperConfig(destinationClass, sourceClass));
		return new MappingExpression<>(destinationClass, sourceClass, shared);
	}
}