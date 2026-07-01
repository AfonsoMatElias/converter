package io.github.afonsomatelias.Core.Base;

import static io.github.afonsomatelias.Helpers.FieldHelper.toFields;
import static io.github.afonsomatelias.Helpers.Global.PRIMITIVE_MAPPER;
import static io.github.afonsomatelias.Helpers.Global.getAndResolveMethodName;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Converter;
import io.github.afonsomatelias.Callback.ICallbacks.IFn;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Core.Mapper;
import io.github.afonsomatelias.Helpers.$$;
import io.github.afonsomatelias.Options.MappingActions;
import io.github.afonsomatelias.Options.Expression.MemberConfigExpression;
import io.github.afonsomatelias.Options.MemberMapping.MethodMemberMapping;

public class ProxyInterface <Entry> extends Mapper<Entry> {
	
	public ProxyInterface(
		Converter converter, 
		ConverterShared shared, 
		Entry entry, 
		Class<?> clazz
	) {
		super(
			converter, 
			shared, 
			(Entry) entry, 
			new MappingActions(),
			new HashMap<String, Object>()
		);
		
		try {
			_this = this;
			this.init(clazz, entry);
		} catch (Exception e) {
			$$.err("Failed the initialize the Proxy Interface. Exception Message: " + e.getMessage());
		}
	}

	private Entry entry;
	private Class<?> clazz;
	private InvocationHandler handler;
	private Map<String, IFn<Object>> dataSource = new HashMap<>();
	private ProxyInterface<Entry> _this;
	
	public String getMappingName() {
		return entry.getClass().getName() + ":" + clazz.getName();
	}

	private void init(Class<?> clazz, Entry entry) {
		this.clazz = clazz;
		this.entry = entry;

		List<Field> sourceFields = Arrays.asList(toFields(entry.getClass()));
		List<Method> sourceMethods = Arrays.asList(entry.getClass().getDeclaredMethods());
		
		// 1. Create the map of methods
		for (Method method : clazz.getDeclaredMethods()) {
			final String interfaceMethodName = method.getName();

			if (
				interfaceMethodName.equals("toString") || 
				interfaceMethodName.equals("hashCode") || 
				interfaceMethodName.startsWith("set")
			) {
				continue;
			}

			// From the interface
			final String equivalentPropOrMethodName = getAndResolveMethodName(method);

			Method sourceMethod = sourceMethods.stream().filter(m -> {
				final String sourceMethodName = getAndResolveMethodName(m);
				return sourceMethodName.equals(equivalentPropOrMethodName);
			}).findFirst().orElse(null);
			
			Field sourceField = sourceFields.stream().filter(f -> {
				return f.getName().toLowerCase().equals(equivalentPropOrMethodName);
			}).findFirst().orElse(null);


			// Subscribing the data source
			this.dataSource.put(equivalentPropOrMethodName, () -> {

				Object fieldOrMethodValue = null;
				Class<?> methodReturnType = method.getReturnType();
				
				// If there is a method that matches the original source method, use it
				if (sourceMethod != null) {
					try { fieldOrMethodValue = sourceMethod.invoke(entry); } 
					catch (Exception e) {}
				}
				
				// Otherwise, check for and look for field
				if (sourceField != null) {
					try { fieldOrMethodValue = sourceField.get(entry); } 
					catch (Exception e) {}
				}

				if (fieldOrMethodValue == null)
					return null;

				// If the return type is an interface, cr
				if (methodReturnType.isInterface()) {
					return new ProxyInterface<>(converter, shared, fieldOrMethodValue, methodReturnType)
						.build(); 
				}

				if (
					PRIMITIVE_MAPPER.containsKey(fieldOrMethodValue.getClass()) &&
					!method.getReturnType().equals(fieldOrMethodValue.getClass())
				) 
					return null;

				return fieldOrMethodValue;
			});
		}
		
		// 2. Create the InvocationHandler (The "brain" that catches all method calls)
        this.handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                
				// Resolving the name
				final String equivalentPropOrMethodName = getAndResolveMethodName(method);

				Map<Method, MethodMemberMapping> forMemberMapping = shared.forMemberMethodMapping
					.getOrDefault(getMappingName(), null);
				
				MethodMemberMapping getterMemberMapping = null;

				// If there is not any mapping interception
				if (
					forMemberMapping == null || 
					(getterMemberMapping = forMemberMapping.getOrDefault(method, null)) == null
				) return getValue(equivalentPropOrMethodName);

				// Otherwise, return the intercepted value
                return getterMemberMapping.call(entry, proxy, new MemberConfigExpression(_this));
            }
        };
	} 

	private Object getValue(String equivalentName) {
		try {
			IFn<Object> fnDataSource = this.dataSource.get(equivalentName);
			return fnDataSource == null ? null : fnDataSource.call();
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <D> D build() {
		try {
			return (D) Proxy.newProxyInstance(
				this.clazz.getClassLoader(), new Class<?>[] { this.clazz }, this.handler
			);
		} catch (Exception e) {
			$$.err("Failed to instantiate the Proxy Interface. Exception Message: " + e.getMessage());
			try { return (D)this.entry; } 
			catch (Exception _e) { return null; }
		}
	}
}
