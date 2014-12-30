package org.urbanstmt.random;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.hadoop.hbase.util.Bytes;
import org.urbanstmt.util.ConstantsAndEnums;
import org.urbanstmt.util.UtilFunctions;


public class RandomTest {

		public static void main(String args[]) throws ParseException	{
			
			String a = "2014100403";
			String b = "2014101003";
			
			byte[] ab = Bytes.toBytes(a);
			byte[] bb = Bytes.toBytes(b);
			
			System.out.println(Bytes.compareTo(ab, bb));
			SimpleDateFormat format = new SimpleDateFormat(ConstantsAndEnums.CSV_IMPOR_DATETIME_FORMAT);
			format.parse("Mon Jan 20 14:38:19 UTC 2014").getTime();
			System.out.println(format.parse("Mon Jan 20 14:38:19 UTC 2014"));
			
			System.out.println(UtilFunctions.stripQuotes("ab\"c\""));
		}
}
