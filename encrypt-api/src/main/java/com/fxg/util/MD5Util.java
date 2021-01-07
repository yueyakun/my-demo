package com.fxg.util;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

public class MD5Util {

	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("UTF-8 is unsupported", e);
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
	 * @param aesKey
	 * @param request
	 * @return true 验证通过 false 验证失败
	 * @throws Exception
	 */
	public static boolean verifySign(String aesKey, String timestamp, String nonce, HttpServletRequest request)
			throws Exception {
		TreeMap<String, String> params = new TreeMap<>();
		params.put("timestamp", timestamp);
		params.put("aesKey", aesKey);
		params.put("nonce", nonce);

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
		//验证签名
		if (sign(params).equals(signStr)) {
			return true;
		}
		return false;
	}

}
