package com.viching.generate.source;

import java.sql.Types;

import com.viching.generate.elements.java.FullyQualifiedJavaType;

public class DBColumnJavaBean extends DBColumn{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean identity;

    private boolean isSequenceColumn;

    private String javaProperty;

    private FullyQualifiedJavaType fullyQualifiedJavaType;

    private String typeHandler;
    
    private boolean isColumnNameDelimited;
    
    private boolean isAutoIncrement;

    private boolean isGeneratedColumn;

	public boolean isIdentity() {
		return identity;
	}

	public void setIdentity(boolean identity) {
		this.identity = identity;
	}

	public boolean isSequenceColumn() {
		return isSequenceColumn;
	}

	public void setSequenceColumn(boolean isSequenceColumn) {
		this.isSequenceColumn = isSequenceColumn;
	}

	public String getJavaProperty() {
		return javaProperty;
	}

	public void setJavaProperty(String javaProperty) {
		this.javaProperty = javaProperty;
	}

	public FullyQualifiedJavaType getFullyQualifiedJavaType() {
		return fullyQualifiedJavaType;
	}

	public void setFullyQualifiedJavaType(
			FullyQualifiedJavaType fullyQualifiedJavaType) {
		this.fullyQualifiedJavaType = fullyQualifiedJavaType;
	}

	public String getTypeHandler() {
		return typeHandler;
	}

	public void setTypeHandler(String typeHandler) {
		this.typeHandler = typeHandler;
	}

	public boolean isColumnNameDelimited() {
		return isColumnNameDelimited;
	}

	public void setColumnNameDelimited(boolean isColumnNameDelimited) {
		this.isColumnNameDelimited = isColumnNameDelimited;
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	public boolean isGeneratedColumn() {
		return isGeneratedColumn;
	}

	public void setGeneratedColumn(boolean isGeneratedColumn) {
		this.isGeneratedColumn = isGeneratedColumn;
	}
	
	public boolean isJDBCDateColumn() {
        return fullyQualifiedJavaType.equals(FullyQualifiedJavaType
                .getDateInstance())
                && "DATE".equalsIgnoreCase(this.getJdbcTypeName()); //$NON-NLS-1$
    }

    public boolean isJDBCTimeColumn() {
        return fullyQualifiedJavaType.equals(FullyQualifiedJavaType
                .getDateInstance())
                && "TIME".equalsIgnoreCase(this.getJdbcTypeName()); //$NON-NLS-1$
    }
    
    public boolean isBLOBColumn() {
        String typeName = getJdbcTypeName();

        return "BINARY".equals(typeName) || "BLOB".equals(typeName) //$NON-NLS-1$ //$NON-NLS-2$
                || "CLOB".equals(typeName) || "LONGNVARCHAR".equals(typeName) //$NON-NLS-1$ //$NON-NLS-2$ 
                || "LONGVARBINARY".equals(typeName) || "LONGVARCHAR".equals(typeName) //$NON-NLS-1$ //$NON-NLS-2$
                || "NCLOB".equals(typeName) || "VARBINARY".equals(typeName); //$NON-NLS-1$ //$NON-NLS-2$ 
    }

    public boolean isStringColumn() {
        return fullyQualifiedJavaType.equals(FullyQualifiedJavaType
                .getStringInstance());
    }

    public boolean isJdbcCharacterColumn() {
    	int jdbcType = getJdbcType();
        return jdbcType == Types.CHAR || jdbcType == Types.CLOB
                || jdbcType == Types.LONGVARCHAR || jdbcType == Types.VARCHAR
                || jdbcType == Types.LONGNVARCHAR || jdbcType == Types.NCHAR
                || jdbcType == Types.NCLOB || jdbcType == Types.NVARCHAR;
    }
    
    public String getJavaProperty(String prefix) {
        if (prefix == null) {
            return javaProperty;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(javaProperty);

        return sb.toString();
    }
}
