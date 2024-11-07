package io.github.afonsomatelias.Options.MemberMapping;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Callback.ICallbacks.CallbackP2;
import io.github.afonsomatelias.Enums.MemberTypeEnum;
import io.github.afonsomatelias.Mapper.Processor;

@SuppressWarnings({"unchecked", "unused"})
public class FieldMemberMapping {

	private String member;
	private Object callback;
	private MemberTypeEnum type;

	public FieldMemberMapping(
			String member,
			Object callback,
			MemberTypeEnum type) {
		this.member = member;
		this.callback = callback;
		this.type = type;
	}

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

		return memberMappingResult;
	}
}