--创建自动编号列
--DROP SEQUENCE SEQ_f_idSvr;
CREATE SEQUENCE SEQ_f_idSvr 
       MINVALUE 1
       START WITH 1
       NOMAXVALUE
       INCREMENT BY 1
       NOCYCLE
       CACHE 30
;