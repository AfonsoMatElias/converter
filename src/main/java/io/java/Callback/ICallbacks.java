package io.java.Callback;

public interface ICallbacks {

	// With return
	public interface I0Callback<Return> {
		Return call();
	}

	public interface I1Callback<T1, Return> {
		Return call(T1 t1);
	}

	public interface I2Callback<T1, T2, Return> {
		Return call(T1 t1, T2 t2);
	}

	public interface I3Callback<T1, T2, T3, Return> {
		Return call(T1 t1, T2 t2, T3 t3);
	}

	public interface I4Callback<T1, T2, T3, T4, Return> {
		Return call(T1 t1, T2 t2, T3 t3, T4 t4);
	}

	// With no return
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
