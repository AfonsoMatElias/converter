package io.github.afonsomatelias;

import java.util.List;

import io.github.afonsomatelias.Mapper.Interfaces.IListProcessor;
import io.github.afonsomatelias.Mapper.Interfaces.IObjectProcessor;

public interface IConverter {
	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<S> IObjectProcessor<S> map(S source);

	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	<S> IListProcessor<S> map(List<S> source);
}
