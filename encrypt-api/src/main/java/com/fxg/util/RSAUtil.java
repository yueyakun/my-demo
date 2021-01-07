package com.fxg.util;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class RSAUtil {

	/**
	 * encryption algorithm RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * encryption
	 *
	 * @param data      data
	 * @param publicKey publicKey
	 * @return byte
	 * @throws Exception Exception
	 */
	public static byte[] encrypt(byte[] data, String publicKey) throws Exception {
		byte[] keyBytes = Base64Util.decode(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		byte[] enBytes = cipher.doFinal(data);
		return enBytes;
	}

	/**
	 * Decrypt
	 *
	 * @param text       text
	 * @param privateKey privateKey
	 * @return byte
	 * @throws Exception Exception
	 */
	public static byte[] decrypt(byte[] text, String privateKey) throws Exception {
		byte[] keyBytes = Base64Util.decode(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateK);
		byte[] deBytes = cipher.doFinal(text);
		return deBytes;
	}
}
