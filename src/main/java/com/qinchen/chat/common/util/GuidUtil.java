package com.qinchen.chat.common.util;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.UUID;

/**
 * Created by bxl on 2015/9/18.
 */
public class GuidUtil {
	static final SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	public static int num = 0;
	private static Object obj = new Object();
	public static String fnstr = "00000000000000";
	private static char[] codeSequence = { '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	private static String worker = ConfigUtil.getStringValue("system.id.worker");
	private static String data = ConfigUtil.getStringValue("system.id.data");


	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}


	private static String getTimestapId() {
		String id = GuidUtil.getUUID();
		id = df.format(System.currentTimeMillis()) + id.substring(14);
		return id;
	}


	public static String getFileName() {
		// SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
		synchronized (obj) {
			/*
			 * if (!df.format(new Date()).equals(fnstr.substring(0, 14))) { num = 0; }
			 */
			if (num >= 99999) {
				num = 0;
			}
			num++;
		}
		fnstr = df.format(System.currentTimeMillis()) + new DecimalFormat("00000").format(num);
		return fnstr;
	}

	/**
	 *
	 * 随机生成验证码（数字+字母）
	 *
	 * @param len 邀请码长度
	 * @return
	 *
	 * @author ailo555
	 * @date 2016年10月23日 上午9:27:09
	 */
	public static String generateRandomStr(int len) {
		// 字符源，可以根据需要删减
		String generateSource = "23456789abcdefghgklmnpqrstuvwxyz";// 去掉1和i ，0和o
		String rtnStr = "";
		for (int i = 0; i < len; i++) {
			// 循环随机获得当次字符，并移走选出的字符
			String nowStr = String
					.valueOf(generateSource.charAt((int) Math.floor(Math.random() * generateSource.length())));
			rtnStr += nowStr;
			generateSource = generateSource.replaceAll(nowStr, "");
		}
		return rtnStr;
	}


	/**
	 * 获取随机字母长度
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomCode(int length) {
		StringBuffer code = new StringBuffer();
		for (int i = 0; i < length; i++) {
			code.append(codeSequence[new Random().nextInt(codeSequence.length)]);
		}
		return code.toString();
	}
}
