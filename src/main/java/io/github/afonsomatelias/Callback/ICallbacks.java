package io.github.afonsomatelias.Callback;

public interface ICallbacks {

	// With Return
	public interface IFn<R> { R call(); }

	public interface I1Fn<T1, R> { R call(T1 t1); }

	public interface I2Fn<T1, T2, R> { R call(T1 t1, T2 t2); }

	public interface I3Fn<T1, T2, T3, R> { R call(T1 t1, T2 t2, T3 t3); }

	public interface I4Fn<T1, T2, T3, T4, R> { R call(T1 t1, T2 t2, T3 t3, T4 t4); }

	// With no Return
	public interface IAction { void call(); }

	public interface I1Action<T1> { void call(T1 t1); }

	public interface I2Action<T1, T2> { void call(T1 t1, T2 t2); }

	public interface I3Action<T1, T2, T3> { void call(T1 t1, T2 t2, T3 t3); }

	public interface ITypeResolver {
		Object resolve(Object value);		
	}
}
