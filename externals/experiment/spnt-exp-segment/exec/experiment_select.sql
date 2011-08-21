select count(distinct(MANUALNAME)) from QSEGMENTEXP_CLEAN where CORPUSENTRYNAME = 'TRI4_AK1'

select count(*) from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' 

select count(distinct(MANUALNAME)) from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' 

select MFCCLABEL, count(MFCCLABEL) from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' group BY MFCCLABEL order by count(MFCCLABEL) desc


select distinct(MFCCLABEL) from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' 

select distinct(LENGTH(MANUALNAME)) from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' 

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' and LENGTH(MANUALNAME) > 4

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%a%' and MFCCLABEL like '%a%' and LENGTH(MFCCLABEL) <3

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%u%' and MFCCLABEL like '%u%' 
	and not MANUALNAME like '%a%' and not MFCCLABEL like '%a%' 
	and LENGTH(MFCCLABEL) <3

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%i%' and MFCCLABEL like '%i%' 
	and not MANUALNAME like '%a%' and not MFCCLABEL like '%a%' 
	and not MANUALNAME like '%u%' and not MFCCLABEL like '%u%' 
	and LENGTH(MFCCLABEL) <3

select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%e%' and MFCCLABEL like '%e%' 
	and not MANUALNAME like '%a%' and not MFCCLABEL like '%a%' 
	and not MANUALNAME like '%u%' and not MFCCLABEL like '%u%' 
	and not MANUALNAME like '%i%' and not MFCCLABEL like '%i%' 
		and not MANUALNAME like '%E%' and not MFCCLABEL like '%E%' 
	and LENGTH(MFCCLABEL) <3


select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%E%' and MFCCLABEL like '%E%' 
	and LENGTH(MFCCLABEL) <3
	
select MANUALNAME, PLPLABEL,LPCLABEL, MFCCLABEL from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' and lower(MANUALNAME) like '%o%' and lower(MFCCLABEL) like '%o%' 
	and not MANUALNAME like '%a%' and not MFCCLABEL like '%a%' 
	and not MANUALNAME like '%u%' and not MFCCLABEL like '%u%' 
		and not MANUALNAME like '%e%' and not MFCCLABEL like '%e%' 
	and LENGTH(MFCCLABEL) <3


select MANUALNAME, MFCCLABEL,  PLPLABEL,LPCLABEL from QSEGMENTEXP_CLEAN where not CORPUSENTRYNAME like 'TRI4%' and MANUALNAME like '%a%' and not MFCCLABEL like '%a%' and LENGTH(MFCCLABEL) <3
	
--where PLP > 0 and PLP <1






