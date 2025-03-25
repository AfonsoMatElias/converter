package io.github.afonsomatelias.Helpers;

import static org.junit.Assert.assertEquals;

public class MethodCallCounter {
    private int calledTimes = 0;
    
    public void call() {
        calledTimes++;
    }

    public static MethodCallCounter $new() {
        return new MethodCallCounter();
    }

    public void assertMethodCalled(int times) {
        assertEquals(this.calledTimes, times);
    }
}