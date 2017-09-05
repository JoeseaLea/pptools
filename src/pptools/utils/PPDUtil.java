package pptools.utils;

public class PPDUtil {
	/**
	 * 暂停一段时间
	 * @param time 暂停市场（单位:毫秒）
	 */
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
