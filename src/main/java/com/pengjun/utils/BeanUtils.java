package com.pengjun.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BeanUtils {

	public static String getStaticFields(Class c) {

		Field[] fields = c.getDeclaredFields();
		StringBuffer sb = new StringBuffer();
		sb.append(c.toString() + "[" + "\n");
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				try {
					sb.append(field.getName() + "="
							+ field.get(null).toString());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				sb.append("\n");
			}
		}
		sb.append("]" + "\n");
		return sb.toString();
	}

	public static String getAllFields(Class c, Object o) {

		Field[] fields = c.getDeclaredFields();
		StringBuffer sb = new StringBuffer();
		sb.append(c.toString() + "[" + "\n");
		for (Field field : fields) {

			try {
				sb.append(field.getName() + "=" + field.get(o).toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
