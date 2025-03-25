package io.github.afonsomatelias.Options.Interfaces;

import java.lang.reflect.Field;

public interface IMappingActions {
    /**
	 * skips member that do not need to be mapped
	 * 
	 * @param members the member that will be skipped
	 */
	void skipMembers(String... members);

    /**
	 * skips member that do not need to be mapped
	 * 
	 * @param members the member that will be skipped
	 */
	void skipMembers(Field... members);

	/**
	 * Subscribes all the types that need to be skipped in current mapping
	 * 
	 * @param types the types that needs to be skipped
	 */
	public void skipTypes(String... types);

	/**
	 * Subscribes all the types that need to be skipped in current mapping
	 * 
	 * @param types the types that needs to be skipped
	 */
	public void skipTypes(Class<?>... types);
}
