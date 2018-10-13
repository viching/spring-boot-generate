package com.viching.generate.converter.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.viching.generate.converter.ConverterDriver;
import com.viching.generate.elements.java.CompilationUnit;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class ConverterForController extends ConverterDriver {
	
	private static int limit = 1;

	@Override
	public List<CompilationUnit> getCompilationUnits(DBTableJavaBean dbTable) {
        
        FullyQualifiedJavaType superclass = new FullyQualifiedJavaType(dbTable.getCommonControllerType());
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(dbTable.getControllerType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setSuperClass(superclass);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addAnnotation("@RestController");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.RestController"));
        commentGenerator.addJavaFileComment(topLevelClass);
        
        Field log = new Field();
        log.setVisibility(JavaVisibility.PRIVATE);
        log.setFinal(true);
        log.setName("logger");
        FullyQualifiedJavaType logType = new FullyQualifiedJavaType("org.slf4j.Logger");
        log.setType(logType);
        log.addAnnotation("@SuppressWarnings(\"unused\")");
        log.setInitializationString("LoggerFactory.getLogger(this.getClass())");
        topLevelClass.addField(log);
        FullyQualifiedJavaType logFactoryType = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
        topLevelClass.addImportedType(logType);
        topLevelClass.addImportedType(logFactoryType);

        // 添加默认属性
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(new FullyQualifiedJavaType(dbTable.getServiceType()));
        topLevelClass.addImportedType(field.getType());
        String serviceName = dbTable.getServiceName();
        field.setName(serviceName);
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        field.addAnnotation("@Autowired");
        topLevelClass.addField(field);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(topLevelClass);
        
        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits(dbTable);
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;
    }
	
	public List<CompilationUnit> getExtraCompilationUnits(DBTableJavaBean dbTable) {

        if(limit <= 0){
            return new ArrayList<CompilationUnit>();
        }

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(dbTable.getCommonControllerType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);
        
        // 添加默认属性 Request
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(new FullyQualifiedJavaType("javax.servlet.http.HttpServletRequest"));
        topLevelClass.addImportedType(field.getType());
        field.setName("request");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        field.addAnnotation("@Autowired");
        topLevelClass.addField(field);
        
        // 添加默认属性 Session
        field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(new FullyQualifiedJavaType("javax.servlet.http.HttpSession"));
        topLevelClass.addImportedType(field.getType());
        field.setName("session");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
        field.addAnnotation("@Autowired");
        topLevelClass.addField(field);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        answer.add(topLevelClass);
        
        limit--;
        return answer;
    
	}
}
