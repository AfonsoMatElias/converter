package io.github.afonsomatelias.Configurations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Options.MappingActions;

public class ConverterShared {
	public int LIMIT_CYCLE_MAPPING = 3;
	public boolean USE_MAPPING_CONFIG = false;
	public final Map<String, MapperConfig> configurations = new HashMap<>();
	public final Map<String, CallbackP1<Object, Object>> tranformations = new HashMap<>();
	public final Map<Field, CallbackP1<Object, Object>> forMemberMapping = new HashMap<>();
	public final Map<String, MappingActions<Object, Object>> globalActionOptions = new HashMap<>();
}
