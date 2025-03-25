package io.github.afonsomatelias;

import java.util.List;

import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Mapper.ListProcessor;
import io.github.afonsomatelias.Mapper.ObjectProcessor;
import io.github.afonsomatelias.Mapper.Interfaces.IListProcessor;
import io.github.afonsomatelias.Mapper.Interfaces.IObjectProcessor;


public abstract class Converter implements IConverter {
	/**
	 * Default Converter
	 */
	public Converter() {
		shared = new ConverterShared();
	}

	// All the public properties that will e shared between inner instances
	protected final ConverterShared shared;

	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	public <S> IObjectProcessor<S> map(S source) {
		return new ObjectProcessor<S>(shared, source);
	}

	/**
	 * Creates Mapping Processor for the {@link S} Object
	 * 
	 * @param <S>    the {@link S} Type
	 * @param source the {@link S} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <S> IListProcessor<S> map(List<S> source) {
		return new ListProcessor<S>(shared, source);
	}
}
