create or replace procedure fd_process(
 uidSvr in number
,fd_idSvr in number
,fd_lenSvr in number
,fd_perSvr in varchar2
)is
begin
	/*更新文件进度*/
  update up6_files
  set f_lenSvr=fd_lenSvr,f_perSvr=fd_perSvr
  where f_uid=uidSvr and f_idSvr=fd_idSvr;
end;