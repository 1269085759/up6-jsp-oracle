package up6;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import up6.model.FileInf;
import up6.model.FolderInf;
import net.sf.json.JSONArray;
import com.google.gson.Gson;

/*
 * 原型
*/
/**
 * @author Administrator
 * 更新记录：
 * 	2015-07-30 修复获取文件夹大小错误的问题。
 *
 */
public class DBFile {

	public DBFile()
	{
	}
	
	/**
	 * 获取指定文件夹下面的所有文件，
	 * @param fid
	 * @param files
	 * @param ids
	 */
	public static void GetCompletes(int fid,ArrayList<FileInf> files,ArrayList<String> ids)
	{
        StringBuilder sql = new StringBuilder("select ");
        sql.append("f_idSvr");
        sql.append(",f_nameLoc");
        sql.append(",f_pathLoc");
        sql.append(",f_lenLoc");
        sql.append(",f_sizeLoc");
        sql.append(",f_md5");
        sql.append(",f_pidRoot");
        sql.append(",f_pid");
        sql.append(",f_lenSvr");
        sql.append(",f_pathSvr");//fix:服务器会重复创建文件项的问题
        sql.append(",fd_pathRel");//
        sql.append(" from up6_files");
        sql.append(" left join up6_folders");
        sql.append(" on fd_id = f_pid");
        sql.append(" where f_pidRoot=? and f_complete=1");//bug:在部分环境中测试发现f_complete为0，可以考虑取消f_complete判断

        DbHelper db = new DbHelper();
        PreparedStatement cmd = db.GetCommand(sql.toString());
        try
        {
        	cmd.setInt(1, fid);
        	ResultSet r = db.ExecuteDataSet(cmd);
            while (r.next())
            {
                FileInf fi 		= new FileInf();
                fi.id	 		= r.getString(1);
                fi.nameLoc 		= r.getString(2);
                fi.pathLoc 		= r.getString(3);
                fi.lenLoc 		= r.getLong(4);
                fi.sizeLoc 		= r.getString(5);
                fi.md5 			= r.getString(6);
                fi.pidRoot 		= r.getString(7);
                fi.pid 			= r.getString(8);
                fi.lenSvr 		= r.getLong(9);
                fi.pathSvr 		= r.getString(10);//fix:服务器会重复创建文件项的问题
                fi.pathRel 		= r.getString(11) + "\\";//相对路径：root\\child\\folder\\
                files.add(fi);
                //添加到列表
                //ids.add( Integer.toString(fi.idSvr) );
            }
            r.close();            
            cmd.close();
        }
        catch (SQLException e) {e.printStackTrace();}
        
	}
	
	static public String GetAllUnComplete(int uid)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(" f_id");
		sb.append(",f_fdTask");
		sb.append(",f_nameLoc");
		sb.append(",f_pathLoc");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_pathSvr");//fix(2015-03-16):修复无法续传文件的问题。
		sb.append(" from up6_files ");//change(2015-03-18):联合查询文件夹数据
		sb.append(" where f_uid=? and f_deleted=0 and f_fdChild=0 and f_complete=0");//fix(2015-03-18):只加载未完成列表

		ArrayList<FileInf> files = new ArrayList<FileInf>();
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setInt(1, uid);
			ResultSet r = db.ExecuteDataSet(cmd);
			while(r.next())
			{
				FileInf f 	= new FileInf();
				f.uid			= uid;
				f.id 			= r.getString(1);
				f.fdTask 		= r.getBoolean(2);				
				f.nameLoc 		= r.getString(3);
				f.pathLoc 		= r.getString(4);
				f.md5 			= r.getString(5);
				f.lenLoc 		= r.getLong(6);
				f.sizeLoc 		= r.getString(7);
				f.offset 		= r.getLong(8);
				f.lenSvr 		= r.getLong(9);
				f.perSvr 		= r.getString(10);
				f.complete 		= r.getBoolean(11);
				f.pathSvr		= r.getString(12);//fix(2015-03-19):修复无法续传文件的问题。

				files.add(f);
				
			}
			r.close();
			cmd.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(files.size() < 1) return null;
		Gson g = new Gson();
	    return g.toJson( files );//bug:arrFiles为空时，此行代码有异常	
	}
	
	/**
	 * 获取所有已经上传完的文件和文件夹供下载列表使用。
	 * @param uid
	 * @return
	 */
	public static String GetAllComplete(int uid)
	{
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(" f_id");
        sb.append(",f_fdTask");
        sb.append(",f_nameLoc");
        sb.append(",f_pathLoc");
        sb.append(",f_lenLoc");
        sb.append(",f_sizeLoc");
        sb.append(",f_perSvr");
        //sb.append(",fd_size");
        sb.append(" from up6_files");
        //sb.append(" left join up6_folders");
        //sb.append(" on f_fdID = fd_id");
        sb.append(" where f_deleted=0 and f_fdChild=0 and f_complete=1");

        ArrayList<FileInf> files = new ArrayList<FileInf>();
        DbHelper db = new DbHelper();
        PreparedStatement cmd = db.GetCommand(sb.toString());
        try
        {
        	ResultSet r = db.ExecuteDataSet(cmd);
            while (r.next())
            {
                FileInf f = new FileInf();
                f.id 		= r.getString(1);
                f.fdTask 	= r.getBoolean(2);                
                f.nameLoc 	= r.getString(3);
                f.pathLoc 	= r.getString(4);
                f.lenLoc 	= r.getLong(5);
                f.sizeLoc 	= r.getString(6);//fix(2015-07-30):修复没有正确获取文件夹大小
                f.perSvr 	= r.getString(7);//已下载百分比

                files.add(f);

            }
            r.close();
        }
        catch (SQLException e) {e.printStackTrace();}
        
        Gson g = new Gson();
	    return g.toJson( files );//bug:arrFiles为空时，此行代码有异常
	}

	/**
	 * 根据文件ID获取文件信息
	 * @param fid
	 * @param inf
	 * @return
	 */
	public boolean GetFileInfByFid(String fid,FileInf inf)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(" f_uid");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete");
		sb.append(",f_time");
		sb.append(",f_deleted");
		sb.append(" from up6_files where f_id=? ");
		
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, fid);
			ResultSet r = db.ExecuteDataSet(cmd);

			if (r.next())
			{
				inf.id 				= fid;
				inf.uid 			= r.getInt(1);
				inf.nameLoc 		= r.getString(2);
				inf.nameSvr 		= r.getString(3);
				inf.pathLoc 		= r.getString(4);
				inf.pathSvr 		= r.getString(5);
				inf.pathRel 		= r.getString(6);
				inf.md5 			= r.getString(7);
				inf.lenLoc 			= r.getLong(8);
				inf.sizeLoc 		= r.getString(9);
	            inf.offset 			= r.getLong(10);
	            inf.lenSvr 			= r.getLong(11);
				inf.perSvr 			= r.getString(12);
				inf.complete 		= r.getBoolean(13);
				inf.PostedTime 		= r.getDate(14);
				inf.deleted 		= r.getBoolean(15);
				ret = true;
			}
			cmd.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/// <summary>
	/// 根据文件MD5获取文件信息
	/// </summary>
	/// <param name="md5"></param>
	/// <param name="inf"></param>
	/// <returns></returns>
	public boolean exist_file(String md5, FileInf fileSvr)
	{
		boolean ret = false;
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(" f_id");
		sb.append(",f_uid");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_sizeLoc");
		sb.append(",f_pos");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_complete"); 
		sb.append(",f_time");
		sb.append(",f_deleted");
		sb.append(" from up6_files");
		sb.append(" where f_md5='");
		sb.append(md5);
		sb.append("'and rownum<=1");
		sb.append(" order by to_number(f_lenSvr) DESC");

		DbHelper db = new DbHelper();
		ResultSet r = db.ExecuteDataSet(sb.toString());
		try {
			//cmd.setString(1, md5);
			//ResultSet r = db.ExecuteDataSet(cmd);
			if (r.next())
			{
				fileSvr.id 				= r.getString(1);
				fileSvr.uid 			= r.getInt(2);
				fileSvr.nameLoc 		= r.getString(3);
				fileSvr.nameSvr 		= r.getString(4);
				fileSvr.pathLoc 		= r.getString(5);
				fileSvr.pathSvr 		= r.getString(6);
				fileSvr.pathRel 		= r.getString(7);
				fileSvr.md5 			= r.getString(8);
				fileSvr.lenLoc 			= r.getLong(9);
				fileSvr.sizeLoc 		= r.getString(10);
				fileSvr.offset 			= r.getLong(11);
				fileSvr.lenSvr 			= r.getLong(12);
				fileSvr.perSvr 			= r.getString(13);
				fileSvr.complete 		= r.getBoolean(14);
				fileSvr.PostedTime 		= r.getDate(15);
				fileSvr.deleted 		= r.getBoolean(16);
				ret = true;
			}
			r.close();
			//cmd.close();			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	/// <summary>
	/// 增加一条数据，并返回新增数据的ID
	/// 在ajax_create_fid.aspx中调用
	/// 文件名称，本地路径，远程路径，相对路径都使用原始字符串。
	/// d:\soft\QQ2012.exe
	/// </summary>
	public void Add(FileInf model)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("insert into up6_files(");
		sb.append(" f_id");
		sb.append(",f_pid");
		sb.append(",f_pidRoot");
		sb.append(",f_fdTask");
		sb.append(",f_fdChild");
		sb.append(",f_uid");
		sb.append(",f_pos");
		sb.append(",f_md5");
		sb.append(",f_lenLoc");
		sb.append(",f_lenSvr");
		sb.append(",f_perSvr");
		sb.append(",f_sizeLoc");
		sb.append(",f_nameLoc");
		sb.append(",f_nameSvr");
		sb.append(",f_pathLoc");
		sb.append(",f_pathSvr");
		sb.append(",f_pathRel");
		sb.append(",f_complete");
		
		sb.append(") values (");
		
		sb.append(" ?");//id
		sb.append(",?");//pid
		sb.append(",?");//pidRoot
		sb.append(",?");//fdTask
		sb.append(",?");//fdChild
		sb.append(",?");//uid
		sb.append(",?");//pos
		sb.append(",?");//md5
		sb.append(",?");//lenLoc		
		sb.append(",?");//lenSvr
		sb.append(",?");//perSvr
		sb.append(",?");//sizeLoc
		sb.append(",?");//nameLoc
		sb.append(",?");//nameSvr
		sb.append(",?");//pathLoc
		sb.append(",?");//pathSvr
		sb.append(",?");//pathRel
		sb.append(",?");//complete
		sb.append(") ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		
		try {
			cmd.setString(1, model.id);
			cmd.setString(2, model.pid);
			cmd.setString(3, model.pidRoot);
			cmd.setBoolean(4, model.fdTask);
			cmd.setBoolean(5, model.fdChild);
			cmd.setInt(6, model.uid);
			cmd.setLong(7, model.offset);
			cmd.setString(8, model.md5);
			cmd.setLong(9, model.lenLoc);
			cmd.setLong(10, model.lenSvr);
			cmd.setString(11, model.perSvr);
			cmd.setString(12, model.sizeLoc);
			cmd.setString(12, model.nameLoc);
			cmd.setString(13, model.nameSvr);			
			cmd.setString(14, model.pathLoc);
			cmd.setString(15, model.pathSvr);
			cmd.setString(16, model.pathRel);
			cmd.setBoolean(17, model.complete);
		} catch (SQLException e) {e.printStackTrace();}

		db.ExecuteNonQuery(cmd);
	}

	/// <summary>
	/// 更新文件夹中子文件信息，
	/// filePathRemote
	/// md5
	/// fid
	/// </summary>
	/// <param name="inf"></param>
	public void UpdateChild(FileInf inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("update up6_files set ");
		sb.append(" f_pathSvr = ?, ");
		sb.append(" f_md5 = ? ");
		sb.append(" where f_idSvr=? ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.pathSvr);
			cmd.setString(1, inf.md5);
			cmd.setString(3, inf.id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.ExecuteNonQuery(cmd);
	}
	
	/// <summary>
    /// 根据文件idSvr信息，更新文件数据表中对应项的MD5。
    /// </summary>
    /// <param name="inf"></param>
    public void UpdateMD5(FileInf inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("update up6_files set ");
		sb.append(" f_md5 = ? ");
		sb.append(" where f_id=? ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.md5);
			cmd.setString(2, inf.id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		db.ExecuteNonQuery(cmd);
	}

    /// <summary>
    /// 根据文件idSvr信息，更新文件数据表中对应项的MD5。
    /// </summary>
    /// <param name="inf"></param>
    public void UpdateMD5_path(FileInf inf)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("update up6_files set ");
		sb.append(" f_md5 = ? ");
		sb.append(",f_pathSvr = ? ");
		sb.append(" where f_id=? ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.md5);
			cmd.setString(2, inf.pathSvr);//fix(2015-07-30):重新更新路径
			cmd.setString(3, inf.id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		db.ExecuteNonQuery(cmd);
	}
    
    /**
     * 更新文件MD5，服务器存储路径。
     */
    public void updateInf(FileInf inf)
    {
		StringBuilder sb = new StringBuilder();
		sb.append("update up6_files set ");
		sb.append(" f_md5 = ? ");
		sb.append(",f_pathSvr = ? ");//
		sb.append(",f_lenSvr = ? ");//
		sb.append(",f_perSvr = ? ");//
		sb.append(",f_complete = ? ");//
		sb.append(" where f_idString=? ");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sb.toString());
		try {
			cmd.setString(1, inf.md5);
			cmd.setString(2, inf.pathSvr);
			cmd.setLong(3, inf.lenSvr);
			cmd.setString(4, inf.perSvr);
			cmd.setBoolean(5, inf.complete);
			cmd.setString(6, inf.id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		db.ExecuteNonQuery(cmd);
	}

	/**
	 * 清空文件表，文件夹表数据。
	 */
	static public void Clear()
	{
		DbHelper db = new DbHelper();
		db.ExecuteNonQuery("truncate table up6_files");
		db.ExecuteNonQuery("truncate table up6_folders");
	}

	/**
	 * @param uid
	 * @param fid
	 */
	static public void Complete(int uid, int fid)
	{
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand("update up6_files set f_perSvr='100%' ,f_complete=1 where f_uid=? and f_fdID=?");
		try {
			cmd.setInt(1, uid);
			cmd.setInt(2, fid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		db.ExecuteNonQuery(cmd);
	}
	
	/**
	 * @param uid
	 * @param fid
	 */
	static public void fd_complete(String fid, String uid)
	{
		String sql = "begin ";
		sql += "update up6_files set f_perSvr='100%' ,f_complete=1 where f_id=?;";
		sql += "update up6_folders set fd_complete=1 where fd_id=? and fd_uid=?;";
		sql += "end;";
		
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);
		try {
			cmd.setString(1, fid);
			cmd.setString(2, fid);
			cmd.setInt(3, Integer.parseInt(uid));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		db.ExecuteNonQuery(cmd);
	}
	
	public boolean fd_fileProcess(int uid, int f_id, long f_pos, long lenSvr, String perSvr, int fd_idSvr, long fd_lenSvr,String fd_perSvr,boolean complete)
    {
    	this.f_process(uid, f_id, f_pos, lenSvr, perSvr,complete);
    	this.fd_process(uid, fd_idSvr, fd_lenSvr,fd_perSvr);
    	return true;
    }
    
    public boolean fd_process(int uid,int fd_idSvr,long fd_lenSvr,String perSvr)
    {
        String sql = "call fd_process(?,?,?,?)";
        DbHelper db = new DbHelper();
        PreparedStatement cmd = db.GetCommandStored(sql);     

		try 
		{
			cmd.setInt(1, uid);
			cmd.setInt(2, fd_idSvr);
			cmd.setLong(3, fd_lenSvr);
			cmd.setString(4, perSvr);
		} catch (SQLException e) {e.printStackTrace();}

		db.ExecuteNonQuery(cmd);
		return true;
	}

	/// <summary>
	/// 更新上传进度
	/// </summary>
	///<param name="uid">用户ID</param>
	///<param name="fid">文件ID</param>
	///<param name="filePos">文件位置，大小可能超过2G，所以需要使用long保存</param>
	///<param name="postedLength">已上传长度，文件大小可能超过2G，所以需要使用long保存</param>
	///<param name="postedPercent">已上传百分比</param>
	public boolean f_process(int uid,int f_id,long f_pos,long f_lenSvr,String f_perSvr,boolean cmp)
	{
		//String sql = "update up6_files set f_pos=?,f_lenSvr=?,f_perSvr=? where f_uid=? and f_idSvr=?";
		String sql = "call f_process(?,?,?,?,?,?)";//使用存储过程
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommandStored(sql);
		
		try 
		{
			cmd.setLong(1, f_pos);
			cmd.setLong(2, f_lenSvr);
			cmd.setString(3, f_perSvr);
			cmd.setInt(4, uid);
			cmd.setInt(5, f_id);
			cmd.setBoolean(6, cmp);
		} catch (SQLException e) {e.printStackTrace();}

		db.ExecuteNonQuery(cmd);
		return true;
	}

	/// <summary>
	/// 上传完成。将所有相同MD5文件进度都设为100%
	/// </summary>
	public void UploadComplete(String md5)
	{
		String sql = "update up6_files set f_lenSvr=f_lenLoc,f_perSvr='100%',f_complete=1 where f_md5='"+md5+"'";
		DbHelper db = new DbHelper();
		//PreparedStatement cmd = db.GetCommand(sql);
		
		//cmd.setString(1, md5);
		//db.ExecuteNonQuery(cmd);//在部分环境中测试发现执行后没有效果。f_complete仍然为0
		db.ExecuteNonQuery(sql);
	}

	/// <summary>
	/// 检查相同MD5文件是否有已经上传完的文件
	/// </summary>
	/// <param name="md5"></param>
	public boolean HasCompleteFile(String md5)
	{
		//为空
		if (md5 == null) return false;
		if(md5.isEmpty()) return false;

		String sql = "select f_idSvr from up6_files where f_complete=1 and f_md5=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);

		try {
			cmd.setString(1, md5);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean ret = db.Execute(cmd);

		return ret;
	}

	/// <summary>
	/// 删除一条数据，并不真正删除，只更新删除标识。
	/// </summary>
	/// <param name="uid"></param>
	/// <param name="fid"></param>
	public void Delete(int uid,String fid)
	{
		String sql = "update up6_files set f_deleted=1 where f_uid=? and f_id=?";
		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql);

		try {
			cmd.setInt(1, uid);
			cmd.setString(2, fid);
			db.ExecuteNonQuery(cmd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/// <summary>
	/// 根据根文件夹ID获取未上传完成的文件列表，并转换成JSON格式。
	/// 说明：
	///		1.此函数会自动对文件路径进行转码
	/// </summary>
	/// <param name="fidRoot"></param>
	/// <returns></returns>
	static public String GetUnCompletes(int fidRoot) throws UnsupportedEncodingException
	{
		StringBuilder sql = new StringBuilder("select ");
		sql.append("f_nameLoc");
		sql.append(",f_pathLoc");
		sql.append(",f_lenLoc");
		sql.append(",f_sizeLoc");
		sql.append(",f_md5");
		sql.append(",f_pidRoot");
		sql.append(",f_pid");
		sql.append(" from up6_files where f_pidRoot=?");
		ArrayList<FileInf> arrFiles = new ArrayList<FileInf>();

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql.toString());
		try 
		{
			cmd.setInt(1, fidRoot);
			ResultSet r = db.ExecuteDataSet(cmd);
			while (r.next())
			{
				FileInf fi = new FileInf();
				fi.nameLoc = r.getString(0);
				fi.pathLoc = r.getString(1);
				fi.pathLoc = URLEncoder.encode(fi.pathLoc,"utf-8");
				fi.pathLoc = fi.pathLoc.replace("+", "%20");
				fi.lenLoc  = r.getLong(2);
				fi.sizeLoc = r.getString(3);
				fi.md5 	   = db.GetStringSafe(r.getString(4),"");			
				fi.pidRoot = r.getString(5);
				fi.pid 	   = r.getString(6);
				arrFiles.add( fi );
			}
			r.close();
		}
		catch (SQLException e){e.printStackTrace();}
		
	    JSONArray json = JSONArray.fromObject( arrFiles );
		return json.toString();
	}

    /// <summary>
    /// 获取未上传完的文件列表
    /// </summary>
    /// <param name="fidRoot"></param>
    /// <param name="files"></param>
	static public void GetUnCompletes(int fidRoot,ArrayList<FileInf> files)
	{
		StringBuilder sql = new StringBuilder("select ");
        sql.append("f_idSvr");
        sql.append(",f_nameLoc");
		sql.append(",f_pathLoc");
		sql.append(",f_lenLoc");
		sql.append(",f_sizeLoc");
		sql.append(",f_md5");
		sql.append(",f_pidRoot");
        sql.append(",f_pid");
        sql.append(",f_lenSvr");
        sql.append(",f_pathSvr");//fix(2015-03-18):续传文件时服务器会创建重复文件项信息
		sql.append(" from up6_files where f_pidRoot=? and f_complete=0");

		DbHelper db = new DbHelper();
		PreparedStatement cmd = db.GetCommand(sql.toString());
		try 
		{
			cmd.setInt(1, fidRoot);
			ResultSet r = db.ExecuteDataSet(cmd);
			while (r.next())
			{
				FileInf fi 	= new FileInf();
	            fi.id 		= r.getString(1);
				fi.nameLoc 	= r.getString(2);
				fi.pathLoc 	= r.getString(3);
				fi.lenLoc 	= r.getLong(4);
				fi.sizeLoc 	= r.getString(5);
				fi.md5 		= db.GetStringSafe(r.getString(6),"");
				fi.pidRoot 	= r.getString(7);
				fi.pid 		= r.getString(8);
	            fi.lenSvr 	= r.getLong(9);
	            fi.pathSvr 	= r.getString(10);//fix(2015-03-18):修复续传文件时服务器会创建重复文件信息的问题。
				files.add(fi);
			}
			r.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}