package com.fxg.encrypt.util;

import com.fxg.encrypt.interceptor.AESKeyHandler;
import com.fxg.encrypt.filter.RequestWrapper;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

public class SignUtil {

	private static String MD5 = "MD5";


	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance(MD5).digest(string.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MessageDigest不支持MD5Util", e);
		}
		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}


	/**
	 * md5签名
	 * <p>
	 * 按参数名称升序，将参数值进行连接 签名
	 *
	 * @param params
	 * @return
	 */
	public static String sign(TreeMap<String, String> params) {
		StringBuilder paramValues = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			paramValues.append(entry.getValue());
		}
		return md5(paramValues.toString());
	}


	/**
	 * 请求参数签名验证
	 *
	 * @param privateKey rsa私钥
	 * @param timestamp  时间戳
	 * @param nonce      随机数
	 * @param request    HttpServletRequest
	 * @return true 验签成功 false 验签失败
	 * @throws Exception
	 */
	public static boolean verifySign(String privateKey, String timestamp, String nonce, RequestWrapper request)
			throws Exception {
		TreeMap<String, String> params = new TreeMap<>();
		params.put("timestamp", timestamp);
		params.put("nonce", nonce);

		// 加密的aes秘钥
		String encryptedAesKey = request.getHeader("X_EAK");//解密aes秘钥
		byte[] aesKeyByte = RSAUtil.decrypt(Base64Util.decode(encryptedAesKey), privateKey);
		String aesKey = new String(aesKeyByte, StandardCharsets.UTF_8);
		params.put("aesKey", aesKey);
		AESKeyHandler.set(aesKey);

		//取出请求头中的签名
		String signStr = request.getHeader("X_SIGN");
		if (StringUtils.isEmpty(signStr)) {
			throw new RuntimeException("There is no SIGN field in the request header!");
		}
		//读取参数存入 treeMap
		Enumeration<String> enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paramName = enu.nextElement().trim();
			params.put(paramName, URLDecoder.decode(request.getParameter(paramName), "UTF-8"));
		}

		//读取body中的参数存入 treeMap
		String bodyString = request.getBody();
		params.put("body", bodyString);
		//验证签名
		if (sign(params).equals(signStr)) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		TreeMap<String, String> params = new TreeMap<>();
		params.put("timestamp", "1613815735447");
		params.put("nonce", "1613815735447");
		params.put("aesKey", "VuL0fSCfWeQzl7yUcYasqhOLlO80M365");
		params.put("id", "99");
		params.put("body", "{\"id\":\"99\"}");
		String sign = sign(params);
		System.out.println(sign);
	}

}
