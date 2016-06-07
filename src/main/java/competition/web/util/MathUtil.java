package competition.web.util;

public class MathUtil {
	
	/**
     * Description : 
     * This method checks if input is power of 2 using bitwise operations.
     * 
     * Ex: 8 in binary format           1 0 0 0
     *     (8-1) = 7 in binary format   0 1 1 1
     *     So, 8 & (8-1) will be        0 0 0 0
     * 
     * @param n , not null
     * @return true, if input number is power of 2.
     */
	public static boolean isPowerOf2(final int n) {
		if (n <= 0) {
			return false;
		}
		return (n & (n - 1)) == 0;
	}

}
