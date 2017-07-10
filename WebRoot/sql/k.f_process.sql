create or replace procedure f_process(
 posSvr in number
,lenSvr in number
,perSvr in varchar2
,uidSvr in number
,fidSvr in number
,complete in number)is
begin
  update up6_files
  set f_pos=posSvr,f_lenSvr=lenSvr,f_perSvr=perSvr,f_complete=complete
  where f_uid=uidSvr and f_idSvr=fidSvr;
end;