package io.github.afonsomatelias.Options.MemberMapping;

import static io.github.afonsomatelias.Helpers.Global.getAndResolveMethodName;

import java.lang.reflect.Method;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I2Fn;
import io.github.afonsomatelias.Callback.ICallbacks.ISetterMethod;
import io.github.afonsomatelias.Enums.MemberCallbackTypeEnum;
import io.github.afonsomatelias.Helpers.FieldHelper;
import io.github.afonsomatelias.Options.Expression.MemberConfigExpression;

@SuppressWarnings({"unchecked"})
public class MethodMemberMapping {
	private final Method method;

	private final Object member;
	private final Object callback;
	
	private final MemberCallbackTypeEnum type;

	public MethodMemberMapping(
		Method method,
		Object member,
		Object callback,
		MemberCallbackTypeEnum type
	) {
		this.method = method;
		this.member = member;
		
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
	public Object call(Object source, Object destination, MemberConfigExpression memberConfigExpression) {
		Object value = null;

		switch (this.type) {
			case SINGLE:
				value = ((I1Fn<Object, Object>) callback).call(source);
				break;
			
			case DOUBLE:
				value = ((I2Fn<Object, MemberConfigExpression, Object>) callback)
						.call(source, memberConfigExpression);
				break;
		}

		

		// We assume that it the method has void as return type 
		// and the method name start with 'set' is a Setter 
		Boolean isSetter = (
			Void.class.equals(method.getReturnType()) || 
			void.class.equals(method.getReturnType())
		) && method.getName().startsWith("set");
		
		value = isSetter
			// Apply setter  
			? this.handlerSetter(value, destination) 
			// Apply getter  
			: this.handlerGetter(value, destination); 
		
		return value;
	}

	Object handlerGetter(Object value, Object destination) {
		// If it is an interface, just return the value,
		// because it is handled by ProxyInterface 
		if (method.getDeclaringClass().isInterface())
			return value;
		
		// Try to find the field
		final String equivalentPropOrMethodName = getAndResolveMethodName(method);
		
		// Tryign to set the value
		FieldHelper.setValue(
			destination, 
			equivalentPropOrMethodName, 
			value
		);

		return value;
	}

	Object handlerSetter(Object value, Object destination) {
		ISetterMethod<Object, Object> mMember = ((ISetterMethod<Object, Object>)member);
		mMember.set(destination, value);
		return value;
	}
}