package util;


public class ArrayUtils {

	public static boolean isContained(int a, int[] in) {
		for (int i = 0; i < in.length; i++) {
			if (a == in[i])
				return true;
		}
		return false;
	}

	public static int sum(int[] in) {
		int res = 0;
		for (int i = 0; i < in.length; i++) {
			res += in[i];
		}
		return res;
	}

}
