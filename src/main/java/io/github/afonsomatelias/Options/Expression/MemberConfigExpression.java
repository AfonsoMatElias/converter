package io.github.afonsomatelias.Options.Expression;

import java.util.List;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Core.ListMapper;
import io.github.afonsomatelias.Core.Mapper;
import io.github.afonsomatelias.Core.ObjectMapper;
import io.github.afonsomatelias.Core.Interfaces.IListMapper;
import io.github.afonsomatelias.Core.Interfaces.IObjectMapper;


public class MemberConfigExpression extends Converter {

	private Mapper<?> mapper;

	public MemberConfigExpression(Mapper<?> mapper) {
		super();
		this.mapper = mapper;
	}

	/**
	 * Creates Mapping Processor for the {@link Entry} Object
	 * 
	 * @param <Entry>    	the {@link Entry} Type
	 * @param entry 		the {@link Entry} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <Entry> IObjectMapper<Entry> map(Entry entry) {
		return new ObjectMapper<>(
			this, 
			shared, 
			entry, 
			mapper.getLocalActionOptions(), 
			mapper.getMappedObject()
		);
	}

	/**
	 * Creates Mapping Processor for the {@link Entry} Object
	 * 
	 * @param <Entry>    	the {@link Entry} Type
	 * @param entry 		the {@link Entry} Object to be mapped
	 * @return the Processor the where having all the all the different methods to
	 *         perform
	 */
	@Override
	public <Entry> IListMapper<Entry> map(List<Entry> entry) {
		return new ListMapper<>(
			this, 
			shared,
			entry,
			mapper.getLocalActionOptions(), 
			mapper.getMappedObject()
		);
	}
}