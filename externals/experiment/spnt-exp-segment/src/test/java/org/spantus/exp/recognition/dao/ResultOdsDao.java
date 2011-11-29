package org.spantus.exp.recognition.dao;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.spantus.utils.FileUtils;

public class ResultOdsDao {
	public static final String SKIEMENIMIS = "skiemenimis";

	public static final String SEGMENTACIJA = "segmentacija";

	public static final String ATPAZINIMAS = "atpazinimas";

	public static final String LYGINIMAS = "lyginimas";

	String[] header = new String[] { "", "00dB", "05dB", "10dB", "15dB", "30dB",
			"bendrai" };

	public static String[] sheets = new String[] { SEGMENTACIJA,   SKIEMENIMIS, LYGINIMAS };

	String[] cols = new String[] { "A", "B", "C", "D", "E", "F", "G" };

	public File save(StringBuilder result, String filePath) {

		
		File out = FileUtils.findNextAvaibleFile(filePath);
		System.out.print(out.getAbsolutePath());
		Iterator<String> sheetIter = Arrays.asList(sheets).iterator();
		try {
			SpreadsheetDocument ods = SpreadsheetDocument
					.newSpreadsheetDocument();
			Table table = null;
			ods.getTableList();
			Row row = null;

			String[] cellArr = null;
			for (String line : result.toString().split("\n")) {
				if (cellArr == null) {
					cellArr = new String[] { "" };
				} else {
					cellArr = line.split(",");
				}
				if (cellArr.length == 1) {
					table = createTable(ods, table, sheetIter);
					row = table.getRowByIndex(0);
					continue;
				}
				row = table.appendRow();
				int index = 0;
				for (String cell : cellArr) {
					try {
						Double number = Double.valueOf(cell);
						row.getCellByIndex(index).setDoubleValue(number);
					} catch (NumberFormatException e) {
						row.getCellByIndex(index).setStringValue(cell);
					}

					index++;
				}
			}
			ods.getTableList().get(0).remove();
			ods.save(out);

		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return out;
	}

	
	/**
	 * 
	 * @param ods
	 * @param table
	 * @param sheetIter
	 * @return
	 */
	private Table createTable(SpreadsheetDocument ods, Table table,
			Iterator<String> sheetIter) {
		if (table != null) {
			calcPercent(ods, table);
		}
		Table newTable = Table.newTable(ods, 1, header.length);
		// newTable.removeRowsByIndex(0,2);
		newTable.setTableName(sheetIter.next());
		Row row = newTable.getRowByIndex(0);
		addHeader(row);
		// row =newTable.appendRow();
		return newTable;
	}

	/**
	 * 
	 * @param ods
	 * @param table
	 */
	private void calcPercent(SpreadsheetDocument ods, Table table) {
		Table newTable = Table.newTable(ods);
		newTable.setTableName("perc_" + table.getTableName());
		int lastRowIndex = table.getRowCount() - 1;
		for (int rowIndex = 0; rowIndex < table.getRowCount(); rowIndex++) {
			Row row = table.getRowByIndex(rowIndex);
			for (int colIndex = 0; colIndex < row.getCellCount(); colIndex++) {
				// OdfTableCell cell = row.getCellByIndex( colIndex);
				Cell newCell = newTable.getCellByPosition(colIndex, rowIndex);
				String formula = "=" + table.getTableName() + "."
						+ cols[colIndex] + (rowIndex + 1);
				if (rowIndex == 0 && colIndex == 0) {
					continue;
				} else if (rowIndex > 0 && colIndex > 0) {
					formula = formula + "/" + table.getTableName() + "."
							+ cols[colIndex] + (lastRowIndex + 1);
					newCell.setFormula(formula);
					newCell.setValueType("percentage");
					newCell.setFormatString("0.00%");
				} else {
					newCell.setFormula(formula);
					newCell.getFormatString();
				}
			}
		}
		// drawChart(ods, newTable);

	}

	// private void drawChart(SpreadsheetDocument ods, Table table) {
	// DataSet data = new DataSet();
	// String range = MessageFormat.format("{0}.A1:{0}.G14",
	// table.getTableName());
	// data.setValues(CellRangeAddressList.valueOf(range), ods, true, true,
	// true);
	// //CellRangeAddressList
	// //.valueOf("perc_segmentacija.A1:perc_segmentacija.G14");
	// Rectangle rect = new Rectangle();
	// rect.width = 15000;
	// rect.height = 8000;
	// Cell positionCell = table.getCellByPosition("I1");
	// // ods.createChart("Tikslumas", data,rect );
	// Chart chart = ods.createChart(table.getTableName(), ods,
	// CellRangeAddressList.valueOf(range), true, true, false, rect,
	// positionCell);
	// if(chart != null){
	// chart.setChartType(ChartType.BAR);
	// }
	// }

	/**
	 * 
	 * @param row
	 */
	protected void addHeader(Row row) {
		int index = 0;
		for (String string : header) {
			row.getCellByIndex(index).setStringValue(string);
			index++;
		}
	}



	



	
}
