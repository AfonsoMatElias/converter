package io.github.afonsomatelias.Options.Expression;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I2Fn;
import io.github.afonsomatelias.Callback.ICallbacks.IGetterMethod;
import io.github.afonsomatelias.Callback.ICallbacks.ISetterMethod;
import io.github.afonsomatelias.Callback.ICallbacks.I2Action;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Configurations.MappingConfig;
import io.github.afonsomatelias.Enums.EMemberType;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Helpers.FieldHelper;
import io.github.afonsomatelias.Helpers.Printer;
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
		Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

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
		Field field = FieldHelper.toMappedFields(destinationClass).getOrDefault(destinationMember, null);

		if (field == null) {
			$$.err("Field '" + destinationMember + "' does not exists");
			return this;
		}

		shared.forMemberMapping.put(
			field, new FieldMemberMapping(destinationMember, transform, EMemberType.DOUBLE_CALLBACK)
		);

		return this;
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

	private Method getMethod(Object anounymous, Method writeReplace) 
		throws IllegalAccessException, 
				IllegalArgumentException, 
				InvocationTargetException, 
				NoSuchMethodException, 
				SecurityException {
		writeReplace.setAccessible(true);

		SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(anounymous);
		final String methodName = serializedLambda.getImplMethodName();
		return destinationClass.getDeclaredMethod(methodName);
	}

	/**
	 * Changes or Mutates the value that needs to be returned from the method
	 * 
	 * @param getter 	the member that will be transformed
	 * @param transform	the interception bahavior
	 */
	public <R> MappingExpression<S, D> forMember(
		IGetterMethod<D, R> getter,
		I1Fn<S, Object> transform
	) {

		for (Method m : getter.getClass().getDeclaredMethods()) {
			System.out.println(m.getName() + " -> " + Arrays.toString(m.getParameterTypes()));
		}

		Method m = this.toGetterMethod(getter);

		// // Building the unique name of the action
		// String key = this.getMappingName();
		// List<SetterMemberMapping> setters = shared.forSetterMemberMapping
		// 	.getOrDefault(key, new ArrayList<SetterMemberMapping>());

		// setters.add(new SetterMemberMapping(setterPropertyMember, transform, EMemberType.SINGLE_CALLBACK));
		// shared.forSetterMemberMapping.put(key, setters);
		return this;
	}

	/**
	 * Changes or Mutates the value that needs needs to be returned from the method
	 * 
	 * @param setterPropertyMember the member that will be transformed
	 * @param transform            the interception bahavior
	 */
	public <R> MappingExpression<S, D> forMember(
		IGetterMethod<D, R> getter,
		I2Fn<S, MemberMapping, Object> transform
	) {
		Method m = this.toGetterMethod(getter);

		// // Building the unique name of the action
		// String key = this.getMappingName();
		// List<SetterMemberMapping> setters = shared.forSetterMemberMapping.getOrDefault(key,
		// 		new ArrayList<SetterMemberMapping>());

		// setters.add(new SetterMemberMapping(setterPropertyMember, transform, EMemberType.DOUBLE_CALLBACK));
		// shared.forSetterMemberMapping.put(key, setters);
		return this;
	}
	
	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param setterMethod 	the member that will be transformed
	 * @param transform	the interception bahavior
	 */
	public <U> MappingExpression<S, D> forMember(
		ISetterMethod<D, U> setterMethod,
		I1Fn<S, Object> transform
	) {
		Method method = this.toSetterMethod(setterMethod);

		// Building the unique name of the action
		String key = this.getMappingName();
		List<SetterMemberMapping> setters = shared.forSetterMemberMapping
			.getOrDefault(key, new ArrayList<SetterMemberMapping>());

		setters.add(new SetterMemberMapping(
			method, 
			setterMethod, 
			transform, 
			EMemberType.SINGLE_CALLBACK
		));
		shared.forSetterMemberMapping.put(key, setters);
		return this;
	}

	/**
	 * Changes or Mutates the value that needs to be placed into a field
	 * 
	 * @param setter 	the member that will be transformed
	 * @param transform the interception bahavior
	 */
	public <U> MappingExpression<S, D> forMember(
		ISetterMethod<D, U> setter,
		I2Fn<S, MemberMapping, Object> transform
	) {
		Method method = this.toSetterMethod(setter);

		// Building the unique name of the action
		String key = this.getMappingName();
		List<SetterMemberMapping> setters = shared.forSetterMemberMapping.getOrDefault(key,
				new ArrayList<SetterMemberMapping>());

		setters.add(new SetterMemberMapping(method, setter, transform, EMemberType.DOUBLE_CALLBACK));
		shared.forSetterMemberMapping.put(key, setters);
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
		Map<String, MappingObjectActions<Object, Object>> globalActionOptions = shared.globalActionOptions;

		// Building the unique name of the action
		String fieldActionOptionName = this.getMappingName();

		MappingObjectActions<Object, Object> mappingActions = globalActionOptions.getOrDefault(fieldActionOptionName, null);

		if (mappingActions == null)
			globalActionOptions.put(fieldActionOptionName, (mappingActions = new MappingObjectActions<>()));

		return mappingActions;
	}
}