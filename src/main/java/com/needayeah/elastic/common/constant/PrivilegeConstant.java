package com.needayeah.elastic.common.constant;

import java.util.Arrays;
import java.util.List;

public class PrivilegeConstant {
    /**
     * 初始密码
     */
    public static String defaultPassword="123456";

    public static List<String> specialMethodName=Arrays.asList(
            "getEmployee","modifyEmployee","addEmployee"
            ,"modifyEmpForbidden","changePassword"
    );

    public static List<String> commonFieldIds=Arrays.asList(
            "employeeId","createBy","updateBy","modifyBy","checkBy"
    );
    public static List<String> commonFieldNames=Arrays.asList(
           "createName","updateName"
    );


    public static String REDIS_EMP_PAGE_COLUMN="emp_page_column:{0}:{1}";
}
