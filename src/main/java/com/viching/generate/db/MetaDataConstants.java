package com.viching.generate.db;

public class MetaDataConstants {

	public static final String ROOT = "ROOT";
	public static final String SCOTT = "SCOTT";

	public static final String TABLE_CAT = "TABLE_CAT";// String => 表类别（可为 null）
	public static final String TABLE_SCHEM = "TABLE_SCHEM";// String => 表模式（可为
															// null）
	public static final String TABLE_NAME = "TABLE_NAME";// String => 表名称
	public static final String TABLE_TYPE = "TABLE_TYPE";// String => 表类型。
	public static final String REMARKS = "REMARKS";// String => 表的解释性注释 或者列的注释
	public static final String TYPE_CAT = "TYPE_CAT";// String => 类型的类别（可为 null）
	public static final String TYPE_SCHEM = "TYPE_SCHEM";// String => 类型模式（可为
															// null）
	public static final String TYPE_NAME = "TYPE_NAME";// String => 类型名称（可为
														// null）
	public static final String SELF_REFERENCING_COL_NAME = "SELF_REFERENCING_COL_NAME";// String
																						// =>
																						// 有类型表的指定
																						// "identifier"
																						// 列的名称（可为
																						// null）
	public static final String REF_GENERATION = "REF_GENERATION";// String
	
	public static final String COMMENT = "COMMENT";

	
	public static final String COLUMN_NAME = "COLUMN_NAME";// String => column name
	public static final String DATA_TYPE = "DATA_TYPE";// int => SQL type from java.sql.Types
	public static final String COLUMN_SIZE = "COLUMN_SIZE";// int => column size.
	public static final String BUFFER_LENGTH = "BUFFER_LENGTH";// is not used.
	public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";// int => the number of fractional
												// digits. Null is returned for
												// data types where
												// DECIMAL_DIGITS is not
												// applicable.
	public static final String NUM_PREC_RADIX = "NUM_PREC_RADIX";// int => Radix (typically either
												// 10 or 2)
	public static final String NULLABLE = "NULLABLE";// int => is NULL allowed.
	public static final String COLUMN_DEF = "COLUMN_DEF";// String => default value for the
											// column, which should be
											// interpreted as a string when the
											// value is enclosed in single
											// quotes (may be null)
	public static final String KEY_SEQ = "KEY_SEQ";
}
