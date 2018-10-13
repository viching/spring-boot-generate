package com.viching.generate.source;

public class GeneratedKey {

	private String column;

	private String runtimeSqlStatement;

	private boolean isIdentity;
	
	private DBColumnJavaBean dbColumn;

	private String type;

	public GeneratedKey(DBColumnJavaBean dbColumn, String column, boolean isIdentity, String type) {
		super();
		this.dbColumn = dbColumn;
		this.column = column;
		this.type = type;
		this.isIdentity = isIdentity;
		this.runtimeSqlStatement = "JDBC";
	}

	public String getColumn() {
		return column;
	}

	public boolean isIdentity() {
		return isIdentity;
	}

	public String getRuntimeSqlStatement() {
		return runtimeSqlStatement;
	}

	public String getType() {
		return type;
	}

	public String getOrder() {
		return isIdentity ? "AFTER" : "BEFORE"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public boolean isJdbcStandard() {
        return "JDBC".equals(runtimeSqlStatement); //$NON-NLS-1$
    }

	public DBColumnJavaBean getDbColumn() {
		return dbColumn;
	}
}
