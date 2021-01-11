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
		params.put("timestamp", "1610333293815");
		params.put("aesKey", "VuL0fSCfWeQzl7yUcYasqhOLlO80M365");
		params.put("aesKey", "MwhD+cSviI5AmmWPrb4LzUcaaAhIQoJt");
		params.put("nonce", "1610333293815");
		params.put("name", "xiaohuihuii");
		params.put("body", "{\"nickName\":\"小灰灰\"}");
		String sign = sign(params);
		System.out.println(sign);

//		TreeMap<String, String> params = new TreeMap<>();
//		params.put("timestamp", "1610330447793");
//		params.put("aesKey", "VuL0fSCfWeQzl7yUcYasqhOLlO80M365");
//		params.put("nonce", "1610329245678");
//		params.put("name", "xiaohuihuii");
//		params.put("body", "x3g8vB9D9fsFYdRiSCuZp7HWU5ResPiXdSXGVnjDcBveeCCT97a2WdgScafWFAnqJwzFB6fxu+mGHjv5WY/Cp5fn/0Uh7igoBmSsCI36mBI969+dA0L0aLZvkVx5y55IQXsATaMEmLIv/rjlGP6xZlVAsMDWylwLSGA1zAV9b8009xQVlSLJ+kTS9ttSB0ExOsevgkdGsIiFQYeoLx+ywduXh1b26W/QrNPmect8DgYb1fcyzrzAJlkx+Eb8rgDLuJiXbwjCYJuQKN6+RehjcSxHnMoeFQS28ex4IMIuH/c2wRWGo5NPWzHKQ174f0fK4vhFLwGvCVpIF4EGUeQhDdgibz8tWGRPkWQDIU09Nd0rBs09Mh7GYYFUXdYJHfywiMEUajPCf8ZFuvsjKYe49Q==");
//		String sign = sign(params);
//		System.out.println(sign);
	}

}
