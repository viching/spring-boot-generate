package com.viching.generate.db;

import static com.viching.generate.db.util.StringUtility.stringHasValue;

import com.viching.generate.source.DBColumnJavaBean;

public class FormattingUtilities {

    /**
     * Utility class - no instances.
     */
    private FormattingUtilities() {
        super();
    }

    /**
     * Gets the parameter clause.
     *
     * @param dbColumn
     *            the introspected column
     * @return the parameter clause
     */
    public static String getParameterClause(
            DBColumnJavaBean dbColumn) {
        return getParameterClause(dbColumn, null);
    }

    /**
     * Gets the parameter clause.
     *
     * @param dbColumn
     *            the introspected column
     * @param prefix
     *            the prefix
     * @return the parameter clause
     */
    public static String getParameterClause(
            DBColumnJavaBean dbColumn, String prefix) {
        StringBuilder sb = new StringBuilder();

        sb.append("#{"); //$NON-NLS-1$
        sb.append(dbColumn.getJavaProperty(prefix));
        sb.append(",jdbcType="); //$NON-NLS-1$
        sb.append(dbColumn.getJdbcTypeName());

        if (stringHasValue(dbColumn.getTypeHandler())) {
            sb.append(",typeHandler="); //$NON-NLS-1$
            sb.append(dbColumn.getTypeHandler());
        }

        sb.append('}');

        return sb.toString();
    }

    /**
     * The phrase to use in a select list. If there is a table alias, the value will be
     * "alias.columnName as alias_columnName"
     *
     * @param dbColumn
     *            the introspected column
     * @return the proper phrase
     */
    public static String getSelectListPhrase(
            DBColumnJavaBean dbColumn) {
        return getEscapedColumnName(dbColumn);
    }

    /**
     * Gets the escaped column name.
     *
     * @param dbColumn
     *            the introspected column
     * @return the escaped column name
     */
    public static String getEscapedColumnName(
            DBColumnJavaBean dbColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append(escapeStringForMyBatis3(dbColumn.getColumnName()));

        if (dbColumn.isColumnNameDelimited()) {
            sb.insert(0, "\"");
            sb.append("\"");
        }

        return sb.toString();
    }

    public static String getAliasedEscapedColumnName(
            DBColumnJavaBean dbColumn) {
    	return getEscapedColumnName(dbColumn);
    }

   public static String getAliasedActualColumnName(
            DBColumnJavaBean dbColumn) {
        StringBuilder sb = new StringBuilder();

        if (dbColumn.isColumnNameDelimited()) {
        	sb.append("\"");
        }

        sb.append(dbColumn.getColumnName());

        if (dbColumn.isColumnNameDelimited()) {
            sb.append("\"");
        }

        return sb.toString();
    }

    /**
     * The renamed column name for a select statement. If there is a table alias, the value will be alias_columnName.
     * This is appropriate for use in a result map.
     *
     * @param dbColumn
     *            the introspected column
     * @return the renamed column name
     */
    public static String getRenamedColumnNameForResultMap(
            DBColumnJavaBean dbColumn) {
    	return dbColumn.getColumnName();
    }

    /**
     * Escape string for my batis3.
     *
     * @param s
     *            the s
     * @return the string
     */
    public static String escapeStringForMyBatis3(String s) {
        // nothing to do for MyBatis3 so far
        return s;
    }
}
