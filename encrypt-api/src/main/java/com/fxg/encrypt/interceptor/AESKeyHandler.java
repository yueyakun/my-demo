package com.fxg.encrypt.interceptor;

public class AESKeyHandler {

	private static ThreadLocal<String> threadLocal = new ThreadLocal<>();


	public static String get() {
		return threadLocal.get();
	}

	public static void set(String aesKey) {
		threadLocal.set(aesKey);
	}
}
