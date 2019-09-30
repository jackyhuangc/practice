package com.jacky.rsa;

import com.jacky.common.util.LogUtil;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

/**
 * <b>公私钥读取工具</b><br>
 * <br>
 * 
 * @author 行者
 * @version 4.1.0
 */
public final class RsaReadUtil {

	private static final int ONE_HUNDRED = 100;

	private RsaReadUtil() {

	}
	/**
	 * 根据Cer文件读取公钥
	 * 
	 * @param pubCerPath 公钥路径
	 * @return PublicKey 公钥
	 */
	public static PublicKey getPublicKeyFromFile(String pubCerPath) {

		InputStream inputStream = null;
		try {
			inputStream = RsaReadUtil.class.getResourceAsStream(pubCerPath);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[ONE_HUNDRED];
			int rc = 0;
			while ((rc = inputStream.read(buff, 0, ONE_HUNDRED)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] in2b = swapStream.toByteArray();
			return getPublicKeyByText(new String(in2b));
		} catch (FileNotFoundException e) {
			LogUtil.error("公钥文件不存在:", e);
		} catch (IOException e) {
			LogUtil.error("公钥文件读取失败:", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					LogUtil.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	/**
	 * 根据公钥Cer文本串读取公钥
	 * 
	 * @param pubKeyText 公钥的内容
	 * @return PublicKey
	 */
	public static PublicKey getPublicKeyByText(String pubKeyText) {
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
			BufferedReader br = new BufferedReader(new StringReader(pubKeyText));
			String line = null;
			StringBuilder keyBuffer = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("-")) {
					keyBuffer.append(line);
				}
			}
			Certificate certificate = certificateFactory.generateCertificate(new
					ByteArrayInputStream(new BASE64Decoder().decodeBuffer(keyBuffer.toString())));
			return certificate.getPublicKey();
		} catch (Exception e) {
			LogUtil.error( "解析公钥内容失败:", e);
		}
		return null;
	}

	/**
	 * 根据私钥路径读取私钥
	 * 
	 * @param pfxPath 私钥路径
	 * @param priKeyPass 私钥密码
	 * @return PrivateKey
	 */
	public static PrivateKey getPrivateKeyFromFile(String pfxPath, String priKeyPass) {

		InputStream inputStream = null;

		try {
			inputStream = RsaReadUtil.class.getResourceAsStream(pfxPath);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[ONE_HUNDRED];
			int rc = 0;
			while ((rc = inputStream.read(buff, 0, ONE_HUNDRED)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			byte[] in2b = swapStream.toByteArray();
			return getPrivateKeyByStream(in2b, priKeyPass);
		} catch (Exception e) {
			LogUtil.error( "解析文件，读取私钥失败:", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
					LogUtil.error( e.getMessage(), e);
				}
			}
		}
		return null;
	}

	/**
	 * 根据PFX私钥字节流读取私钥
	 * 
	 * @param pfxBytes 私钥文件路径
	 * @param priKeyPass 私钥密码
	 * @return PrivateKey
	 */
	public static PrivateKey getPrivateKeyByStream(byte[] pfxBytes, String priKeyPass) {
		try {
			KeyStore ks = KeyStore.getInstance("PKCS12");
			char[] charPriKeyPass = priKeyPass.toCharArray();
			ks.load(new ByteArrayInputStream(pfxBytes), charPriKeyPass);
			Enumeration<String> aliasEnum = ks.aliases();
			String keyAlias = null;
			if (aliasEnum.hasMoreElements()) {
				keyAlias = (String) aliasEnum.nextElement();
			}
			return (PrivateKey) ks.getKey(keyAlias, charPriKeyPass);
		} catch (IOException e) {
			LogUtil.error( "解析文件，读取私钥失败:", e);
		} catch (KeyStoreException e) {
			LogUtil.error( "私钥存储异常:", e);
		} catch (NoSuchAlgorithmException e) {
			LogUtil.error("不存在的解密算法:", e);
		} catch (CertificateException e) {
			LogUtil.error("证书异常:", e);
		} catch (UnrecoverableKeyException e) {
			LogUtil.error( "不可恢复的秘钥异常:", e);
		}
		return null;
	}
}
