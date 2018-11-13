package me.rhin.kingdomraiders.server.helper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Helper {

	public static int findIndexAt(String str, String substr, int n) {
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
			pos = str.indexOf(substr, pos + 1);
		return pos;
	}
}
