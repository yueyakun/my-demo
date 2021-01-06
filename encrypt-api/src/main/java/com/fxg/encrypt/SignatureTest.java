package com.fxg.encrypt;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 加签验签demo
 *  @Author 捡田螺的小男孩
 */
public class SignatureTest {
	//公钥字符串
	private static final String PUBLIC_KEY_STR = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDaJzVjC5K6kbS2YE2fiDs6H8pB\n" +
			"JFDGEYqqJJC9I3E0Ebr5FsofdImV5eWdBSeADwcR9ppNbpORdZmcX6SipogKx9PX\n" +
			"5aAO4GPesroVeOs91xrLEGt/arteW8iSD+ZaGDUVV3+wcEdci/eCvFlc5PUuZJou\n" +
			"M2XZaDK4Fg2IRTfDXQIDAQAB";
	//私钥字符串
	private static final String PRIVATE_KEY_STR = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBANonNWMLkrqRtLZg\n" +
			"TZ+IOzofykEkUMYRiqokkL0jcTQRuvkWyh90iZXl5Z0FJ4APBxH2mk1uk5F1mZxf\n" +
			"pKKmiArH09floA7gY96yuhV46z3XGssQa39qu15byJIP5loYNRVXf7BwR1yL94K8\n" +
			"WVzk9S5kmi4zZdloMrgWDYhFN8NdAgMBAAECgYA9bz1Bn0i68b2KfqRdgOfs/nbe\n" +
			"0XNN1DLQp2t7WDfRCg01iI1zPkZgyFVZWtI85f5/uIrLs5ArLosL1oNuqqc0nNne\n" +
			"CvJK+ZxvA98Hx3ZqYTzDnleR054YhofL5awbhSciYVic204DOG1rhSsYWMqtX7J7\n" +
			"3geoWL7TYdMfYXcCAQJBAPMMKsz6ZJh98EeQ1tDG5gpAGWFQkYNrxZDelP/LjeO0\n" +
			"TP3XkQnIpcaZoCs7V/rRGRGMWwQ2BUdc/01in89ZZ5ECQQDlx2oBc1CtOAm2UAhN\n" +
			"1xWrPkZWENQ53wTrwXO4qbTGDfBKon0AehLlGCSqxQ71aufLkNO7ZlX0IHTAlnk1\n" +
			"TvENAkAGSEQ69CXxgx/Y2beTwfBkR2/gghKg0QJUUkyLqBlMz3ZGAXJwTE1sqr/n\n" +
			"HiuSAiGhwH0ByNuuEotO1sPGukrhAkAMK26a2w+nzPL+u+hkrwKPykGRZ1zGH+Cz\n" +
			"19AYNKzFXJGgclCqiMydY5T1knBDYUEbj/UW1Mmyn1FvrciHoUG1AkAEMEIuDauz\n" +
			"JabEAU08YmZw6OoDGsukRWaPfjOEiVhH88p00veM1R37nwhoDMGyEGXVeVzNPvk7\n" +
			"cELg28MSRzCK";


	public static void main(String[] args) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException, IOException, InvalidKeySpecException {
		//原始报文
		String plain = "欢迎大家关注我的公众号，捡田螺的小男孩";
		//加签
		byte[] signatureByte = sign(plain);
		System.out.println("原始报文是:" + plain);
		System.out.println("加签结果:");
		System.out.println(new BASE64Encoder().encode(signatureByte));
		//验签
		boolean verifyResult = verify(plain, signatureByte);
		System.out.println("验签结果:" + verifyResult);
	}

	/**
	 * 加签方法
	 * @param plain
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 * @throws SignatureException
	 */
	private static byte[] sign(String plain) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, SignatureException {
		//根据对应算法，获取签名对象实例
		Signature signature = Signature.getInstance("SHA256WithRSA");
		//获取私钥，加签用的是私钥，私钥一般是在配置文件里面读的，这里为了演示方便，根据私钥字符串生成私钥对象
		PrivateKey privateKey = getPriveteKey(PRIVATE_KEY_STR);
		//初始化签名对象
		signature.initSign(privateKey);
		//把原始报文更新到对象
		signature.update(plain.getBytes("UTF-8"));
		//加签
		return signature.sign();
	}

	/**
	 * 验签方法
	 * @param plain
	 * @param signatureByte
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws IOException
	 * @throws SignatureException
	 * @throws InvalidKeySpecException
	 */
	private static boolean verify(String plain, byte[] signatureByte) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, InvalidKeySpecException {
		//获取公钥
		PublicKey publicKey = getPublicKey(PUBLIC_KEY_STR);
		//根据对应算法，获取签名对象实例
		Signature signature = Signature.getInstance("SHA256WithRSA");
		//初始化签名对象
		signature.initVerify(publicKey);
		//把原始报文更新到签名对象
		signature.update(plain.getBytes("UTF-8"));
		//进行验签
		return signature.verify(signatureByte);
	}

	private static PublicKey getPublicKey(String publicKeyStr) throws InvalidKeySpecException, IOException {
		PublicKey publicKey = null;
		try {
			java.security.spec.X509EncodedKeySpec bobPubKeySpec = new java.security.spec.X509EncodedKeySpec(
					new BASE64Decoder().decodeBuffer(publicKeyStr));
			// RSA对称加密算法
			java.security.KeyFactory keyFactory;
			keyFactory = java.security.KeyFactory.getInstance("RSA");
			// 生成公钥对象
			publicKey = keyFactory.generatePublic(bobPubKeySpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return publicKey;
	}

	private static PrivateKey getPriveteKey(String privateKeyStr) {
		PrivateKey privateKey = null;
		PKCS8EncodedKeySpec priPKCS8;
		try {
			priPKCS8 = new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(privateKeyStr));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			privateKey = keyf.generatePrivate(priPKCS8);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return privateKey;
	}
}
