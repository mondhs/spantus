package org.spantus.exp.recognition.statistics;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class MergeCsvTest {

	@Test
	public void testMerge() throws IOException {
		Map<String, String> orgLabelStats = Files.readLines(new File(
				"target/testTexGridLabelStatisticsTest.csv"),
				Charsets.ISO_8859_1, new MyLineProcessor());
		Map<String, String> testLabelStats = Files.readLines(new File(
				"target/testMspntLabelStatisticsTest.csv"),
				Charsets.ISO_8859_1, new MyLineProcessor());

		List<String> merger = Lists.newArrayList(); 
		for (Entry<String, String> test : testLabelStats.entrySet()) {
			if("Label".equals(test.getKey())){
				continue;
			}
			if(orgLabelStats.get(test.getKey()) != null){
				merger.add(test.getKey()+test.getValue()+orgLabelStats.get(test.getKey()));
			}else{
				merger.add(test.getKey()+test.getValue()+";;");
			}
		}
		Files.write(LabelStatistics.getHeader()+";orgLength;OrgCount\n"+Joiner.on("\n").join(merger), 
				new File("./target/test"+getClass().getSimpleName()+".csv"), Charsets.ISO_8859_1);
		
	}

	protected class MyLineProcessor implements
			LineProcessor<Map<String, String>> {
		Map<String, String> stats = new HashMap<String, String>();

		public boolean processLine(String line) throws IOException {
			String[] strs = line.split(";");
			String key = strs[0];
			line = line.replace(key, "");
//			if("Label".equals(key)){
//				return false;
//			}
			stats.put(key, line);
			return true;
		}

		public Map<String, String> getResult() {
			return stats;
		}
	}

}
