package com.gentics.mesh.util;

import java.nio.charset.Charset;

import com.google.common.hash.Hashing;

public class ETag {

	/**
	 * Hash the given key in order to generate a uniform etag hash.
	 * 
	 * @param key
	 *            Key which should be hashed
	 * @return Computed hash
	 */
	public static String hash(String key) {
		return Hashing.crc32c().hashString(key.toString(), Charset.defaultCharset()).toString();
	}

	/**
	 * Wrap the given etag with the needed quotes and add the weak flag if needed.
	 * 
	 * @param entityTag
	 * @param isWeak
	 * @return
	 */
	public static String prepareHeader(String entityTag, boolean isWeak) {
		StringBuilder builder = new StringBuilder();
		if (isWeak) {
			builder.append("W/");
		}
		builder.append('"');
		builder.append(entityTag);
		builder.append('"');
		return builder.toString();
	}

	/**
	 * Extracts the etag from the provided header value.
	 * 
	 * @param etag
	 * @return
	 */
	public static String extract(String headerValue) {
		if (headerValue == null) {
			return null;
		}
		return headerValue.substring(headerValue.indexOf("\"") + 1, headerValue.lastIndexOf("\""));
	}

}