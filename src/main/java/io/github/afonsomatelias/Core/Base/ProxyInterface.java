package io.github.afonsomatelias.Core.Base;

import static io.github.afonsomatelias.Helpers.FieldHelper.toFields;
import static io.github.afonsomatelias.Helpers.Global.getAndResolveMethodName;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.afonsomatelias.Callback.ICallbacks.IFn;
import io.github.afonsomatelias.Configurations.ConverterShared;
import io.github.afonsomatelias.Helpers.$$;

public class ProxyInterface <Entry> extends BaseCore<Entry> {
	
	public ProxyInterface(ConverterShared shared, Entry entry, Class<?> clazz) {
		super(shared, entry);

		try {
			this.init(clazz, entry);
		} catch (Exception e) {
			$$.err("Failed the initialize the Proxy Interface. Exception Message: " + e.getMessage());
		}
	}

	private Class<?> clazz;
	private InvocationHandler handler;
	private Map<String, IFn<Object>> dataSource = new HashMap<>();


	private void init(Class<?> clazz, Entry entry) {
		this.clazz = clazz;

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
					return new ProxyInterface<>(shared, fieldOrMethodValue, methodReturnType)
						.build(); 
				}

				return fieldOrMethodValue;
			});
		}
		
		// 2. Create the InvocationHandler (The "brain" that catches all method calls)
        this.handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                
				// Resolving the name
				final String equivalentPropOrMethodName = getAndResolveMethodName(method);

				// Extacting the value
				Object value = getValue(equivalentPropOrMethodName);

                return value;
            }
        };
	} 

	private Object getValue(String equivalentName) {
		IFn<Object> fnDataSource = this.dataSource.get(equivalentName);
		return fnDataSource == null ? null : fnDataSource.call();
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
