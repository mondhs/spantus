package org.spantus.exp.recognition.dao.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.spantus.exp.recognition.dao.ResultOdsDao;

public class ResultOdsDaoTest {
	ResultOdsDao dao = new ResultOdsDao();
	StringBuilder testData = new StringBuilder(
	",0dB,5dB,10dB,15dB,30dB,99999999dB,\n"+
	"Teisingai ga,87,92,95,98,91,463,\n"+
	"Teisingai ma,30,41,49,45,48,213,\n"+
	"Teisingai me,66,91,97,98,95,447,\n"+
	"Teisingai na,19,37,42,46,44,188,\n"+
	"Teisingai ne,3,22,44,44,51,164,\n"+
	"Teisingai re,63,74,83,84,87,391,\n"+
	"Teisingai ta,44,48,49,50,48,239,\n"+
	"2 skiemenys apjungti ,1,9,6,5,5,26,\n"+
	"aptiktas triukšmas ,63,72,80,77,90,382,\n"+
	"Skiemenų trūkiai ,49,48,61,70,81,309,\n"+
	"Viso aptikta,425,534,606,617,640,2822,\n"+
	"Skirtingų segmentų tipų,9,10,10,10,9,10,\n"+
	"turėjo būti,500,500,500,500,500,500,\n"+
    "\n"+   
	"a,180,192,123,115,92,702,\n"+
	"e,4,13,63,85,103,268,\n"+
	"a kaip e,1,32,117,126,144,420,\n"+
	"e kaip a,128,176,162,143,129,738,\n"+
	"triukšmas kaip a,0,0,0,0,0,0,\n"+
	"triukšmas kaip e,0,0,0,0,0,0,\n"+
	"Atsisakyta,6,15,30,44,58,153,\n"+
	"Apjungti Skiemenys,1,9,6,5,5,26,\n"+
	"Skiemenų Trūkiai ,49,48,61,70,81,309,\n");
	
	@Test
	public void testSave() {
		dao.save(testData);
	}

}
