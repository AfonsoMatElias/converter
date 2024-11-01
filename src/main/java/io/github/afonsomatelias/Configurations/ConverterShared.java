package io.github.afonsomatelias.Configurations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Options.MappingActions;
import io.github.afonsomatelias.Options.MemberMapping.FieldMemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.SetterMemberMapping;

public class ConverterShared {
	public int LIMIT_CYCLE_MAPPING = 1;
	public boolean USE_MAPPING_CONFIG = false;
	public final Map<String, MapperConfig> configurations = new HashMap<>();
	public final Map<String, CallbackP1<Object, Object>> tranformations = new HashMap<>();

	public final Map<Field, FieldMemberMapping> forMemberMapping = new HashMap<>();
	public final Map<String, List<SetterMemberMapping>> forSetterMemberMapping = new HashMap<>();
	public final Map<String, MappingActions<Object, Object>> globalActionOptions = new HashMap<>();
}
