package com.viching.generate.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.viching.generate.config.ICommentGenerator;
import com.viching.generate.elements.java.Field;
import com.viching.generate.elements.java.FullyQualifiedJavaType;
import com.viching.generate.elements.java.JavaVisibility;
import com.viching.generate.elements.java.TopLevelClass;
import com.viching.generate.source.DBTableJavaBean;

@Component
public class AdapterForSerializable extends AdapterDriver {
	
	private static final FullyQualifiedJavaType serializable = new FullyQualifiedJavaType("java.io.Serializable");;
	
	@Autowired
	private ICommentGenerator commentGenerator;

	@Override
	public void adaptClass(TopLevelClass topLevelClass,
			DBTableJavaBean dbTable) {
		if((!dbTable.needGeneratePrimaryKey() && topLevelClass.getSuperClass() == null )
				|| topLevelClass.getType().getFullyQualifiedName().equals(dbTable.getPrimaryKeyType())){
			topLevelClass.addImportedType(serializable);
			topLevelClass.addSuperInterface(serializable);
		}

        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("1L");
        field.setName("serialVersionUID");
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long"));
        field.setVisibility(JavaVisibility.PRIVATE);
        commentGenerator.addFieldComment(field, dbTable);

        topLevelClass.addField(field);
	}
}
