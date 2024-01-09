package io.github.cnvtr.Callback;

public interface ICallbacks {

	// With Return
	public interface CallbackP0<R> { R call(); }

	public interface CallbackP1<T1, R> { R call(T1 t1); }

	public interface CallbackP2<T1, T2, R> { R call(T1 t1, T2 t2); }

	public interface CallbackP3<T1, T2, T3, R> { R call(T1 t1, T2 t2, T3 t3); }

	// With no Return
	public interface CallbackV0 { void call(); }

	public interface CallbackV1<T1> { void call(T1 t1); }

	public interface CallbackV2<T1, T2> { void call(T1 t1, T2 t2); }

	public interface CallbackV3<T1, T2, T3> { void call(T1 t1, T2 t2, T3 t3); }

}
