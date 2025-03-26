package io.github.afonsomatelias.Options.MemberMapping;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.I2Fn;
import io.github.afonsomatelias.Core.Mappers.Mapper;
import io.github.afonsomatelias.Enums.EMemberType;

@SuppressWarnings({"unchecked", "unused"})
public class FieldMemberMapping {

	private final String member;
	private final Object callback;
	private final EMemberType type;

	public FieldMemberMapping(
		String member,
		Object callback,
		EMemberType type
	) {
		this.member = member;
		this.callback = callback;
		this.type = type;
	}
	
	/**
	 * Calls the mapping callback method to resolve the member value for the given source object.
	 * 
	 * @param source the source object from which the member value is to be resolved
	 * @param destination the destination object to which the mapped value is to be set
	 * @param processor the Processor object that triggered the mapping
	 * @return the mapped value
	 */
	public Object call(
		Object source, 
		Object destination, 
		Mapper<?> processor
	) {
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

		return memberMappingResult;
	}
}