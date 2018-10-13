package com.viching.generate.adapter;

import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

public interface Adapter {
	
	void adaptClass(TopLevelClass topLevelClass, DBTableJavaBean dbTable);
	
	void adaptMethod(Method method, DBTableJavaBean dbTable);
	
	void adaptFeild(Field field, DBTableJavaBean dbTable, DBColumnJavaBean dbColumn);
}
