package com.viching.generate.config;

import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

public interface JavaTypeResolver {

    /**
     * 
     * @param introspectedColumn
     * @return
     */
    FullyQualifiedJavaType calculateJavaType(
            DBColumnJavaBean introspectedColumn, DBTableJavaBean dbTable);

    /**
     * 
     * @param introspectedColumn
     * @return
     */
    String calculateJdbcTypeName(DBColumnJavaBean introspectedColumn);
}
