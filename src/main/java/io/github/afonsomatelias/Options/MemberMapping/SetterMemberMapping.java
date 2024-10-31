package io.github.afonsomatelias.Options.MemberMapping;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP2;
import io.github.afonsomatelias.Mapper.Processor;
import io.github.afonsomatelias.Options.Interfaces.ISetterFunction;

@SuppressWarnings("unchecked")
public class SetterMemberMapping {

	private ISetterFunction<Object, Object> setter;
	private Object callback;

	public SetterMemberMapping(
		ISetterFunction<?, ?> setter, 
		Object callback
	) {
		this.setter = (ISetterFunction<Object, Object>) setter;
		this.callback = callback;
	}

	public Object call(Object source, Object destination, Processor<?> processor) 
	{
		Object memberMappingResult = null;
				
		if (CallbackP1.class.equals(callback.getClass())) {
			memberMappingResult = ((CallbackP1<Object, Object>) callback).call(source);
		}

		if (CallbackP2.class.equals(callback.getClass())) {
			memberMappingResult = ((CallbackP2<Object, MemberMapping, Object>) callback)
					.call(source, new MemberMapping(processor));
		}

		setter.accept(destination, memberMappingResult);
		return memberMappingResult;
	}
}