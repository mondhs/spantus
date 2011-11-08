package org.spantus.exp.recognition.dao.test;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.spantus.exp.recognition.dao.ChartJFreeDao;
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
	"Klaidingai 2 skiemenys apjungti ,1,9,6,5,5,26,\n"+
	"Klaidingai aptiktas triukšmas ,63,72,80,77,90,382,\n"+
	"Klaidingai Skiemenų trūkiai ,49,48,61,70,81,309,\n"+
	"Viso aptikta,425,534,606,617,640,2822,\n"+
	"Skirtingų segmentų tipų,9,10,10,10,9,10,\n"+
	"turėjo būti,500,500,500,500,500,500,\n"+
    "\n"+   
	"Teisingai a,180,192,123,115,92,702,\n"+
	"Teisingai e,4,13,63,85,103,268,\n"+
	"Klaidingai a kaip e,1,32,117,126,144,420,\n"+
	"Klaidingai e kaip a,128,176,162,143,129,738,\n"+
	"Klaidingai triukšmas kaip a,0,0,0,0,0,0,\n"+
	"Klaidingai triukšmas kaip e,0,0,0,0,0,0,\n"+
	"Atsisakyta,6,15,30,44,58,153,\n"+
	"Klaidingai Apjungti Skiemenys,1,9,6,5,5,26,\n"+
	"Klaidingai Skiemenų Trūkiai ,49,48,61,70,81,309,\n"+
	"Viso aptikta segmentavime,425,534,606,617,640,2822,\n"+
    "\n"+
    "Teisingai segmentuota,312,404,459,464,463,2102,\n"+
    "Teisingai atpažinta,183,199,182,198,192,954,"

//    "Klaidingai segmentuota,0.38,0.19,0.08,0.07,0.07,3.21\n"+
//    "Klaidingai atpažinta,0.63,0.59,0.63,0.6,0.61,0.94"
    );
	
	@Test @Ignore
	public void testSave() {
		dao.save(testData, "./target/data/test.ods");
	}
	@Test 
	public void testChart() throws Exception {
		ChartJFreeDao chartDao = new ChartJFreeDao();
		File ods = new File("./target/data/results.ods");
//		ods = dao.save(testData,ods.getAbsolutePath());
		chartDao.draw(SpreadsheetDocument.loadDocument(ods));
	}
}
