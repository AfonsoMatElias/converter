package io.github.afonsomatelias.Options.MemberMapping;

import static io.github.afonsomatelias.Helpers.Global.getAndResolveMethodName;

import java.lang.reflect.Method;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I2Fn;
import io.github.afonsomatelias.Callback.ICallbacks.IGetterMethod;
import io.github.afonsomatelias.Core.Mappers.Mapper;
import io.github.afonsomatelias.Enums.EMemberType;
import io.github.afonsomatelias.Helpers.FieldHelper;

@SuppressWarnings({"unused", "unchecked"})
public class GetterMemberMapping {

	private final Method method;
	private final IGetterMethod<Object, Object> member;
	private final Object callback;
	private final EMemberType type;
	private EMemberType field;

	public GetterMemberMapping(
		Method method,
		IGetterMethod<?, ?> setter,
		Object callback,
		EMemberType type) {

		this.method = method;
		this.member = (IGetterMethod<Object, Object>) setter;
		this.callback = callback;
		this.type = type;
	}

	/**
	 * Calls the mapping callback method to resolve the member value and apply the result to the setter function.
	 * 
	 * @param source the source object from which the member value is to be resolved
	 * @param destination the destination object to which the mapped value is to be set
	 * @param processor the Processor object that triggered the mapping
	 * @return the mapped value
	 */
	public Object call(Object source, Object destination, Mapper<?> processor) {
		Object memberMappingResult = null;

		switch (this.type) {
			case SINGLE_CALLBACK:
				memberMappingResult = ((I1Fn<Object, Object>) callback).call(source);
				break;
			
			case DOUBLE_CALLBACK:
				memberMappingResult = ((I2Fn<Object, MemberMapping, Object>) callback)
						.call(source, new MemberMapping(processor));
				break;
		}

		
		// Try to find the field
		final String equivalentPropOrMethodName = getAndResolveMethodName(method);

		Boolean valueSuccessfullySet = FieldHelper.setValue(
			destination, 
			equivalentPropOrMethodName, 
			memberMappingResult
		);
		
		if (!valueSuccessfullySet) {

		}

		// Otherwise, the try to find the proxyInterface


		// transform.

		// No need to call the get method

		// member.accept(destination, memberMappingResult);

		return memberMappingResult;
	}
}