package com.fxg.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

//@Configuration
@ConfigurationProperties(prefix = "api.security")
public class SecretKeyConfig {

    private String publicKey;
	//API加解密、签名验证、重放验证 整体开关
    private boolean open = true;
	//RSS私钥
    private String privateKey;
    //是否开启签名验证
    private boolean checkSign = true;
    //是否开启重放验证
    private boolean checkReplay = true;
    //是否打印加解密log
    private boolean showLog = true;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

	public boolean isCheckSign() {
		return checkSign;
	}

	public void setCheckSign(boolean checkSign) {
		this.checkSign = checkSign;
	}

	public boolean isCheckReplay() {
		return checkReplay;
	}

	public void setCheckReplay(boolean checkReplay) {
		this.checkReplay = checkReplay;
	}
}
