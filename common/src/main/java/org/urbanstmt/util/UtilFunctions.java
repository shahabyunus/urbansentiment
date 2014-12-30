package org.urbanstmt.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.urbanstmt.model.twitter.CoordinatesVO;
import org.urbanstmt.model.twitter.PlaceVO;

public class UtilFunctions {

	private final static Set<Class<?>> customClasses = createCustomClassSet();

	private static Set<Class<?>> createCustomClassSet() {
		Set<Class<?>> customClasses = new HashSet<Class<?>>();
		customClasses.add(PlaceVO.class);
		customClasses.add(CoordinatesVO.class);
		return customClasses;
	}

	public static Date createDate(String dateString, String format,
			TimeZone tzone) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(tzone);

		return formatter.parse(dateString);
	}

	public static boolean isCustomClass(Class<?> c) {
		return customClasses.contains(c);
	}

	public static String stripQuotes(String s) {
		if (!StringUtils.isEmpty(s)) {
			if (s.charAt(0) == '"') {
				s = s.substring(1);
			}

			int l = s.length();
			if (l >= 1) {
				if (s.charAt(l - 1) == '"') {
					s = s.substring(0, l - 1);
				}
			}
		}

		return s;
	}

}
