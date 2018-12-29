package me.rhin.kingdomraiders.server.helper;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Helper {
	private static Random rnd = new Random();

	// Static helper class.

	public static int findIndexAt(String str, String substr, int n) {
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
			pos = str.indexOf(substr, pos + 1);
		return pos;
	}

	public static int RandomNum(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}
