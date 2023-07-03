package io.java.Callback;

public interface ICallbacks {

	// With Return
	public interface I0Callback<R> {
		R call();
	}

	public interface I1Callback<T1, R> {
		R call(T1 t1);
	}

	public interface I2Callback<T1, T2, R> {
		R call(T1 t1, T2 t2);
	}

	public interface I3Callback<T1, T2, T3, R> {
		R call(T1 t1, T2 t2, T3 t3);
	}

	public interface I4Callback<T1, T2, T3, T4, R> {
		R call(T1 t1, T2 t2, T3 t3, T4 t4);
	}

	// With no Return
	public interface IV0Callback {
		void call();
	}

	public interface IV1Callback<T1 extends Object> {
		void call(T1 t1);
	}

	public interface IV2Callback<T1, T2> {
		void call(T1 t1, T2 t2);
	}

	public interface IV3Callback<T1, T2, T3> {
		void call(T1 t1, T2 t2, T3 t3);
	}

	public interface IV4Callback<T1, T2, T3, T4> {
		void call(T1 t1, T2 t2, T3 t3, T4 t4);
	}

}
