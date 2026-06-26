package io.github.afonsomatelias.Options.Expression;

import static io.github.afonsomatelias.Helpers.MethodHelper.toMappedMethods;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Callback.ICallbacks.I2Fn;
import io.github.afonsomatelias.Callback.ICallbacks.IGetterMethod;
import io.github.afonsomatelias.Callback.ICallbacks.ISetterMethod;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Configurations.MappingConfig;
import io.github.afonsomatelias.Enums.MemberCallbackTypeEnum;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Helpers.FieldHelper;
import io.github.afonsomatelias.Helpers.Printer;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.MemberMapping.FieldMemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.MethodMemberMapping;

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
	 * For member subscribe wrapper
	 */
	<R> MappingExpression<S, D> forFieldMember(
		Field field,
		Object callback,
		MemberCallbackTypeEnum memberType
	) {
		// Building the unique name of the action
		final String mappingKey = this.getMappingName();

		Map<Field, FieldMemberMapping> fieldsMemberMapping = shared.forMemberFieldMapping.getOrDefault(
			mappingKey, 
			new HashMap<>()
		);

		// Compiler trick
		fieldsMemberMapping.put(
			field, new FieldMemberMapping(field.getName(), callback, memberType)
		);
		
		// Compiler trick
		shared.forMemberFieldMapping.put(mappingKey, fieldsMemberMapping);

		return this;
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param callback          the interception bahavior
	 */
	public MappingExpression<S, D> forMember(
		String destinationMember, 
		I1Fn<S, Object> callback
	) {
		Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			$$.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		return this.forFieldMember(field, callback, MemberCallbackTypeEnum.SINGLE);
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param destinationMember the member that will be transformed
	 * @param callback          the interception bahavior
	 */
	public MappingExpression<S, D> forMember(
		String destinationMember, 
		I2Fn<S, MemberConfigExpression, Object> callback
	) {
		Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			$$.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		return this.forFieldMember(field, callback, MemberCallbackTypeEnum.DOUBLE);
	}

	private Method getMethod(Object anounymous, Method writeReplace)
		throws IllegalAccessException, 
				IllegalArgumentException, 
				InvocationTargetException, 
				NoSuchMethodException, 
				SecurityException {
		writeReplace.setAccessible(true);

		SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(anounymous);
		final String methodName = serializedLambda.getImplMethodName();
		return toMappedMethods(destinationClass).getOrDefault(methodName, null);
	}

	private <R> Method toGetterMethod(IGetterMethod<D, R> method) {
		try {
			// Force the lambda to serialize, which exposes its internal metadata
			return this.getMethod(
				method, method.getClass().getDeclaredMethod("writeReplace")
			);
		} catch (Exception e) {
			Printer.err("Failed to extract the method for the proveded getter. Error: " + e.getMessage());
			return null;
		}
	}

	private <R> Method toSetterMethod(ISetterMethod<D, R> method) {
		try {
			// Force the lambda to serialize, which exposes its internal metadata
			return this.getMethod(
				method, method.getClass().getDeclaredMethod("writeReplace")
			);
		} catch (Exception e) {
			Printer.err("Failed to extract the method for the proveded setter. Error: " + e.getMessage());
			return null;
		}
	}

	/**
	 * For member subscribe wrapper
	 */
	<R> MappingExpression<S, D> forMethodMember(
		Method method,
		Object getter,
		Object callback,
		MemberCallbackTypeEnum memberType
	) {
		// Building the unique name of the action
		final String mappingKey = this.getMappingName();

		Map<Method, MethodMemberMapping> getters = shared.forMemberMethodMapping
			.getOrDefault(mappingKey, new HashMap<>());

		getters.put(
			method, new MethodMemberMapping(method, getter, callback, memberType)
		);
		
		shared.forMemberMethodMapping.put(mappingKey, getters);
		return this;
	}

	/**
	 * Changes or Mutates the value that needs to be returned from the method
	 * 
	 * @param getter 	the member that will be transformed
	 * @param callback	the interception bahavior
	 */
	public <R> MappingExpression<S, D> forMember(
		IGetterMethod<D, R> getter,
		I1Fn<S, Object> callback
	) {
		Method method = this.toGetterMethod(getter);
		return this.forMethodMember(
			method, 
			getter, 
			callback, 
			MemberCallbackTypeEnum.SINGLE
		);
	}

	/**
	 * Changes or Mutates the value that needs needs to be returned from the method
	 * 
	 * @param getter the member that will be transformed
	 * @param callback            the interception bahavior
	 */
	public <R> MappingExpression<S, D> forMember(
		IGetterMethod<D, R> getter,
		I2Fn<S, MemberConfigExpression, Object> callback
	) {
		Method method = this.toGetterMethod(getter);
		return this.forMethodMember(method, getter, callback, MemberCallbackTypeEnum.DOUBLE);
	}
	
	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param setter 	the member that will be transformed
	 * @param callback	the interception bahavior
	 */
	public <U> MappingExpression<S, D> forMember(
		ISetterMethod<D, U> setter,
		I1Fn<S, Object> callback
	) {
		Method method = this.toSetterMethod(setter);
		return this.forMethodMember(
			method, 
			setter, 
			callback, 
			MemberCallbackTypeEnum.SINGLE
		);
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param setter 	the member that will be transformed
	 * @param callback the interception bahavior
	 */
	public <U> MappingExpression<S, D> forMember(
		ISetterMethod<D, U> setter,
		I2Fn<S, MemberConfigExpression, Object> callback
	) {
		Method method = this.toSetterMethod(setter);
		return this.forMethodMember(
			method, 
			setter, 
			callback, 
			MemberCallbackTypeEnum.DOUBLE
		);
	}

	/**
	 * skips or set null to the destination member provided
	 * 
	 * @param destinationMember the member that will be transformed
	 */
	public MappingExpression<S, D> skipMember(String destinationMember) {
		Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			$$.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		return this.forFieldMember(
			field,
			(I1Fn<Object, Object>) o -> null, 
			MemberCallbackTypeEnum.SINGLE
		);
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
		Map<String, MappingObjectActions<Object, Object>> globalActionOptions = shared.globalActionOptions;

		// Building the unique name of the action
		String fieldActionOptionName = this.getMappingName();

		MappingObjectActions<Object, Object> mappingActions = globalActionOptions.getOrDefault(fieldActionOptionName, null);

		if (mappingActions == null)
			globalActionOptions.put(fieldActionOptionName, (mappingActions = new MappingObjectActions<>()));

		return mappingActions;
	}
}