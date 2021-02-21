/*
DROP TABLE ExperimentResult IF EXISTS;

CREATE CACHED TABLE ExperimentResult( 
id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 0) NOT NULL PRIMARY KEY,
--experimentDate DATE NOT NULL,
experimentID BIGINT NOT NULL,
resource VARCHAR NOT NULL, 
features VARCHAR NOT NULL,
totalResult FLOAT NOT NULL,
S_ONSET FLOAT,
S_STEADY FLOAT,
S_OFFSET FLOAT,
S_DELTAVAF FLOAT,
FEATURENUM INT
);

CREATE INDEX feature_idx on EXPERIMENTRESULT(TOTALRESULT)
*/
--INSERT INTO ExperimentResult(experimentID,  RESOURCE, FEATURES, TOTALRESULT) VALUES(1,'test_res', 'test_feat', 1.0 ) 

--SELECT * FROM ExperimentResult
select
FEATURES,
avg(TOTALRESULT) as avg1,
stddev_pop(TOTALRESULT) as stdev1,
min(TOTALRESULT) as min1,
max(TOTALRESULT) as max1,
avg(FEATURENUM),
count(id) counts
FROM ExperimentResult
--WHERE EXPERIMENTID = 2
--WHERE FEATURES like '%ENVELOPE MFCC0%'
WHERE RESOURCE='keyboard'
GROUP BY FEATURES
--HAVING count(id) = 7
ORDER BY avg1;
/*
original
plane
shower
traffic
keyboard
hammer
rain
*/

select * from EXPERIMENTRESULT where FEATURES like'ENERGY%SPECTRAL_FLUX%'

SELECT RESOURCE, count(ID) from ExperimentResult GROUP by RESOURCE

SELECT  * from ExperimentResult

SHUTDOWN