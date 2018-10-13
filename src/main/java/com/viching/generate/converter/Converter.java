package com.viching.generate.converter;

import java.util.List;

import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

public interface Converter {
	
	Method getGetter(Field field);
	
	Method getSetter(Field field);
	
	void addDefaultConstructor(TopLevelClass topLevelClass, DBTableJavaBean dbTable);
	
	Method getDefaultConstructor(TopLevelClass topLevelClass, DBTableJavaBean dbTable);
	
	List<CompilationUnit> getCompilationUnits(DBTableJavaBean dbTable);
	
	FullyQualifiedJavaType getSuperClass(DBTableJavaBean dbTable);
	
	void addParameterizedConstructor(TopLevelClass topLevelClass,
			List<DBColumnJavaBean> constructorColumns, DBTableJavaBean dbTable);
	
	List<DBColumnJavaBean> getColumnsInThisClass(DBTableJavaBean dbTable);
}
