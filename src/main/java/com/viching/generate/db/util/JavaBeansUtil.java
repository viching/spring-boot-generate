package com.viching.generate.db.util;

import java.util.Locale;

import com.viching.generate.config.ICommentGenerator;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

/**
 * 
 * @author Administrator
 *
 */
public class JavaBeansUtil {

	/**
     *  
     */
	private JavaBeansUtil() {
		super();
	}

	/**
	 * 获取get方法的规则:
	 * 
	 * eMail > geteMail() 
	 * firstName > getFirstName() 
	 * URL > getURL() 
	 * XAxis > getXAxis() 
	 * a > getA() 
	 * B > invalid - this method assumes that this is not the case. Call getValidPropertyName first. 
	 * Yaxis > invalid - this method assumes that this is not the case. Call getValidPropertyName first.
	 * 
	 * @param property
	 * @return the getter method name
	 */
	public static String getGetterMethodName(String property,
			FullyQualifiedJavaType fullyQualifiedJavaType) {
		StringBuilder sb = new StringBuilder();

		sb.append(property);
		if (Character.isLowerCase(sb.charAt(0))) {
			if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
				sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			}
		}

		if (fullyQualifiedJavaType.equals(FullyQualifiedJavaType
				.getBooleanPrimitiveInstance())) {
			sb.insert(0, "is"); //$NON-NLS-1$
		} else {
			sb.insert(0, "get"); //$NON-NLS-1$
		}

		return sb.toString();
	}

	/**
	 * 获取set方法的规则:
	 * 
	 * eMail > seteMail() 
	 * firstName > setFirstName() 
	 * URL > setURL() 
	 * XAxis > setXAxis() 
	 * a > setA() 
	 * B > invalid - this method assumes that this is not the case.Call getValidPropertyName first. 
	 * Yaxis > invalid - this method assumes that this is not the case. Call getValidPropertyName first.
	 * 
	 * @param property
	 * @return the setter method name
	 */
	public static String getSetterMethodName(String property) {
		StringBuilder sb = new StringBuilder();

		sb.append(property);
		if (Character.isLowerCase(sb.charAt(0))) {
			if (sb.length() == 1 || !Character.isUpperCase(sb.charAt(1))) {
				sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			}
		}

		sb.insert(0, "set"); //$NON-NLS-1$

		return sb.toString();
	}
	
	/**
	 * 驼峰命名
	 * @param inputString
	 * @param firstCharacterUppercase
	 * @return
	 */
	public static String getCamelCaseString(String inputString,
			boolean firstCharacterUppercase) {
		StringBuilder sb = new StringBuilder();

		boolean nextUpperCase = false;
		for (int i = 0; i < inputString.length(); i++) {
			char c = inputString.charAt(i);

			switch (c) {
			case '_':
			case '-':
			case '@':
			case '$':
			case '#':
			case ' ':
			case '/':
			case '&':
				if (sb.length() > 0) {
					nextUpperCase = true;
				}
				break;

			default:
				if (nextUpperCase) {
					sb.append(Character.toUpperCase(c));
					nextUpperCase = false;
				} else {
					sb.append(Character.toLowerCase(c));
				}
				break;
			}
		}

		if (firstCharacterUppercase) {
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		}

		return sb.toString();
	}

	/**
	 * 确保属性名称符合java规范，规则如下：
	 * 
	 * 1. If the first character is lower case, then OK 
	 * 2. If the first two characters are upper case, then OK 
	 * 3. If the first character is upper
	 * case, and the second character is lower case, then the first character
	 * should be made lower case
	 * 
	 * eMail > eMail 
	 * firstName > firstName 
	 * URL > URL 
	 * XAxis > XAxis 
	 * a > a 
	 * B > b
	 * Yaxis > yaxis
	 * 
	 * @param inputString
	 * @return the valid property name
	 */
	public static String getValidPropertyName(String inputString) {
		String answer;

		if (inputString == null) {
			answer = null;
		} else if (inputString.length() < 2) {
			answer = inputString.toLowerCase(Locale.US);
		} else {
			if (Character.isUpperCase(inputString.charAt(0))
					&& !Character.isUpperCase(inputString.charAt(1))) {
				answer = inputString.substring(0, 1).toLowerCase(Locale.US)
						+ inputString.substring(1);
			} else {
				answer = inputString;
			}
		}

		return answer;
	}
	
	public static Method getJavaBeansGetter(DBColumnJavaBean dbColumn,
			ICommentGenerator commentGenerator,
            DBTableJavaBean dbTable) {
        FullyQualifiedJavaType fqjt = dbColumn
                .getFullyQualifiedJavaType();
        String property = dbColumn.getJavaProperty();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(fqjt);
        method.setName(getGetterMethodName(property, fqjt));
        commentGenerator.addGetterComment(method,
                dbTable, dbColumn);

        StringBuilder sb = new StringBuilder();
        sb.append("return "); //$NON-NLS-1$
        sb.append(property);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }
	
	public static Field getJavaBeansField(DBColumnJavaBean dbColumn,
			ICommentGenerator commentGenerator,
            DBTableJavaBean dbTable) {
        FullyQualifiedJavaType fqjt = dbColumn
                .getFullyQualifiedJavaType();
        String property = dbColumn.getJavaProperty();
        
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(fqjt);
        field.setName(property);
        commentGenerator.addFieldComment(field,
                dbTable, dbColumn);

        return field;
    }

    public static Method getJavaBeansSetter(DBColumnJavaBean dbColumn,
    		ICommentGenerator commentGenerator,
            DBTableJavaBean dbTable) {
        FullyQualifiedJavaType fqjt = dbColumn
                .getFullyQualifiedJavaType();
        String property = dbColumn.getJavaProperty();

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(getSetterMethodName(property));
        method.addParameter(new Parameter(fqjt, property));
        commentGenerator.addSetterComment(method,
                dbTable, dbColumn);

        StringBuilder sb = new StringBuilder();
        if (dbColumn.isStringColumn()) {
            sb.append("this."); //$NON-NLS-1$
            sb.append(property);
            sb.append(" = "); //$NON-NLS-1$
            sb.append(property);
            sb.append(" == null ? null : "); //$NON-NLS-1$
            sb.append(property);
            sb.append(".trim();"); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
        } else {
            sb.append("this."); //$NON-NLS-1$
            sb.append(property);
            sb.append(" = "); //$NON-NLS-1$
            sb.append(property);
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        return method;
    }
}
