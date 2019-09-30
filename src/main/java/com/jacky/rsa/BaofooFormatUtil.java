package com.jacky.rsa;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Kang Gu
 * @create 2018.4.9
 * @description 宝付专用格式的工具类
 */
public final class BaofooFormatUtil {

	private BaofooFormatUtil() {

	}

	private static final Double DOUBLE_NUM = 100.0000d;
	private static final int TWO = 2;
	private static final int SIXTEEN = 16;
	private static final int INT_VALUE = 0x00ff;
	/** ==============IS Base=================== */

	/**
	 * 判断是否为空 ,为空返回true
	 * @param arg 需要判断的参数
	 * @return boolean
	 */
	public static boolean isEmpty(Object arg) {
		return toStringTrim(arg).length() == 0 ? true : false;
	}

	/** ==============TO Base=================== */

	/**
	 * Object 转换成 String 为null 返回空字符 <br>
	 * 使用:toString(值,默认值[选填])
	 * @param args 需要转换的参数
	 * @return String
	 */
	public static String toString(Object... args) {
		String def = "";
		if (args != null) {
			if (args.length > 1) {
				def = toString(args[args.length - 1]);
			}
			Object obj = args[0];
			if (obj == null) {
				return def;
			}
			return obj.toString();
		} else {
			return def;
		}
	}

	/**
	 * Object 转换成 String[去除所以空格]; 为null 返回空字符 <br>
	 * 使用:toStringTrim(值,默认值[选填])
	 * @param args 需要转换的参数
	 * @return String
	 */
	public static String toStringTrim(Object... args) {
		String str = toString(args);
		return str.replaceAll("\\s*", "");
	}

	/** ==============Other Base=================== */

	/**
	 * 小数 转 百分数
	 * @param str 小数
	 * @return String
	 */
	public static String toPercent(Double str) {
		StringBuffer sb = new StringBuffer(Double.toString(str * DOUBLE_NUM));
		return sb.append("%").toString();
	}

	/**
	 * 百分数 转 小数
	 * @param str 百分数
	 * @return Double
	 */
	public static Double toPercent2(String str) {
		if (str.charAt(str.length() - 1) == '%') {
			return Double.parseDouble(str.substring(0, str.length() - 1)) / DOUBLE_NUM;
		}
		return 0d;
	}

	/**
	 * 将byte[] 转换成字符串
	 * @param srcBytes 需要转换的字符串
	 * @return String
	 */
	public static String byte2Hex(byte[] srcBytes) {
		StringBuilder hexRetSB = new StringBuilder();
		for (byte b : srcBytes) {
			String hexString = Integer.toHexString(INT_VALUE & b);
			hexRetSB.append(hexString.length() == 1 ? 0 : "").append(hexString);
		}
		return hexRetSB.toString();
	}

	/**
	 * 将16进制字符串转为转换成字符串
	 * @param source 需要转换的字符串
	 * @return byte[]
	 */
	public static byte[] hex2Bytes(String source) {
		byte[] sourceBytes = new byte[source.length() / TWO];
		for (int i = 0; i < sourceBytes.length; i++) {
			sourceBytes[i] = (byte) Integer.parseInt(source.substring(i * TWO, i * TWO + TWO), SIXTEEN);
		}
		return sourceBytes;
	}

	/** ============== END =================== */
	public static final class MoneyType {
		/** * 保留2位有效数字，整数位每3位逗号隔开 （默认） */
		public static final String DECIMAL = "#,##0.00";
		/** * 保留2位有效数字 */
		public static final String DECIMAL_2 = "0.00";
		/** * 保留4位有效数字 */
		public static final String DECIMAL_4 = "0.0000";
	}

    /**
     * TreeMa集合2String
     *
     * @param data TreeMa
     * @return String
     */	
    public static String coverMap2String(Map<String, String> data) {
        StringBuilder sf = new StringBuilder();
        for (String key : data.keySet()) {
            if (!isBlank(data.get(key))) {
                sf.append(key).append("=").append(data.get(key).trim()).append("&");
            }
        }
        return sf.substring(0, sf.length() - 1);
    }

    /**
     * 空值判断
     * @param cs 参数
     * @return boolean
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen = cs.length();
        if (cs == null || strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 返回参数处理
	 * @param param 参数
     * @return Map
     * @throws Exception 
     */
    public static Map<String, String> getParm(String param) throws Exception {
    	Map<String, String> dateArry = new TreeMap<String, String>();
    	String[] listObj = param.split("&");
    	for (String temp : listObj) {
    		if (temp.matches("(.+?)=(.+?)")) {
    			String[] tempListObj = temp.split("=");
    			dateArry.put(tempListObj[0], tempListObj[1]);
    		} else if (temp.matches("(.+?)=")) {
    			String[] tempListObj = temp.split("=");
    			dateArry.put(tempListObj[0], "");
    		} else {
    			throw new Exception("参数无法分解！");
    		}
    	}
    	return dateArry;
    }
    
    /**
     * 获取密钥
     * @param keyStr 密钥
     * @return String
	 * @throws Exception 抛出的异常
     */
    public static String getAesKey(String keyStr) throws Exception {
    	String[] listKeyObj = keyStr.split("\\|");
    	if (listKeyObj.length == TWO) {
    		if (!listKeyObj[1].trim().isEmpty()) {
    			return listKeyObj[1];
    		} else {
    			throw new Exception("Key is Null!");
    		}
    	} else {
    		throw new Exception("Data format is incorrect!");
    	}
    }    
}
