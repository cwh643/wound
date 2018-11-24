package com.forgpio.demo;

public class ForGpio {
	static {
		System.loadLibrary("gpiodemo");
	}
	
	
	public static native int open();
	public static native int read(int i);//
	public static native int write(int w);//
	public static native void close();
}
