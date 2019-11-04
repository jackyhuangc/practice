package com.jacky.rsa;


import com.jacky.common.util.JsonUtil;
import com.jacky.common.util.LogUtil;
import com.jacky.common.util.StringUtil;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <b>Rsa加解密工具</b><br>
 * <br>
 * 公钥采用X509,Cer格式的<br>
 * 私钥采用PKCS12加密方式的PFX私钥文件<br>
 * 加密算法为1024位的RSA，填充算法为PKCS1Padding<br>
 *
 * @author 行者
 * @version 4.1.0
 */
public final class RsaCodingUtil {

    public static void main(String[] args) {

        String str =JsonUtil.toJson("{\"abc\":\"xxxx\"}");
        LogUtil.warn("开始测试...........");

        String err1 = "5449e7ab955ce09beb1b5184284334272153d670bfc57d7a20e84fadd84b5f46e8081e31866dd6a5b38dfbb465bcb0f7cb4eb5589b654312444fa03cca50dfee584bda43c6788933e3f6053464057f285774041eba9b38df6488057377e1ea5160048fb52e134be5b4d32c5f228f482f006f5d0bf69083d03019046f83594c51";
        String err2 = "4d2473a01ac81156c15af604156755afa306abad6e022f2984978580600c1dc3d9c46898eb5c8f880424a454fce1b031a85350824f0f57429dc956c05882244b5bf2b7a82b40a535a904897b8501993730e79f9885ad0dab716d16af8b7af2a1102830ceb6cb847cbc362a957d00f65ca3987df8125a475548898bbc9bcc6c8c";
        String suc1 = "81d7e301d3046afbd69de0eeb5eefe6103179df35d94d14224c708fb7555c4924d8005787e4241973d9a1ab5cdc751460f4ef6f2c845895c7792f1b054dc423f26455b2afd5fc3a61cb3b1422f6f82457d96f769203e70a5883def233fcaef9c5b1d6de1ea63bd84807d101311a5640804d6fb63faa4b390d2dab3e8293afd02";

        while(true) {
            String result1 = decryptByPriPfxFile(err1, "/1197026/private.pfx", "baofuyn");
            LogUtil.warn(String.format("result1:%s", result1));

            String result2 = decryptByPriPfxFile(err2, "/1197026/private.pfx", "baofuyn");
            LogUtil.warn(String.format("result2:%s", result2));

            String result3 = decryptByPriPfxFile(suc1, "/1197026/private.pfx", "baofuyn");
            LogUtil.warn(String.format("result3:%s", result3));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //LogUtil.warn("测试完成...........");
    }

    private RsaCodingUtil() {

    }

    // ======================================================================================
    // 公钥加密私钥解密段
    // ======================================================================================

    /**
     * 指定Cer公钥路径加密
     *
     * @param src        需要加密的内容
     * @param pubCerPath 公钥的路径
     * @return hex串
     */
    public static String encryptByPubCerFile(String src, String pubCerPath) {

        PublicKey publicKey = RsaReadUtil.getPublicKeyFromFile(pubCerPath);
        if (publicKey == null) {
            return null;
        }
        return encryptByPublicKey(src, publicKey);
    }

    /**
     * 用公钥内容加密
     *
     * @param src        需要加密的内容
     * @param pubKeyText 公钥的内容
     * @return hex串
     */
    public static String encryptByPubCerText(String src, String pubKeyText) {
        PublicKey publicKey = RsaReadUtil.getPublicKeyByText(pubKeyText);
        if (publicKey == null) {
            return null;
        }
        return encryptByPublicKey(src, publicKey);
    }

    /**
     * 公钥加密返回
     *
     * @param src       需要加密的内容
     * @param publicKey 公钥的内容
     * @return hex串
     */
    public static String encryptByPublicKey(String src, PublicKey publicKey) {
        byte[] destBytes = rsaByPublicKey(src.getBytes(), publicKey, Cipher.ENCRYPT_MODE);

        if (destBytes == null) {
            return null;
        }

        return BaofooFormatUtil.byte2Hex(destBytes);

    }

    /**
     * 根据私钥文件解密
     *
     * @param src        需要加密的内容
     * @param pfxPath    私钥文件路径
     * @param priKeyPass 私钥密码
     * @return String
     */
    public static String decryptByPriPfxFile(String src, String pfxPath, String priKeyPass) {
        if (BaofooFormatUtil.isEmpty(src) || BaofooFormatUtil.isEmpty(pfxPath)) {
            return null;
        }
        PrivateKey privateKey = RsaReadUtil.getPrivateKeyFromFile(pfxPath, priKeyPass);
        if (privateKey == null) {
            return null;
        }
        return decryptByPrivateKey(src, privateKey);
    }

    /**
     * 根据私钥文件流解密
     *
     * @param src        需要加密的内容
     * @param pfxBytes   加密的字节
     * @param priKeyPass 私钥密码
     * @return String
     */
    public static String decryptByPriPfxStream(String src, byte[] pfxBytes, String priKeyPass) {
        if (BaofooFormatUtil.isEmpty(src)) {
            return null;
        }
        PrivateKey privateKey = RsaReadUtil.getPrivateKeyByStream(pfxBytes, priKeyPass);
        if (privateKey == null) {
            return null;
        }
        return decryptByPrivateKey(src, privateKey);
    }

    /**
     * 私钥解密
     *
     * @param src        需要解密的内容
     * @param privateKey 私钥密码
     * @return String
     */
    public static String decryptByPrivateKey(String src, PrivateKey privateKey) {
        if (BaofooFormatUtil.isEmpty(src)) {
            return null;
        }

        String key = null;
        try {
            byte[] destBytes = rsaByPrivateKey(BaofooFormatUtil.hex2Bytes(src), privateKey, Cipher.DECRYPT_MODE);
            if (destBytes == null) {
                return null;
            }
            key = new String(destBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.error("解密内容不是正确的UTF8格式:", e);
        } catch (Exception e) {
            LogUtil.error("解密内容异常", e);
        }
        return key;
    }

    // ======================================================================================
    // 私钥加密公钥解密
    // ======================================================================================

    /**
     * 根据私钥文件加密
     *
     * @param src        需要加密的内容
     * @param pfxPath    私钥文件路径
     * @param priKeyPass 私钥密码
     * @return String
     */
    public static String encryptByPriPfxFile(String src, String pfxPath, String priKeyPass) {

        PrivateKey privateKey = RsaReadUtil.getPrivateKeyFromFile(pfxPath, priKeyPass);
        if (privateKey == null) {
            return null;
        }
        return encryptByPrivateKey(src, privateKey);
    }

    /**
     * 根据私钥文件流加密
     *
     * @param src        需要加密的内容
     * @param pfxBytes   加密的字节
     * @param priKeyPass 私钥密码
     * @return String
     */
    public static String encryptByPriPfxStream(String src, byte[] pfxBytes, String priKeyPass) {
        PrivateKey privateKey = RsaReadUtil.getPrivateKeyByStream(pfxBytes, priKeyPass);
        if (privateKey == null) {
            return null;
        }
        return encryptByPrivateKey(src, privateKey);
    }

    /**
     * 根据私钥加密
     *
     * @param src        需要加密的内容
     * @param privateKey 私钥密码
     * @return String
     */
    public static String encryptByPrivateKey(String src, PrivateKey privateKey) {

        byte[] destBytes = rsaByPrivateKey(src.getBytes(), privateKey, Cipher.ENCRYPT_MODE);

        if (destBytes == null) {
            return null;
        }
        return BaofooFormatUtil.byte2Hex(destBytes);
    }

    /**
     * 指定Cer公钥路径解密
     *
     * @param src        需要加密的内容
     * @param pubCerPath 公钥路径
     * @return String
     */
    public static String decryptByPubCerFile(String src, String pubCerPath) {
        PublicKey publicKey = RsaReadUtil.getPublicKeyFromFile(pubCerPath);
        if (publicKey == null) {
            return null;
        }
        return decryptByPublicKey(src, publicKey);
    }

    /**
     * 根据公钥文本解密
     *
     * @param src        需要加密的内容
     * @param pubKeyText 公钥的内容
     * @return String
     */
    public static String decryptByPubCerText(String src, String pubKeyText) {
        PublicKey publicKey = RsaReadUtil.getPublicKeyByText(pubKeyText);
        if (publicKey == null) {
            return null;
        }
        return decryptByPublicKey(src, publicKey);
    }

    /**
     * 根据公钥解密
     *
     * @param src       需要加密的内容
     * @param publicKey 公钥的内容
     * @return String
     */
    public static String decryptByPublicKey(String src, PublicKey publicKey) {

        try {
            byte[] destBytes = rsaByPublicKey(BaofooFormatUtil.hex2Bytes(src), publicKey, Cipher.DECRYPT_MODE);
            if (destBytes == null) {
                return null;
            }
            return new String(destBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
//			//log.error("解密内容不是正确的UTF8格式:", e);
        }
        return null;
    }

    // ======================================================================================
    // 公私钥算法
    // ======================================================================================

    /**
     * 公钥算法
     *
     * @param srcData   源字节
     * @param publicKey 公钥
     * @param mode      加密 OR 解密
     * @return byte[]
     */
    public static byte[] rsaByPublicKey(byte[] srcData, PublicKey publicKey, int mode) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(mode, publicKey);
            // 分段加密
            int blockSize = (mode == Cipher.ENCRYPT_MODE) ? cipher.getOutputSize(srcData.length)
                    - BaofooConst.LENGTH : cipher.getOutputSize(srcData.length);
            byte[] encryptedData = null;
            for (int i = 0; i < srcData.length; i += blockSize) {
                // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
                byte[] doFinal = cipher.doFinal(subarray(srcData, i, i + blockSize));
                encryptedData = addAll(encryptedData, doFinal);
            }
            return encryptedData;

        } catch (Exception e) {
            LogUtil.error("rsaByPublicKey方法出错", e);
        }
        return null;
    }

    /**
     * 私钥算法
     *
     * @param srcData    源字节
     * @param privateKey 私钥
     * @param mode       加密 OR 解密
     * @return byte[]
     */
    public static byte[] rsaByPrivateKey(byte[] srcData, PrivateKey privateKey, int mode) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(mode, privateKey);
            // 分段加密
//			int blockSize = (mode == Cipher.ENCRYPT_MODE) ? cipher.getOutputSize(srcData.length) -
//					BaofooConst.LENGTH : cipher.getOutputSize(srcData.length);

            int blockSize = 0;
            int cipherLength = cipher.getOutputSize(srcData.length) - BaofooConst.LENGTH;
            blockSize = (mode == Cipher.ENCRYPT_MODE) ? cipherLength : cipher.getOutputSize(srcData.length);

            byte[] decryptData = null;

            for (int i = 0; i < srcData.length; i += blockSize) {
                byte[] doFinal = cipher.doFinal(subarray(srcData, i, i + blockSize));

                decryptData = addAll(decryptData, doFinal);
            }
            return decryptData;
        } catch (Exception e) {
            LogUtil.error("rsaByPrivateKey方法出错", e);
        }
        return null;
    }

    /**
     * @param array               数组
     * @param startIndexInclusive 开始索引
     * @param endIndexExclusive   结束索引
     * @return byte[]
     */
    public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;

        if (newSize <= 0) {
            return new byte[0];
        }

        byte[] subarray = new byte[newSize];

        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);

        return subarray;
    }

    /**
     * @param array1 数组
     * @param array2 数组
     * @return byte[]
     */
    public static byte[] addAll(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * @param array 数组
     * @return byte[]
     */
    public static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }
        return (byte[]) array.clone();
    }
}
