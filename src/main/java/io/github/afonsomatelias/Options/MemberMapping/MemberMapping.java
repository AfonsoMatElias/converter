package io.github.afonsomatelias.Options.MemberMapping;

import java.util.List;

import io.github.afonsomatelias.Mapper.Processor;

public class MemberMapping {
	Processor<?> processor;

	public MemberMapping(Processor<?> processor) {
		this.processor = processor;
	}

	public <S> MemberObjectMapping map(S source) {
		return new MemberObjectMapping(this);
	}

	public <S> MemberListMapping map(List<S> source) {
		return new MemberListMapping(this);
	}
}