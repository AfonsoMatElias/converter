package io.java.Mapper.Interfaces;

public interface IProcessor<Source> {
	
	/**
	 * Creates a new instance of object provided, just like copy and paste with
	 * different memory address
	 * 
	 * @param <Source> the {@link Source} Type
	 * @return new object instance
	 */
	Source build();

}
