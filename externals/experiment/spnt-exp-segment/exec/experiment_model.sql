UPDATE QSEGMENTEXP SET MANUALNAME = '' where MANUALNAME in ('0','1','10','2','3','4','5','6','7','8','9')
update QSEGMENTEXP set CORPUSENTRYNAME = CONCAT('0',CORPUSENTRYNAME) where CORPUSENTRYNAME = '5'

select * from QSEGMENTEXP

select distinct(MANUALNAME) from QSEGMENTEXP

select count(*) from QSEGMENTEXP

select count(distinct(MANUALNAME)) from QSEGMENTEXP Where  not MANUALNAME = ''

select MFCCLABEL, count(MFCCLABEL) from QSEGMENTEXP  group BY MFCCLABEL order by count(MFCCLABEL) desc

select CORPUSENTRYNAME, count(id) from QSEGMENTEXP where MANUALNAME = 'e' and not MARKERLABEL like 'D;%'  GROUP BY CORPUSENTRYNAME
select CORPUSENTRYNAME, count(id) from QSEGMENTEXP where MANUALNAME = 'a' and not MARKERLABEL like 'D;%'  GROUP BY CORPUSENTRYNAME
select CORPUSENTRYNAME, count(id) from QSEGMENTEXP where LENGTH(MANUALNAME) = 2 and not MARKERLABEL like 'D;%'  GROUP BY CORPUSENTRYNAME
select CORPUSENTRYNAME, count(id) from QSEGMENTEXP where not MARKERLABEL like 'D;%' and  MANUALNAME = '' GROUP BY CORPUSENTRYNAME

select CORPUSENTRYNAME, count(id) from QSEGMENTEXP where MANUALNAME = 'a'  and  MFCCLABEL = 'a' and MFCC <90 and not MARKERLABEL like 'D;%'  GROUP BY CORPUSENTRYNAME
select CORPUSENTRYNAME, count(id) from QSEGMENTEXP where MANUALNAME = 'e'  and  MFCCLABEL = 'e' and MFCC <90 and not MARKERLABEL like 'D;%'  GROUP BY CORPUSENTRYNAME
select CORPUSENTRYNAME, count(id) from QSEGMENTEXP where MANUALNAME = 'e'  and  MFCCLABEL = 'a' and MFCC <90 and not MARKERLABEL like 'D;%'  GROUP BY CORPUSENTRYNAME
select CORPUSENTRYNAME, count(id) from QSEGMENTEXP where MANUALNAME = ''  and  MFCCLABEL = 'a' and MFCC <90 GROUP BY CORPUSENTRYNAME


select * from QSEGMENTEXP where  MANUALNAME = '' and  MFCCLABEL = 'e' and MFCC >100 and MARKERLABEL like ';0;%'
select * from QSEGMENTEXP where   MANUALNAME = 'a' and MFCCLABEL = 'a' and MFCC > 90 and WAVFILEPATH LIKE '30-%'

--triukšmas
select count(*) from QSEGMENTEXP select count(*) from QSEGMENTEXP Where not MANUALNAME like ';%'

select * from QSEGMENTEXP Where LENGTH(MANUALNAME) = 3 and  not MANUALNAME like ';%'

select distinct(MFCCLABEL) from QSEGMENTEXP 

select distinct(LENGTH(MANUALNAME)) from QSEGMENTEXP 

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP Where LENGTH(MANUALNAME) > 4

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%a%' and MFCCLABEL like '%a%' and LENGTH(MFCCLABEL) <3

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%u%' and MFCCLABEL like '%u%' 
	and not MANUALNAME like '%a%' and not MFCCLABEL like '%a%' 
	and LENGTH(MFCCLABEL) <3

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%i%' and MFCCLABEL like '%i%' 
	and not MANUALNAME like '%a%' and not MFCCLABEL like '%a%' 
	and not MANUALNAME like '%u%' and not MFCCLABEL like '%u%' 
	and LENGTH(MFCCLABEL) <3

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%e%' and MFCCLABEL like '%e%' 
	and not MANUALNAME like '%a%' and not MFCCLABEL like '%a%' 
	and not MANUALNAME like '%u%' and not MFCCLABEL like '%u%' 
	and not MANUALNAME like '%i%' and not MFCCLABEL like '%i%' 
		and not MANUALNAME like '%E%' and not MFCCLABEL like '%E%' 
	and LENGTH(MFCCLABEL) <3


select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%E%' and MFCCLABEL like '%E%' 
	and LENGTH(MFCCLABEL) <3
	
select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP where not CORPUSENTRYNAME like 'TRI4%' and lower(MANUALNAME) like '%o%' and lower(MFCCLABEL) like '%o%' 
	and not MANUALNAME like '%a%' and not MFCCLABEL like '%a%' 
	and not MANUALNAME like '%u%' and not MFCCLABEL like '%u%' 
		and not MANUALNAME like '%e%' and not MFCCLABEL like '%e%' 
	and LENGTH(MFCCLABEL) <3


select MANUALNAME, MFCCLABEL,  PLPLABEL,LPCLABEL from QSEGMENTEXP where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%a%' and not MFCCLABEL like '%a%' and LENGTH(MFCCLABEL) <3
	
--where PLP > 0 and PLP <1






