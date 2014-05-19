package com.pengjun.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class BeanUtils {

	public static String getIsAndGetMethodValue(Class c) {

		StringBuffer sb = new StringBuffer();
		Method[] methods = c.getMethods();
		sb.append("[\n");
		try {
			Object object = c.newInstance();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getName().startsWith("is")
						|| methods[i].getName().startsWith("get")
						|| methods[i].getName().startsWith("do")) {
					sb.append("\t" + methods[i].getName() + " : "
							+ methods[i].invoke(object) + "\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.append("]");
		return sb.toString();
	}

	public static String getStaticFields(Class c) {

		Field[] fields = c.getDeclaredFields();
		StringBuffer sb = new StringBuffer();
		sb.append(c.getCanonicalName() + "[" + "\n");
		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers())) {
				try {
					sb.append("\t" + field.getName() + "="
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
		sb.append(c.getCanonicalName() + "[" + "\n");
		for (Field field : fields) {

			try {
				sb.append("\t" + field.getName() + "="
						+ field.get(o).toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			sb.append("\n");
		}
		sb.append("]");
		return sb.toString();
	}
}
