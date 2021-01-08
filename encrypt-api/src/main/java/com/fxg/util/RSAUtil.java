package com.fxg.util;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
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
	 * RSA Maximum Encrypted Plaintext Size
	 */
	private static final int MAX_ENCRYPT_BLOCK = 53;

	/**
	 * RSA Maximum decrypted ciphertext size
	 */
	private static final int MAX_DECRYPT_BLOCK = 64;

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
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// Sectional Encryption of Data
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
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
		int inputLen = text.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// Sectional Encryption of Data
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(text, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(text, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	public static void main(String[] args) {
		String pubKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMbFUgBEsev1lURtNFgfr0jtz4IDJ6MEy"
				+ "IkA2WMG57bPfSsT4Pei7bxsXUCyMTXQbaxV0SThX802gxrpTEBAbJsCAwEAAQ==";

		String priKey = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAxsVSAESx6/WVRG00WB+vSO"
				+ "3PggMnowTIiQDZYwbnts99KxPg96LtvGxdQLIxNdBtrFXRJOFfzTaDGulMQEBsmwIDAQABAkEAqG6gM9YCJn5tx"
				+ "BP9nQcMU3IgunzN45e0DlQH4aACTac6JHPTZAA1STxdgTosdDBhrC1HA2pPlRzCuCAh3MpvgQIhAOxTENdAAiQPspaF"
				+ "WAvGJZhN767g9LFGUVdabvf0mCC7AiEA11HZRiSpICXO2U1MrYsLrTJMHrQQvCM/mOhW4UullaECIDs/7DX7T04ZPW4"
				+ "tilCRYjWYPKJ8tfyII7ah7rZt9YInAiBSdJSY6OcfWXsp+hEYEDxLegxuYZRbB8COBMNoiXiCoQIgMls9U5YPlGQ3aj"
				+ "DUhFACFIUNpGQl8l2faxPy/yRoV6o=";

		String aesKey = "ceshi";

		System.out.println("aesKey:" + aesKey);
		try {
			byte[] encrypt = encrypt(aesKey.getBytes(), pubKey);
			String encryptAesKey = Base64Util.encode(encrypt);
			System.out.println("encryptAesKey:" + encryptAesKey);
			//			System.out.println("encryptAesKey:"+ new String(Base64Util.decode(encryptAesKey.replace("\\r","").replace("\\n",""))));
			byte[] decrypt = decrypt(Base64Util.decode(encryptAesKey), priKey);
			String decryptAesKey = new String(decrypt);
			System.out.println("decryptAesKey:" + decryptAesKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
