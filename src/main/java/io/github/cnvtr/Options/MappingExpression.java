package io.github.cnvtr.Options;

import java.lang.reflect.Field;
import java.util.Map;

import io.github.cnvtr.Callback.ICallbacks.CallbackP1;
import io.github.cnvtr.Callback.ICallbacks.CallbackV2;
import io.github.cnvtr.Configurations.ConverterShared;
import io.github.cnvtr.Configurations.MapperConfig;
import io.github.cnvtr.Helpers.FieldHelper;
import io.github.cnvtr.Helpers.Printer;
import io.github.cnvtr.Options.Interfaces.IMappingExpression;
import io.github.cnvtr.Options.Interfaces.ISetterFunction;

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
	 * @param transform         the interception bahavior
	 */
	public MappingExpression<S, D> forMember(String destinationMember,
			CallbackP1<S, Object> transform) {
		Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			Printer.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		// Compiler trick
		final Object transformAsObject = transform;
		shared.forMemberMapping.put(field, (CallbackP1<Object, Object>) transformAsObject);

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

		this.getMapperActions().afterMap((src, dst) -> {
			S source = (S)src;
			D destination = (D)dst;
			U result = (U) transform.call(source);

			setterPropertyMember.accept(destination, result);
		});

		return this;
	}

	/**
	 * skips or set null to the destination member provided
	 * 
	 * @param destinationMember the member that will be transformed
	 */
	public MappingExpression<S, D> skipMember(String destinationMember) {
		Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			Printer.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		shared.forMemberMapping.put(field, (obj) -> null);

		return this;
	}

	/**
	 * Gets the mapper actions for the {@link S} and {@link D} Types
	 */
	private MappingActions<Object, Object> getMapperActions() {
		final Map<String, MappingActions<Object, Object>> globalActionOptions = shared.globalActionOptions;

		// Building the unique name of the action
		final String fieldActionOptionName = new StringBuilder()
				.append(sourceClass.getName())
				.append(":")
				.append(destinationClass.getName())
				.toString();

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