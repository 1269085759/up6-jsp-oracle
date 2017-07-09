<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%><%@ 
	page import="java.io.File"%><%@
	page import="java.io.BufferedReader"%><%@
	page import="java.io.FileReader"%><%@
	page import="java.io.InputStreamReader"%><%@
	page import="java.io.FileInputStream"%><%@
	page import="up6.*" %><%@
	page import="up6.DbHelper"%><%@
	page import="up6.PathTool"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";


String pathCur = application.getRealPath(request.getServletPath());
String pathParent = new File(pathCur).getParent();
pathParent = new File(pathParent).getParent();
String sqlDir = PathTool.combine(pathParent,"sql");
String downDir = PathTool.combine(pathParent,"sql.down");
DbHelper db = new DbHelper();

String[] clear_type = {"ARRAY_INT","ARRAY_MD5"}; 
for(String str : clear_type)
{
	String sql = "select count(*) from user_objects where object_type='TYPE' and object_name = '" + str + "'";
	int i = db.ExecuteScalar(sql);
	if(i > 0)
	{
		String sql_drop = "DROP TYPE " + str;
		db.ExecuteNonQuery(sql_drop);
	}
}

String[] clear_sequence = {"SEQ_F_IDSVR","SEQ_FD_ID","SEQ_DN_F_IDSVR","SEQ_DN_FD_ID"};
for(String str : clear_sequence)
{
	String sql = "select count(*) from user_objects where object_type='SEQUENCE' and object_name = '" + str + "'";
	int i = db.ExecuteScalar(sql);
	if(i > 0)
	{
		String sql_drop = "DROP SEQUENCE " + str;
		db.ExecuteNonQuery(sql_drop);
	}
}

String[] clear_table = {"UP6_FILES","UP6_FOLDERS","DOWN_FILES","DOWN_FOLDERS"};
for(String str : clear_table)
{
	String sql = "select count(*) from user_tables where table_name = '" + str + "'";
	int i = db.ExecuteScalar(sql);
	if(i > 0)
	{
		String sql_drop = "DROP TABLE " + str;
		db.ExecuteNonQuery(sql_drop);
	}
}

String[] clear_procedure = {"F_PROCESS","FD_FILES_ADD_BATCH","FD_FILES_CHECK","FD_PROCESS","FD_ADD_BATCH"};
for(String str : clear_procedure)
{
	String sql = "select count(*) from user_objects where object_type='PROCEDURE' and object_name = '" + str + "'";
	int i = db.ExecuteScalar(sql);
	if(i > 0)
	{
		String sql_drop = "DROP PROCEDURE " + str;
		db.ExecuteNonQuery(sql_drop);
	}
}

File dir = new File(sqlDir);
if(dir.exists())
{
	File[] files = dir.listFiles();
	if(files.length > 0)
	{
		for(File file : files)
		{
			if(file.getName().endsWith(".sql"))
			{
				InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
				BufferedReader reader = new BufferedReader(isr);
				StringBuffer buffer = new StringBuffer();
				String text;
				while((text = reader.readLine()) != null)
				{
					buffer.append(text + "\n");
				}
				String sb = buffer.toString();
				String filename = file.getName().substring(0,file.getName().lastIndexOf("."));
				if(filename.equalsIgnoreCase("up6_files") || filename.equalsIgnoreCase("up6_folders"))
				{
					String tab = sb.substring(sb.indexOf("CREATE TABLE"),sb.indexOf(";",sb.indexOf("CREATE TABLE")));
					String key = sb.substring(sb.indexOf("ALTER TABLE"),sb.indexOf(";",sb.indexOf("ALTER TABLE")));
					String seq = sb.substring(sb.indexOf("CREATE SEQUENCE"),sb.indexOf(";",sb.indexOf("CREATE SEQUENCE")));
					db.ExecuteNonQuery(tab);
					db.ExecuteNonQuery(key);
					db.ExecuteNonQuery(seq);
					//XDebug.Output("tab",filename);
				}
				else if(filename.equalsIgnoreCase("fd_files_add_batch") || filename.equalsIgnoreCase("fd_files_check"))
				{
					String ctp = sb.substring(sb.indexOf("CREATE"),sb.indexOf(";",sb.indexOf("CREATE")));
					String cpd = sb.substring(sb.lastIndexOf("create"),sb.lastIndexOf(";"));
					db.ExecuteNonQuery(ctp);
					db.ExecuteNonQuery(cpd);
					//XDebug.Output("pdu",filename);
				}
				else
				{
					db.ExecuteNonQuery(sb);
					//XDebug.Output("pdu",filename);
				}
			}
		}
	}
}

dir = new File(downDir);
if(dir.exists())
{
	File[] files = dir.listFiles();
	if(files.length > 0)
	{
		for(File file : files)
		{
			if(file.getName().endsWith(".sql"))
			{
				InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
				BufferedReader reader = new BufferedReader(isr);
				StringBuffer buffer = new StringBuffer();
				String text;
				while((text = reader.readLine()) != null)
				{
					buffer.append(text + "\n");
				}
				String sb = buffer.toString();
				String filename = file.getName().substring(0,file.getName().lastIndexOf("."));
				if(filename.equalsIgnoreCase("down_files") || filename.equalsIgnoreCase("down_folders"))
				{
					String tab = sb.substring(sb.indexOf("CREATE TABLE"),sb.indexOf(";",sb.indexOf("CREATE TABLE")));
					String key = sb.substring(sb.indexOf("ALTER TABLE"),sb.indexOf(";",sb.indexOf("ALTER TABLE")));
					String seq = sb.substring(sb.indexOf("CREATE SEQUENCE"),sb.indexOf(";",sb.indexOf("CREATE SEQUENCE")));
					db.ExecuteNonQuery(tab);
					db.ExecuteNonQuery(key);
					db.ExecuteNonQuery(seq);
				}
				else
				{
					db.ExecuteNonQuery(sb);
				}
			}
		}
	}
}
out.write("数据库初始化完毕");
%>


