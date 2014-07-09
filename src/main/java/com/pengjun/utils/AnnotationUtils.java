package com.pengjun.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

public class AnnotationUtils {

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface PrintMethod {
	}

	public static String getPrintMethodValue(Class c) {

		StringBuffer sb = new StringBuffer();
		Method[] methods = c.getMethods();
		sb.append("[\n");
		try {
			Object object = c.newInstance();
			for (int i = 0; i < methods.length; i++) {
				if (methods[i].getParameterTypes().length == 0
						&& methods[i].getAnnotation(PrintMethod.class) != null) {
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
}
