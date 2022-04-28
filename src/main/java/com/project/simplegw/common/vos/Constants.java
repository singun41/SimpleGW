package com.project.simplegw.common.vos;

public class Constants {
    public static final String SYSTEM_PATH = "C:/webapp/SpringBoot/SimpleGW/";
    
    public final static String ERROR_PAGE_403 = "error/403";
	public final static String ERROR_PAGE_410 = "error/410";


    // ----- ----- ----- ----- ----- column size ----- ----- ----- ----- ----- //
    public static final int COLUMN_LENGTH_ROLE = 10;
    
    public static final int COLUMN_LENGTH_USER_ID = 30;
    public static final int COLUMN_LENGTH_PW = 70;
    
    public static final int COLUMN_LENGTH_TEAM = 50;
    public static final int COLUMN_LENGTH_NAME = 20;
    public static final int COLUMN_LENGTH_JOB_TITLE = 30;
    public static final int COLUMN_LENGTH_MOBILE_NO = 13; // format: 000-0000-0000
    public static final int COLUMN_LENGTH_MAIL_ADDRESS = 50;
    
    public static final int COLUMN_LENGTH_DOCU_TYPE = 10;
    public static final int COLUMN_LENGTH_DOCU_KIND = 30;

    public static final int COLUMN_LENGTH_DOCU_TITLE = 200;
    public static final int COLUMN_LENGTH_COMMENT = 200;

    public static final int COLUMN_LENGTH_REMARKS = 200;

    public static final int COLUMN_LENGTH_BASE_CODE_TYPE = 20;
    public static final int COLUMN_LENGTH_BASE_CODE = 3;
    public static final int COLUMN_LENGTH_BASE_CODE_VALUE = 50;

    public static final int COLUMN_LENGTH_APPROVER_STATUS = 10;
    // ----- ----- ----- ----- ----- column size ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- others ----- ----- ----- ----- ----- //
    public static final int PW_UPDATE_AT_LEAST_LENGTH = 8;
    // ----- ----- ----- ----- ----- others ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- column define ----- ----- ----- ----- ----- //
    public static final String COLUMN_DEFINE_TITLE = "nvarchar(" + COLUMN_LENGTH_DOCU_TITLE + ")";
    public static final String COLUMN_DEFINE_CONTENT = "nvarchar(max)";
    public static final String COLUMN_DEFINE_COMMENT = "nvarchar(" + COLUMN_LENGTH_COMMENT + ")";

    public static final String COLUMN_DEFINE_TEAM = "nvarchar(" + COLUMN_LENGTH_TEAM + ")";
    public static final String COLUMN_DEFINE_NAME = "nvarchar(" + COLUMN_LENGTH_NAME + ")";
    public static final String COLUMN_DEFINE_JOB_TITLE = "nvarchar(" + COLUMN_LENGTH_JOB_TITLE + ")";
    
    public static final String COLUMN_DEFINE_DATE = "date";
    public static final String COLUMN_DEFINE_TIME = "time";
    public static final String COLUMN_DEFINE_DATETIME = "datetime";

    public static final String COLUMN_DEFINE_BASE_CODE_VALUE = "nvarchar(" + COLUMN_LENGTH_BASE_CODE_VALUE + ")";

    public static final String COLUMN_DEFINE_REMARKS = "nvarchar(" + COLUMN_LENGTH_REMARKS + ")";
    // ----- ----- ----- ----- ----- column define ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- service common message define ----- ----- ----- ----- ----- //
    public static final String RESULT_MESSAGE_OK = "ok";
    public static final String RESULT_MESSAGE_INSERTED = "등록하였습니다.";
    public static final String RESULT_MESSAGE_UPDATED = "수정하였습니다.";
    public static final String RESULT_MESSAGE_DELETED = "삭제하였습니다.";
    public static final String RESULT_MESSAGE_NOT_AUTHORIZED = "권한이 없습니다.";
    // ----- ----- ----- ----- ----- service common message define ----- ----- ----- ----- ----- //
}
