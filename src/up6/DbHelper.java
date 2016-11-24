package up6;
import java.sql.*;

/*
 * 原型
*/
public class DbHelper {
	//当前项目路径。D:\\WebApps\\HttpUploader3\\
	String m_curPath;

	public String m_dbDriver = "oracle.jdbc.driver.OracleDriver";
	//数据库连接字符串
	public String m_dbUrl = "jdbc:oracle:thin:@localhost:1521:orcl";
	public String m_dbName = "system";//数据库帐号
	public String m_dbPass = "123456";//数据库密码

	public DbHelper()
	{
		UploaderCfg cfg = new UploaderCfg();
		this.m_curPath = cfg.ProjectPath();
	}
	
	public Connection GetCon()
	{		
		Connection con = null;
		
		try 
		{
			Class.forName(this.m_dbDriver).newInstance();//加载驱动。
			con = DriverManager.getConnection(this.m_dbUrl,this.m_dbName,this.m_dbPass);
		}
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
	}
	
	/**
	 * 自动创建命令对象。
	 * @param sql
	 * @return
	 */
	public PreparedStatement GetCommand(String sql)
	{
		PreparedStatement cmd = null;
		try {
			cmd = this.GetCon().prepareStatement(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * 与ExecuteGenKey配合使用。
	 * @param sql
	 * @return
	 */
	public PreparedStatement GetCommandPK(String sql)
	{
		PreparedStatement cmd = null;
		try {
			//cmd = this.GetCon().prepareStatement(sql,new String[]{colName});
			//声明在执行时返回主键
			cmd = this.GetCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * @param sql
	 * @param colIndex 自增ID列名称
	 * @return
	 */
	public PreparedStatement GetCommand(String sql,String colName)
	{
		PreparedStatement cmd = null;
		try {
			cmd = this.GetCon().prepareStatement(sql,new String[]{colName});
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * 自动创建命令对象。
	 * @param sql
	 * @return
	 */
	public PreparedStatement GetCommandCall(String sql)
	{
		PreparedStatement cmd = null;
		try {
			cmd = this.GetCon().prepareCall(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * 执行存储过程
	 * sql语法：call 存储过程名称(?,?,?,?)
	 * @param sql
	 * @return
	 */
	public CallableStatement GetCommandStored(String sql)
	{
		CallableStatement cmd = null;
		try {
			cmd = this.GetCon().prepareCall(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public void ExecuteNonQuery(PreparedStatement cmd)
	{	
		this.ExecuteNonQuery(cmd,true);
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public void ExecuteNonQuery(PreparedStatement cmd,boolean autoClose)
	{		
		try 
		{
			cmd.executeUpdate();
			if(autoClose)
			{
				cmd.close();
				//cmd.getConnection().close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public void ExecuteNonQuery(String sql)
	{		
		try 
		{
			PreparedStatement cmd = this.GetCommand(sql);
			//cmd.execute();
			cmd.executeUpdate();
			//cmd.getConnection().close();
			cmd.close();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public boolean Execute(PreparedStatement cmd)
	{		
		boolean ret = false;
		try {
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = true;
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public int ExecuteScalar(PreparedStatement cmd)
	{		
		int ret = 0;
		try {
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getInt(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public int ExecuteScalar(PreparedStatement cmd,String sql)
	{		
		int ret = 0;
		try {
			ResultSet rs = cmd.executeQuery(sql);
			if(rs.next())
			{
				ret = rs.getInt(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public int ExecuteScalar(String sql)
	{		
		int ret = 0;
		try {
			PreparedStatement cmd = this.GetCommand(sql);
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getInt(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,获取自动生成的key
	 * @param cmd
	 * @return
	 */
	public long ExecuteGenKey(PreparedStatement cmd)
	{		
		long ret = 0;
		try {
			cmd.executeUpdate();
			ResultSet rs = cmd.getGeneratedKeys();
			if(rs.next())
			{
				ret = rs.getLong(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public long ExecuteLong(PreparedStatement cmd)
	{		
		long ret = 0;
		try {
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getLong(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public long ExecuteLong(String sql)
	{		
		long ret = 0;
		try {
			PreparedStatement cmd = this.GetCommand(sql);
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getLong(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 执行SQL,自动关闭数据库连接
	 * @param cmd
	 * @return
	 */
	public String ExecuteString(PreparedStatement cmd)
	{		
		String ret = null;
		try {
			ResultSet rs = cmd.executeQuery();
			if(rs.next())
			{
				ret = rs.getString(1);
			}
			
			rs.close();
			cmd.close();
			//cmd.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 注意：外部必须关闭ResultSet，connection,
	 * ResultSet索引基于1
	 * @param cmd
	 * @return
	 */
	public ResultSet ExecuteDataSet(PreparedStatement cmd)
	{
		ResultSet ret = null;
		try {
			ret = cmd.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public ResultSet ExecuteDataSet(String sql)
	{
		try {
			PreparedStatement cmd = this.GetCommand(sql);
			ResultSet rs = cmd.executeQuery();
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public String GetStringSafe(String v,String def)
	{
		return v == null ? def : v;
	}	
}