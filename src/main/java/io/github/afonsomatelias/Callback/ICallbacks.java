package io.github.afonsomatelias.Callback;

public interface ICallbacks {

	// Functional Interface

	// With Return
	@FunctionalInterface
	public interface IFn<R> { R call(); }

	@FunctionalInterface
	public interface I1Fn<T1, R> { R call(T1 t1); }

	@FunctionalInterface
	public interface I2Fn<T1, T2, R> { R call(T1 t1, T2 t2); }

	@FunctionalInterface
	public interface I3Fn<T1, T2, T3, R> { R call(T1 t1, T2 t2, T3 t3); }

	@FunctionalInterface
	public interface I4Fn<T1, T2, T3, T4, R> { R call(T1 t1, T2 t2, T3 t3, T4 t4); }

	// With no Return
	@FunctionalInterface
	public interface IAction { void call(); }

	@FunctionalInterface
	public interface I1Action<T1> { void call(T1 t1); }

	@FunctionalInterface
	public interface I2Action<T1, T2> { void call(T1 t1, T2 t2); }

	@FunctionalInterface
	public interface I3Action<T1, T2, T3> { void call(T1 t1, T2 t2, T3 t3); }

	// Specific
	@FunctionalInterface
	public interface ITypeResolver {
		Object resolve(Object value);		
	}

	@FunctionalInterface
	public interface ISetterFunction<ModelType, SetterType> {
		void accept(ModelType model, SetterType setterValue);
	}

}
