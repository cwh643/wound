
package com.dnion.app.android.injuriesapp.utils;


import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class MD5 {
	private static final String LOG_TAG = "MD5";
	private static final String ALGORITHM = "MD5";
	private static MessageDigest sDigest;

	static {
		try {
			sDigest = MessageDigest.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOG_TAG, "Get MD5 Digest failed.");
			throw new RuntimeException(ALGORITHM, e);
		}
	}

	private MD5() {
	}

	
	final public static String encode(String source) {
		byte[] btyes = source.getBytes();
		byte[] encodedBytes = sDigest.digest(btyes);

		return Utility.hexString(encodedBytes);
	}


//	public static void main(String[] args) {
//		// MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
//		System.out.println(MD5.encode(""));
//		// MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
//		System.out.println(MD5.encode("a"));
//		// MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
//		System.out.println(MD5.encode("abc"));
//	}
}
