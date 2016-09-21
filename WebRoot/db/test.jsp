<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ 
	page contentType="text/html;charset=UTF-8"%><%@ 
	page import="up6.*" %><%@
	page import="up6.model.*" %><%@
	page import="up6.biz.folder.*" %><%@
	page import="java.sql.*" %><%@
	page import="oracle.sql.*" %><%@
	page import="oracle.jdbc.*" %><%@ 
	page import="org.apache.commons.lang.StringUtils" %><%@
	page import="java.net.URLDecoder" %><%@ 
	page import="net.sf.json.JSONArray" %><%@ 
	page import="net.sf.json.JSONObject" %><%@ 
	page import="net.sf.json.util.JSONUtils" %><%@ 
	page import="com.google.gson.Gson" %><%@ 
	page import="com.google.gson.GsonBuilder" %><%@ 
	page import="com.google.gson.annotations.SerializedName" %><%@ 
	page import="java.io.*" %><%/*
	此页面主要用来向数据库添加一条记录。
	一般在 HttpUploader.js HttpUploader_MD5_Complete(obj) 中调用
	更新记录：
		2012-05-24 完善
		2012-06-29 增加创建文件逻辑，
*/

DbHelper db = new DbHelper();
Connection con = db.GetCon();
CallableStatement cmd = con.prepareCall("{call fd_files_add_batch(?,?,?,?)}");
cmd.setInt(1, 1);
cmd.setInt(2, 1);
cmd.registerOutParameter(3, OracleTypes.ARRAY,"ARRAY_INT");
cmd.registerOutParameter(4, OracleTypes.ARRAY,"ARRAY_INT");        
cmd.execute();
ARRAY arr_f = ((OracleCallableStatement)cmd).getARRAY(3);
int[] arr1 = arr_f.getIntArray();
out.write( String.valueOf(arr1.length));
for(int i = 0 ,l=arr1.length;i<l;++i)
{
	out.write(String.valueOf(arr1[i]) );
}
ARRAY arr_fd = ((OracleCallableStatement)cmd).getARRAY(4);        
//String[] ids_f = (String[])arr_f.getArray();
//XDebug.Output("文件ID", ids_f);
//String[] ids_fd  = (String[])arr_fd.getArray();
//XDebug.Output("文件夹ID", ids_fd);
cmd.close();
con.close();
%>