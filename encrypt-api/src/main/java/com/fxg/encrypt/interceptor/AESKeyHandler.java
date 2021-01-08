package com.fxg.encrypt.interceptor;

/**
 * 用来向后面的RequestBodyAdvice和ResponseBodyAdvice传递解密的aesKey
 */
public class AESKeyHandler {

	private static ThreadLocal<String> threadLocal = new ThreadLocal<>();


	public static String get() {
		return threadLocal.get();
	}

	public static void set(String aesKey) {
		threadLocal.set(aesKey);
	}

	public static void remove() {
		threadLocal.remove();
	}
}
