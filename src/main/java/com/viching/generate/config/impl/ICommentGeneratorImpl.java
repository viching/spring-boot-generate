package com.viching.generate.config.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import com.viching.generate.Application;
import com.viching.generate.config.ICommentGenerator;
import com.viching.generate.db.util.StringUtility;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.InnerClass;
import com.viching.generate.elements.java.InnerEnum;
import com.viching.generate.elements.java.JavaElement;
import com.viching.generate.elements.java.Method;
import com.viching.generate.elements.java.Parameter;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBColumnJavaBean;
import com.viching.generate.source.DBTableJavaBean;

/**
 * 生成评论
 * @author Administrator
 *
 */
public class ICommentGeneratorImpl implements ICommentGenerator {

    private boolean suppressDate;

    private boolean suppressAllComments;

    /** If suppressAllComments is true, this option is ignored. */
    private boolean addRemarkComments;
    
    private boolean suppressGetterSetter;

    private SimpleDateFormat dateFormat;

    public ICommentGeneratorImpl(boolean suppressDate, boolean suppressAllComments, boolean addRemarkComments, boolean suppressGetterSetter, SimpleDateFormat dateFormat) {
        super();
        this.suppressDate = suppressDate;
        this.suppressAllComments = suppressAllComments;
        this.addRemarkComments = addRemarkComments;
        this.suppressGetterSetter = suppressGetterSetter;
        this.dateFormat = dateFormat;
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        // add no file level comments by default
    }

    /**
     * 
     * @param javaElement
     * @param markAsDoNotDelete
     */
    protected void addJavadocTag(JavaElement javaElement,
            boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *"); 
        StringBuilder sb = new StringBuilder();
        sb.append(" * "); 
        sb.append("@date");
        if (markAsDoNotDelete) {
            sb.append(" time-for-merge "); 
        }
        String s = getDateString();
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }
        javaElement.addJavaDocLine(sb.toString());
    }

    /**
     * 
     * @return
     */
    protected String getDateString() {
        if (suppressDate) {
            return null;
        } else if (dateFormat != null) {
            return dateFormat.format(new Date());
        } else {
            return new Date().toString();
        }
    }

    @Override
    public void addClassComment(InnerClass innerClass,
            DBTableJavaBean dbTable) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        innerClass.addJavaDocLine("/**"); 
        innerClass
                .addJavaDocLine(" * @author viching.com "); 

        sb.append(" *  @table "); 
        sb.append(dbTable.getSource());
        innerClass.addJavaDocLine(sb.toString());

        addJavadocTag(innerClass, false);

        innerClass.addJavaDocLine(" */"); 
    }

    @Override
    public void addClassComment(InnerClass innerClass,
            DBTableJavaBean dbTable, boolean markAsDoNotDelete) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        innerClass.addJavaDocLine("/**"); 
        innerClass
                .addJavaDocLine(" * @author viching.com "); 

        sb.append(" * @table "); 
        sb.append(dbTable.getSource());
        innerClass.addJavaDocLine(sb.toString());

        addJavadocTag(innerClass, markAsDoNotDelete);

        innerClass.addJavaDocLine(" */"); 
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass,
            DBTableJavaBean dbTable) {
        if (suppressAllComments  || !addRemarkComments) {
            return;
        }

        topLevelClass.addJavaDocLine("/**"); 

        String remarks = dbTable.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            topLevelClass.addJavaDocLine(" * @remarks "); 
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));  
            /*for (String remarkLine : remarkLines) {
                topLevelClass.addJavaDocLine(" * " + remarkLine);  
            } */
            Arrays.asList(remarkLines).stream().forEach(remarkLine->topLevelClass.addJavaDocLine(" * " + remarkLine));
        }
        topLevelClass.addJavaDocLine(" *"); 

        topLevelClass
                .addJavaDocLine(" * @author viching.com "); 

        StringBuilder sb = new StringBuilder();
        sb.append(" * @table "); 
        sb.append(dbTable.getSource());
        topLevelClass.addJavaDocLine(sb.toString());

        addJavadocTag(topLevelClass, true);

        topLevelClass.addJavaDocLine(" */"); 
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum,
            DBTableJavaBean dbTable) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        innerEnum.addJavaDocLine("/**"); 
        innerEnum
                .addJavaDocLine(" * @author viching.com "); 

        sb.append(" * @TYPE "); 
        sb.append(dbTable.getSource());
        innerEnum.addJavaDocLine(sb.toString());

        addJavadocTag(innerEnum, false);

        innerEnum.addJavaDocLine(" */"); 
    }

    @Override
    public void addFieldComment(Field field,
            DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn) {
        if (suppressAllComments) {
            return;
        }

        field.addJavaDocLine("/**"); 

        String remarks = dbColumn.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            field.addJavaDocLine(" * @remarks "); 
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));  
            /*for (String remarkLine : remarkLines) {
                field.addJavaDocLine(" * " + remarkLine);  
            }*/
            Arrays.asList(remarkLines).stream().forEach(remarkLine -> field.addJavaDocLine(" * " + remarkLine));
        }

        field.addJavaDocLine(" *"); 
        field.addJavaDocLine(" * @author viching.com "); 

        StringBuilder sb = new StringBuilder();
        sb.append(" * @field "); 
        sb.append(dbTable.getSource());
        sb.append('.');
        sb.append(dbColumn.getColumnName());
        field.addJavaDocLine(sb.toString());

        addJavadocTag(field, false);

        field.addJavaDocLine(" */"); 
    }

    @Override
    public void addFieldComment(Field field, DBTableJavaBean dbTable) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        field.addJavaDocLine("/**"); 
        field.addJavaDocLine(" * @author viching.com "); 
        sb.append(" * @table "); 
        sb.append(dbTable.getSource());
        field.addJavaDocLine(sb.toString());

        addJavadocTag(field, false);

        field.addJavaDocLine(" */"); 
    }

    @Override
    public void addGeneralMethodComment(Method method,
            DBTableJavaBean dbTable) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**"); 
        method
                .addJavaDocLine(" * @author viching.com "); 

        sb.append(" * @table "); 
        sb.append(dbTable.getSource());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method, false);

        method.addJavaDocLine(" */"); 
    }

    @Override
    public void addGetterComment(Method method,
            DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn) {
        if (suppressAllComments) {
            return;
        }
        if(suppressGetterSetter){
        	return;
        }
        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**"); 
        method
                .addJavaDocLine(" * @getter "); 

        sb.append(" * @author viching.com "); 
        sb.append(dbTable.getSource());
        sb.append('.');
        sb.append(dbColumn.getColumnName());
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" *"); 

        sb.setLength(0);
        sb.append(" * @return the value of "); 
        sb.append(dbTable.getSource());
        sb.append('.');
        sb.append(dbColumn.getColumnName());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method, false);

        method.addJavaDocLine(" */"); 
    }

    @Override
    public void addSetterComment(Method method,
            DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn) {
        if (suppressAllComments) {
            return;
        }
        if(suppressGetterSetter){
        	return;
        }

        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**"); 
        method
                .addJavaDocLine(" * @author viching.com "); 

        sb.append(" * @setter "); 
        sb.append(dbTable.getSource());
        sb.append('.');
        sb.append(dbColumn.getColumnName());
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" *"); 

        Parameter parm = method.getParameters().get(0);
        sb.setLength(0);
        sb.append(" * @param "); 
        sb.append(parm.getName());
        sb.append(" the value for "); 
        sb.append(dbTable.getSource());
        sb.append('.');
        sb.append(dbColumn.getColumnName());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method, false);

        method.addJavaDocLine(" */"); 
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, DBTableJavaBean dbTable,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); 
        String comment = "@table: " + dbTable.getSource(); 
        method.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); 
        String comment = "@field: " 
                + dbTable.getSource()
                + "." 
                + dbColumn.getColumnName();
        method.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addFieldAnnotation(Field field, DBTableJavaBean dbTable,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); 
        String comment = "@table: " + dbTable.getSource(); 
        field.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addFieldAnnotation(Field field, DBTableJavaBean dbTable,
            DBColumnJavaBean dbColumn, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); 
        String comment = "@field: " 
                + dbTable.getSource()
                + "." 
                + dbColumn.getColumnName();
        field.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, DBTableJavaBean dbTable,
            Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); 
        String comment = "@table: " + dbTable.getSource(); 
        innerClass.addAnnotation(getGeneratedAnnotation(comment));
    }
    
    private String getGeneratedAnnotation(String comment) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("@Generated("); 
        if (suppressAllComments) {
            buffer.append('\"');
        } else {
            buffer.append("value=\""); 
        }
        
        buffer.append(Application.class.getName());
        buffer.append('\"');
        
        if (!suppressDate && !suppressAllComments) {
            buffer.append(", date=\""); 
            buffer.append(DatatypeConverter.printDateTime(Calendar.getInstance()));
            buffer.append('\"');
        }
        
        if (!suppressAllComments) {
            buffer.append(", comments=\""); 
            buffer.append(comment);
            buffer.append('\"');
        }
        
        buffer.append(')');
        return buffer.toString();
    }
}
