package io.github.afonsomatelias.Options.MemberMapping;

import java.util.List;

import io.github.afonsomatelias.Core.Mappers.Mapper;

public class MemberMapping {
	Mapper<?> processor;

	public MemberMapping(Mapper<?> processor) {
		this.processor = processor;
	}

	/**
	 * Creates a mapping processor for the given source object.
	 * 
	 * @param <S>    the type of the source object
	 * @param source the source object to be mapped
	 * @return a mapping processor for the source object
	 */
	public <S> MemberObjectMapping map(S source) {
		return new MemberObjectMapping(new Mapper<S>(processor, source));
	}

	/**
	 * Creates a mapping processor for the given list of source objects.
	 * 
	 * @param <S>    the type of the source objects
	 * @param source the list of source objects to be mapped
	 * @return a mapping processor for the list of source objects
	 */
	public <S> MemberListMapping map(List<S> source) {
		return new MemberListMapping(new Mapper<List<S>>(processor, source));
	}
}