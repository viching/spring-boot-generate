package com.viching.generate.config;

import java.util.Set;

import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.InnerClass;
import com.viching.generate.elements.java.InnerEnum;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

/**
 * 生成注释
 * @author Administrator
 *
 */
public interface ICommentGenerator {
	
	/**
	 * 
	 * @param field
	 * @param dbTable
	 * @param dbColumn
	 */
    void addFieldComment(Field field,
            DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn);

    /**
     * 
     * @param field
     * @param dbTable
     */
    void addFieldComment(Field field, DBTableJavaBean dbTable);

    /**
     * 
     * @param topLevelClass
     * @param dbTable
     */
    void addModelClassComment(TopLevelClass topLevelClass,
            DBTableJavaBean dbTable);

    /**
     * 
     * @param innerClass
     * @param dbTable
     */
    void addClassComment(InnerClass innerClass,
            DBTableJavaBean dbTable);

    /**
     * 
     * @param innerClass
     * @param dbTable
     * @param markAsDoNotDelete
     */
    void addClassComment(InnerClass innerClass,
            DBTableJavaBean dbTable, boolean markAsDoNotDelete);

    /**
     * 
     * @param innerEnum
     * @param dbTable
     */
    void addEnumComment(InnerEnum innerEnum,
            DBTableJavaBean dbTable);

    /**
     * 
     * @param method
     * @param dbTable
     * @param dbColumn
     */
    void addGetterComment(Method method,
            DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn);

    /**
     * 
     * @param method
     * @param dbTable
     * @param dbColumn
     */
    void addSetterComment(Method method,
            DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn);

    /**
     * 
     * @param method
     * @param dbTable
     */
    void addGeneralMethodComment(Method method,
            DBTableJavaBean dbTable);

    /**
     * 
     * @param compilationUnit
     */
    void addJavaFileComment(CompilationUnit compilationUnit);

    /**
     * 
     * @param method
     * @param dbTable
     * @param imports
     */
    void addGeneralMethodAnnotation(Method method, DBTableJavaBean dbTable,
            Set<FullyQualifiedJavaType> imports);

    /**
     * 
     * @param method
     * @param dbTable
     * @param dbColumn
     * @param imports
     */
    void addGeneralMethodAnnotation(Method method, DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn, Set<FullyQualifiedJavaType> imports);
    
    /**
     * 
     * @param field
     * @param dbTable
     * @param imports
     */
    void addFieldAnnotation(Field field, DBTableJavaBean dbTable,
            Set<FullyQualifiedJavaType> imports);

    /**
     * 
     * @param field
     * @param dbTable
     * @param dbColumn
     * @param imports
     */
    void addFieldAnnotation(Field field, DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn, Set<FullyQualifiedJavaType> imports);

    /**
     * 
     * @param innerClass
     * @param dbTable
     * @param imports
     */
    void addClassAnnotation(InnerClass innerClass, DBTableJavaBean dbTable,
            Set<FullyQualifiedJavaType> imports);}
