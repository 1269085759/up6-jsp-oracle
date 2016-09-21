--drop table down_files
CREATE TABLE down_files
(
	 f_idSvr   		number NOT NULL    
	,f_uid        	number DEFAULT '0'       --用户ID
	,f_mac        	varchar(50)  DEFAULT  '' --MAC地址，用来识别不同电脑的下载任务
	,f_nameLoc		varchar(255) DEFAULT  ''
	,f_pathLoc      varchar(255)  DEFAULT '' --本地文件路径。
	,f_fileUrl      varchar(255)  DEFAULT '' --服务器文件地址。http://www.qq.com/QQ2014.exe
	,f_perLoc		varchar(6) 	DEFAULT '0%'	 --已下载进度。10%
	,f_lenLoc    	number(19) 	DEFAULT 0  	 --本地文件长度（已下载文件长度）
	,f_lenSvr    	number(19) 	DEFAULT 0  	 --服务器文件长度
	,f_sizeSvr		varchar(10) DEFAULT '0'
	,f_complete     number(1) 	DEFAULT '0'  	 --是否已下载完成。	
	,f_pidRoot		number 		DEFAULT '0'		 --文件夹根级ID
	,f_fdTask		number(1) 	DEFAULT '0'		 --是否是文件夹
);

--创建主键
ALTER TABLE down_files ADD CONSTRAINT PK_down_files PRIMARY KEY(f_idSvr);

--创建自动编号列
--DROP SEQUENCE SEQ_dn_f_idSvr
CREATE SEQUENCE SEQ_dn_f_idSvr
       MINVALUE 1
       START WITH 1
       NOMAXVALUE
       INCREMENT BY 1
       NOCYCLE
       CACHE 30
;