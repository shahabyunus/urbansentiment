package org.urbanstmt.util.hbase;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanstmt.exception.TweetStorageException;
import org.urbanstmt.model.twitter.CoordinatesVO;
import org.urbanstmt.model.twitter.PlaceVO;
import org.urbanstmt.model.twitter.TweetVO;
import org.urbanstmt.util.UtilFunctions;
import org.urbanstmt.util.ConstantsAndEnums.DataDomainType;

public class HBaseUtility {

	private static final Logger LOG = LoggerFactory
			.getLogger(HBaseUtility.class);

	private static final Map<Class<?>, List<Field>> classToFieldsMap = new HashMap<Class<?>, List<Field>>();
	private static final Map<Field, List<String>> fieldToMethodsMap = new HashMap<Field, List<String>>();
	private static final Map<String, byte[]> fieldNametoByteArrayMap = new HashMap<String, byte[]>();

	public static final byte[] TWEET_COL_FAMILY = Bytes.toBytes("tweets");
	public static final byte[] TWEET_TEXT_COL = Bytes.toBytes("TEXT");
	public static final byte[] TWEET_USERNAME_COL = Bytes.toBytes("TEXT");
	public static final byte[] TWEET_USERID_COL = Bytes.toBytes("IN_R_USER_ID");
	public static final byte[] TWEET_PL_CITYNAME_COL = Bytes.toBytes("PL_NAME");
	public static final byte[] TWEET_CREATEDAT_COL = Bytes.toBytes("CREATED_AT");
	public static final byte[] TWEET_LON_COL = Bytes.toBytes("LON");
	public static final byte[] TWEET_LAT_COL = Bytes.toBytes("LAT");
	public static final byte[] DATA_TABLE_NAME = Bytes.toBytes("raw_data");

	public static final byte[] ANALYSIS_TC_COL_FAMILY = Bytes.toBytes("data");
	public static final byte[] TERM_COUNT_SCORE_COL = Bytes.toBytes("score");
	public static final byte[] TERM_COUNT_REG_LONLAT = Bytes.toBytes("lonlat");
	public static final byte[] TERM_COUNT_CREATED_COL = Bytes
			.toBytes("created");
	public static final byte[] RES_ANALYSIS_TABLE_NAME = Bytes
			.toBytes("tc_result");

	private enum GetterSetter {
		G, S
	};

	public static Object getObjectValue(String fieldType, byte[] cellValue) {
		Object fieldValue = null;

		try {

			switch (fieldType) {
			case "int":
				fieldValue = Bytes.toInt(cellValue);
				break;
			case "long":
				fieldValue = Bytes.toLong(cellValue);
				break;
			case "String":
				fieldValue = Bytes.toString(cellValue);
				break;
			case "boolean":
				fieldValue = Bytes.toBoolean(cellValue);
				break;
			case "char":
				fieldValue = Bytes.toShort(cellValue);
				break;
			default:
				break;

			}
		} catch (Exception ex) {
			LOG.error("getObjectValue()=" + ex.getMessage() + ", fieldType="
					+ fieldType + ", cellValue=" + cellValue);
			fieldValue = 0;
		}
		return fieldValue;
	}

	public static byte[] getByteValue(String fieldType, Object fieldValue) {
		byte[] cellValue = null;
		switch (fieldType) {
		case "int":
			int intValue = (int) fieldValue;
			if (intValue > 0)
				cellValue = Bytes.toBytes(intValue);
			break;
		case "long":
			long longValue = (long) fieldValue;
			if (longValue > 0)
				cellValue = Bytes.toBytes(longValue);
			break;
		case "String":
			String stringValue = (String) fieldValue;
			if (!stringValue.trim().isEmpty())
				cellValue = Bytes.toBytes(stringValue);
			break;
		case "boolean":
			Boolean value = (Boolean) fieldValue;
			if (value != null) {
				cellValue = Bytes.toBytes(value);
			}
			break;
		case "char":
			char charValue = (char) fieldValue;
			cellValue = Bytes.toBytes((short) charValue);
			break;
		case "float":
			float floatValue = (float) fieldValue;
			cellValue = Bytes.toBytes(floatValue);
			break;
		case "double":
			double doubleValue = (double) fieldValue;
			cellValue = Bytes.toBytes(doubleValue);
			break;

		default:
			break;

		}
		return cellValue;
	}

	/*
	 * public static DimensionCanonical constructDimensionObject(
	 * DimensionCanonical canonical, Result result) throws
	 * DimensionObjectConstructionException {
	 * 
	 * for (Field field : getAllFields(canonical.getClass())) { if
	 * (Modifier.isStatic(field.getModifiers())) { continue; }
	 * 
	 * String fieldName = field.getName(); String fieldType =
	 * field.getType().getSimpleName(); String methodName = getMethodName(field,
	 * GetterSetter.S);
	 * 
	 * // TODO Right now I am just copying it over as is. // Need to know if,
	 * what and how mapping should be done here. Class<?>[] param =
	 * getParamType(fieldType);
	 * 
	 * byte[] cellValue = result.getValue(
	 * HBaseUtility.PHOENIX_COLUMN_FAMILY_BYTES,
	 * getFieldNameInBytes(fieldName));
	 * 
	 * if (cellValue != null) {
	 * 
	 * Object fieldValue = getObjectValue(fieldType, cellValue);
	 * 
	 * Method m; try { m = canonical.getClass().getMethod(methodName, param);
	 * m.invoke(canonical, fieldValue); } catch (NoSuchMethodException |
	 * SecurityException e) { LOG.error("Cannot create " + canonical.getClass()
	 * + "dimension canonical object.", e); throw new
	 * DimensionObjectConstructionException(e); } catch (IllegalAccessException
	 * e) { LOG.error("Cannot create " + canonical.getClass() +
	 * "dimension canonical object.", e); throw new
	 * DimensionObjectConstructionException(e); } catch
	 * (IllegalArgumentException e) { LOG.error("Cannot create " +
	 * canonical.getClass() + "dimension canonical object.", e); throw new
	 * DimensionObjectConstructionException(e); } catch
	 * (InvocationTargetException e) { LOG.error("Cannot create " +
	 * canonical.getClass() + "dimension canonical object.", e); throw new
	 * DimensionObjectConstructionException(e); } }
	 * 
	 * }
	 * 
	 * return canonical; }
	 * 
	 * 
	 * private static Class<?>[] getParamType(String fieldType) { Class<?>[]
	 * param = new Class[1]; switch (fieldType) { case "int": param[0] =
	 * Integer.TYPE; break; case "long": param[0] = Long.TYPE; break; case
	 * "String": param[0] = String.class; break; case "char": param[0] =
	 * Character.TYPE; break; case "boolean": param[0] = Boolean.TYPE; break; }
	 * 
	 * return param; }
	 */

	public static void populateTweetPuts(TweetVO obj, Put... puts)
			throws InstantiationException, IllegalAccessException {
		List<Field> fields = getAllFields(obj.getClass(), obj, puts);
		populatePuts(obj, fields, puts);
	}

	private static void populateTweetPuts(CoordinatesVO obj, TweetVO topObj,
			Put... puts) throws InstantiationException, IllegalAccessException {
		List<Field> fields = getAllFields(obj.getClass(), topObj, puts);
		populatePuts(obj, fields, puts);
	}

	private static void populateTweetPuts(PlaceVO obj, TweetVO topObj,
			Put... puts) throws InstantiationException, IllegalAccessException {
		List<Field> fields = getAllFields(obj.getClass(), topObj, puts);
		populatePuts(obj, fields, puts);
	}

	private static void populatePuts(Object obj, List<Field> fields,
			Put... puts) throws IllegalAccessException {
		for (Field field : fields) {
			byte[] cellValue = null;
			String fieldName = field.getName();
			String fieldType = field.getType().getSimpleName();
			String methodName = getMethodName(field, GetterSetter.G);

			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			Method m = null;
			Object fieldValue = null;
			try {
				m = obj.getClass().getMethod(methodName);
				fieldValue = m.invoke(obj);
			} catch (NoSuchMethodException | SecurityException
					| IllegalArgumentException | InvocationTargetException e) {
				LOG.error(methodName + " method not found on tweet vo.", e);
				throw new IllegalStateException(e);
			}

			if (fieldValue != null) {
				cellValue = getByteValue(fieldType, fieldValue);
			}

			if (cellValue != null) {
				for (Put p : puts) {
					p.add(TWEET_COL_FAMILY, getFieldNameInBytes(fieldName),
							cellValue);
				}
			}
		}
	}

	private static List<Field> getAllFields(Class<?> c, TweetVO topObj,
			Put... puts) throws InstantiationException, IllegalAccessException {
		List<Field> fields;// = classToFieldsMap.get(c);
		//if (fields == null) {
			fields = getAllFields(new ArrayList<Field>(), c, topObj, puts);
			//classToFieldsMap.put(c, fields);
		//}

		return fields;
	}

	/*
	 * Right now this does not support unlimited nested class heirarchy. Only
	 * hardcoded till one level for Coordinates and Places
	 */
	private static List<Field> getAllFields(List<Field> fields, Class<?> c,
			TweetVO topObj, Put... puts) throws InstantiationException,
			IllegalAccessException {

		for (Field field : c.getDeclaredFields()) {

			System.out.println("field type for nested=" + field.getType()
					+ ", condition="
					+ (field.getType().equals(CoordinatesVO.class)) +  " or ="+(field.getType().equals(PlaceVO.class)));
			if (UtilFunctions.isCustomClass(field.getType())) {

				try {

					Method m = topObj.getClass().getMethod(
							getMethodName(field, GetterSetter.G));
					Object nestedObj = m.invoke(topObj);
					System.out.println("My nested expected type ="+field.getType()+", "+field.getName()+ ", "+m.getName()+", "+topObj.getId()+", "+nestedObj);
					if (nestedObj != null) {
						System.out.println("nestedObj=" + nestedObj.getClass()
								+ ", ");
						if (nestedObj instanceof CoordinatesVO) {
							populateTweetPuts((CoordinatesVO) nestedObj,
									topObj, puts);
						} else if (nestedObj instanceof PlaceVO) {
							System.out.println("place is true");
							populateTweetPuts((PlaceVO) nestedObj, topObj, puts);
						}
					}

				} catch (NoSuchMethodException | SecurityException
						| IllegalArgumentException | InvocationTargetException e) {
					LOG.error("Error in reflection while copying nest custom objects to hbase puts.");
					throw new IllegalStateException(e);
				}
			} else {
				fields.add(field);
			}
		}

		if (c.getSuperclass() != null) {
			fields = getAllFields(fields, c.getSuperclass(), topObj, puts);
		}

		return fields;
	}

	private static String getMethodName(Field field, GetterSetter type) {
		List<String> methods = fieldToMethodsMap.get(field);
		if (methods == null || methods.size() < 2) {
			methods = new ArrayList<String>();
			String fieldName = field.getName();
			String methodName = fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1, fieldName.length());
			methods.add("get" + methodName);
			methods.add("set" + methodName);
			fieldToMethodsMap.put(field, methods);
		}

		return type.equals(GetterSetter.G) ? methods.get(0) : methods.get(1);
	}

	private static byte[] getFieldNameInBytes(String fieldName) {

		byte[] byteName = fieldNametoByteArrayMap.get(fieldName);
		if (byteName == null) {
			byteName = Bytes.toBytes(fieldName.toUpperCase());
			fieldNametoByteArrayMap.put(fieldName, byteName);
		}

		return byteName;

	}
	
	public static byte[] buildRowKey(long tweetId, long creationTime,
			DataDomainType domainType) throws TweetStorageException {
		byte[] rowKey;
		try {

			byte[] time = Bytes.toBytes(creationTime);
			byte[] type = Bytes.toBytes(domainType.toString());
			byte[] id = Bytes.toBytes(tweetId);

			int keyLength =  time.length + type.length + id.length;
			rowKey = new byte[keyLength];
			ByteBuffer target = ByteBuffer.wrap(rowKey);
			target.put(time);
			target.put(type);
			target.put(id);
		} catch (Exception ex) {
			throw new TweetStorageException("Error creating id row key=" + ex);
		}
		return rowKey;
	}

}