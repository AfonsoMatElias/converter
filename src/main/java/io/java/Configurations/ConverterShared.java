package io.java.Configurations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import io.java.Callback.ICallbacks.I1Callback;
import io.java.Options.MappingActions;

public class ConverterShared {
	public int LIMIT_CYCLE_MAPPING = 3;
	public boolean USE_MAPPING_CONFIG = false;
	public final Map<String, MapperConfig> configurations = new HashMap<>();
	public final Map<String, I1Callback<Object, Object>> tranformations = new HashMap<>();
	public final Map<Field, I1Callback<Object, Object>> forMemberMapping = new HashMap<>();
	public final Map<String, MappingActions<Object, Object>> globalActionOptions = new HashMap<>();
}
