package io.java.Mapper.Interfaces;

public interface IProcessor<S> {
	
	/**
	 * Creates a new instance of object provided, just like copy and paste with
	 * different memory address
	 * 
	 * @param <Source> the {@link S} Type
	 * @return new object instance
	 */
	S build();

}
