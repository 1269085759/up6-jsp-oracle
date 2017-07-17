--drop table down_folders
CREATE TABLE down_folders
(
   fd_id				number NOT NULL   		 --文件夹ID，自动编号
  ,fd_name  			varchar(50) DEFAULT ''   --文件夹名称。test
  ,fd_uid  				number DEFAULT '0'   	 --用户ID 
  ,fd_mac  				varchar(50) DEFAULT ''   --用户电脑识别码
  ,fd_pathLoc			varchar(255) DEFAULT ''  --文件夹信息文件在本地路径。D:\\Soft\\test.cfg
  ,fd_complete  		number(1) DEFAULT '0' 	 --是否已经下载
  ,fd_id_old			number DEFAULT '0'  	 --对应表字段：xdb_folders.fd_id，用来获取文件夹JSON信息
  ,fd_percent			varchar(7) DEFAULT ''  	 --上传百分比。
)