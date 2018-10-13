package com.viching.generate.converter;

import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBTableJavaBean;

public abstract class GenerateProvider {
	protected static final FullyQualifiedJavaType NEW_BUILDER_IMPORT = new FullyQualifiedJavaType(
			"org.apache.ibatis.jdbc.SQL");

	protected final String builderPrefix = "sql.";
	
	public abstract void addClassElements(TopLevelClass topLevelClass, DBTableJavaBean dbTable);
}
