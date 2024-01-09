package io.github.afonsomatelias.Mapper.Interfaces;

public interface IProcessor<S> {
	
	/**
	 * Creates a new instance of object provided, just like copy and paste with
	 * different memory address
	 * 
	 * @return new object instance
	 */
	S to();

}
