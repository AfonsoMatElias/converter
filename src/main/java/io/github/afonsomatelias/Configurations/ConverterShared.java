package io.github.afonsomatelias.Configurations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.afonsomatelias.Callback.ICallbacks.CallbackP1;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.MemberMapping.FieldMemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.SetterMemberMapping;

public class ConverterShared {
	public boolean SILENT_LOGS = false;
	public boolean USE_MAPPING_CONFIG = false;
	
	public final Map<String, MappingConfig> configurations = new HashMap<>();
	public final Map<String, CallbackP1<Object, Object>> tranformations = new HashMap<>();

	public final Map<Field, FieldMemberMapping> forMemberMapping = new HashMap<>();
	public final Map<String, List<SetterMemberMapping>> forSetterMemberMapping = new HashMap<>();
	public final Map<String, MappingObjectActions<Object, Object>> globalActionOptions = new HashMap<>();

	public final Set<String> classTypesToIgnore = new HashSet<>();
}
