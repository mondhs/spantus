/*
DROP TABLE ExperimentResult;
DROP SEQUENCE ExperimentResult_Seq;

CREATE SEQUENCE ExperimentResult_Seq;
CREATE TABLE ExperimentResult( 
id BIGINT PRIMARY  KEY DEFAULT nextval('ExperimentResult_Seq'),
--experimentDate DATE NOT NULL,
experimentID BIGINT NOT NULL,
RESOURCE VARCHAR NOT NULL, 
FEATURES VARCHAR NOT NULL,
TOTALRESULT FLOAT NOT NULL,
S_ONSET FLOAT,
S_STEADY FLOAT,
S_OFFSET FLOAT,
S_DELTAVAF FLOAT,
FEATURENUM INT
);

CREATE INDEX feature_idx on EXPERIMENTRESULT(FEATURES)
*/
--INSERT INTO ExperimentResult(experimentID,  RESOURCE, FEATURES, TOTALRESULT) VALUES(1,'test_res', 'test_feat', 1.0 ) 

--SELECT * FROM ExperimentResult
select  FEATURES, avg(TOTALRESULT) as avg1, stddev_pop(TOTALRESULT) as stdev1, min(TOTALRESULT) as min1, max(TOTALRESULT) as max1 FROM ExperimentResult GROUP BY FEATURES ORDER BY avg1
SELECT RESOURCE, count(ID) from ExperimentResult GROUP by RESOURCE
SELECT  count(*) from ExperimentResult where TOTALRESULT > .90

SHUTDOWN




select
FEATURES, 
avg(TOTALRESULT) as total1, stddev_pop(TOTALRESULT) as stdev1, 
avg(S_ONSET) as onset1, AVG(S_OFFSET) as offset1, avg(S_STEADY) as steady1, avg(S_DELTAVAF) as DELTAVAF1,
AVG(FEATURENUM) as avgfeatureNum
from EXPERIMENTRESULT
where not RESOURCE = ''
GROUP BY FEATURES
HAVING count(id) > 5 and avgfeatureNum=6
ORDER BY total1