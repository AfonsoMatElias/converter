package io.github.afonsomatelias.Configurations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.afonsomatelias.Callback.ICallbacks.I1Fn;
import io.github.afonsomatelias.Callback.ICallbacks.ITypeResolver;
import io.github.afonsomatelias.Options.MappingObjectActions;
import io.github.afonsomatelias.Options.MemberMapping.FieldMemberMapping;
import io.github.afonsomatelias.Options.MemberMapping.MethodMemberMapping;

public final class ConverterShared {
	public boolean SILENT_LOGS = false;
	public boolean USE_MAPPING_CONFIG = false;
	// public boolean USE_FIELD_FALLBACK_IN_IPROJECTION = false;
	
	public final Map<String, MappingConfig> configurations = new HashMap<>();
	public final Map<String, I1Fn<Object, Object>> tranformations = new HashMap<>();

	public final Map<String, Map<Field, FieldMemberMapping>> forMemberFieldMapping = new HashMap<>();
	public final Map<String, Map<Method, MethodMemberMapping>> forMemberMethodMapping = new HashMap<>();
	
	public final Map<String, MappingObjectActions<Object, Object>> globalActionOptions = new HashMap<>();

	public final Set<String> classTypesToIgnore = new HashSet<>();

	public final Map<Class<?>, ITypeResolver> typeResolvers = new HashMap<>();

}
