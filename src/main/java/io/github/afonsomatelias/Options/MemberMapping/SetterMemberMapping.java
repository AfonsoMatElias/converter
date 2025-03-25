package io.github.afonsomatelias.Options.MemberMapping;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP2;
import io.github.afonsomatelias.Enums.EMemberType;
import io.github.afonsomatelias.Mapper.Processor;
import io.github.afonsomatelias.Options.Interfaces.ISetterFunction;

@SuppressWarnings("unchecked")
public class SetterMemberMapping {

	private final ISetterFunction<Object, Object> member;
	private final Object callback;
	private final EMemberType type;

	public SetterMemberMapping(
			ISetterFunction<?, ?> setter,
			Object callback,
			EMemberType type) {

		this.member = (ISetterFunction<Object, Object>) setter;
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
	public Object call(Object source, Object destination, Processor<?> processor) {
		Object memberMappingResult = null;

		switch (this.type) {
			case SINGLE_CALLBACK:
				memberMappingResult = ((CallbackP1<Object, Object>) callback).call(source);
				break;
			
			case DOUBLE_CALLBACK:
				memberMappingResult = ((CallbackP2<Object, MemberMapping, Object>) callback)
						.call(source, new MemberMapping(processor));
				break;
		}

		member.accept(destination, memberMappingResult);
		return memberMappingResult;
	}
}