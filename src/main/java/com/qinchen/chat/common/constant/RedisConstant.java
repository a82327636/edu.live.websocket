package com.qinchen.chat.common.constant;

/**
 * @Description: 公共参数常量标示
 * @Param null:
 * @return: null
 * @Date: 2019-07-23
 **/

public final class RedisConstant {


	public static String getLiveUserKey(Long taskId) {
		return "LIVE_USER_" + taskId;
	}

	public static String getUserJoinKey(Long userId) {
		return "userJoin_" + userId;
	}

	/**
	 * @Description: 缓存时长相关
	 * @Author: wyb
	 * @Date: 2020-04-28
	 **/
	public static class RedisCacheTime {

		public static final int TEN_SEC = 10;// 10s

		public static final int THIRTY_SEC = 30;// 30s

		public static final int MINUTES_ONE = 60;// 一分钟

		public static final int MINUTES_FIVE = 5 * 60;// 五分钟

		public static final int MINUTES_TEN = 10 * 60;// 十分钟

		public static final int MINUTES_FIFTEEN = 15 * 60;// 十分钟

		public static final int MINUTES_THIRTY = 30 * 60;// 三十分钟

		public static final int HOURS_ONE = 60 * 60;// 一小时

		public static final int HOURS_TWO = 2 * 60 * 60;// 两小时

		public static final int DAYS_ONE = 24 * 60 * 60;// 一天

		public static final int WEEKS_ONE = 7 * 24 * 60 * 60;// 一周

		public static final int MONTHS_ONE = 30 * 24 * 60 * 60;// 一月

		public static final int YEARS_ONE = 365 * 24 * 60 * 60;// 一年
		/** token过期时效 */
		public static final int TOKEN_EXPIRE_TIME = 2 * 60 * 60;// 2个小时

	}

}
