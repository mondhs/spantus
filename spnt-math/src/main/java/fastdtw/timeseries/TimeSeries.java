// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TimeSeries.java

package fastdtw.timeseries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import fastdtw.util.Arrays;

// Referenced classes of package timeseries:
//            TimeSeriesPoint

public class TimeSeries implements ITimeSeries {

	TimeSeries() {
		labels = new ArrayList<String>();
		timeReadings = new ArrayList<Double>();
		tsArray = new ArrayList<TimeSeriesPoint>();
	}

	public TimeSeries(int numOfDimensions) {
		this();
		labels.add("Time");
		for (int x = 0; x < numOfDimensions; x++)
			labels.add("" + x);

	}

	public TimeSeries(TimeSeries origTS) {
		labels = new ArrayList<String>(origTS.labels);
		timeReadings = new ArrayList<Double>(origTS.timeReadings);
		tsArray = new ArrayList<TimeSeriesPoint>(origTS.tsArray);
	}

	public TimeSeries(String inputFile, boolean isFirstColTime) {
		this(inputFile, ZERO_ARRAY, isFirstColTime);
	}

	public TimeSeries(String inputFile, char delimiter) {
		this(inputFile, ZERO_ARRAY, true, true, delimiter);
	}

	public TimeSeries(String inputFile, boolean isFirstColTime, char delimiter) {
		this(inputFile, ZERO_ARRAY, isFirstColTime, true, delimiter);
	}

	public TimeSeries(String inputFile, boolean isFirstColTime,
			boolean isLabeled, char delimiter) {
		this(inputFile, ZERO_ARRAY, isFirstColTime, isLabeled, delimiter);
	}

	public TimeSeries(String inputFile, int colToInclude[],
			boolean isFirstColTime) {
		this(inputFile, colToInclude, isFirstColTime, true, ',');
	}

	public TimeSeries(String inputFile, int colToInclude[],
			boolean isFirstColTime, boolean isLabeled, char delimiter) {
		this();
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = br.readLine();
			StringTokenizer st = new StringTokenizer(line, String
					.valueOf(delimiter));
			if (isLabeled) {
				for (int currentCol = 0; st.hasMoreTokens(); currentCol++) {
					String currentToken = st.nextToken();
					if (colToInclude.length == 0
							|| Arrays.contains(colToInclude, currentCol))
						labels.add(currentToken);
				}

				if (labels.size() == 0)
					throw new InternalError(
							"ERROR:  The first row must contain label information, it is empty!");
				if (!isFirstColTime)
					labels.add(0, "Time");
				else if (isFirstColTime
						&& !((String) labels.get(0)).equalsIgnoreCase("Time"))
					throw new InternalError(
							"ERROR:  The time column (1st col) in a time series must be labeled as 'Time', '"
									+ labels.get(0) + "' was found instead");
			} else {
				if (colToInclude == null || colToInclude.length == 0) {
					labels.add("Time");
					if (isFirstColTime)
						st.nextToken();
					int currentCol = 1;
					for (; st.hasMoreTokens(); labels.add(new String("c"
							+ currentCol++)))
						st.nextToken();

				} else {
					java.util.Arrays.sort(colToInclude);
					labels.add("Time");
					for (int c = 0; c < colToInclude.length; c++)
						if (colToInclude[c] > 0)
							labels.add(new String("c" + c));

				}
				br.close();
				br = new BufferedReader(new FileReader(inputFile));
			}
			do {
				if ((line = br.readLine()) == null)
					break;
				if (line.length() > 0) {
					st = new StringTokenizer(line, ",");
					ArrayList<Double> currentLineValues = new ArrayList<Double>();
					for (int currentCol = 0; st.hasMoreTokens(); currentCol++) {
						String currentToken = st.nextToken();
						if (colToInclude.length != 0
								&& !Arrays.contains(colToInclude, currentCol))
							continue;
						Double nextValue;
						try {
							nextValue = Double.valueOf(currentToken);
						} catch (NumberFormatException e) {
							throw new InternalError("ERROR:  '" + currentToken
									+ "' is not a valid number");
						}
						currentLineValues.add(nextValue);
					}

					if (isFirstColTime)
						timeReadings.add(currentLineValues.get(0));
					else
						timeReadings.add(new Double(timeReadings.size()));
					int firstMeasurement;
					if (isFirstColTime)
						firstMeasurement = 1;
					else
						firstMeasurement = 0;
					TimeSeriesPoint readings = new TimeSeriesPoint(
							currentLineValues.subList(firstMeasurement,
									currentLineValues.size()));
					tsArray.add(readings);
				}
			} while (true);
		} catch (FileNotFoundException e) {
			throw new InternalError("ERROR:  The file '" + inputFile
					+ "' was not found.");
		} catch (IOException e) {
			throw new InternalError("ERROR:  Problem reading the file '"
					+ inputFile + "'.");
		}
	}

	public void save(File outFile) throws IOException {
		PrintWriter out = new PrintWriter(new FileOutputStream(outFile));
		out.write(toString());
		out.flush();
		out.close();
	}

	/* (non-Javadoc)
	 * @see fastdtw.timeseries.ITimeSeries#clear()
	 */
//	public void clear() {
//		labels.clear();
//		timeReadings.clear();
//		tsArray.clear();
//	}

	/* (non-Javadoc)
	 * @see fastdtw.timeseries.ITimeSeries#size()
	 */
	public int size() {
		return timeReadings.size();
	}


	public int numOfDimensions() {
		return labels.size() - 1;
	}

	public Float getTimeAtNthPoint(int n) {
		return timeReadings.get(n).floatValue();
	}

	public String getLabel(int index) {
		return labels.get(index);
	}

	public String[] getLabelsArr() {
		String labelArr[] = new String[labels.size()];
		for (int x = 0; x < labels.size(); x++)
			labelArr[x] = (String) labels.get(x);

		return labelArr;
	}

	public ArrayList<String> getLabels() {
		return labels;
	}

	public void setLabels(String newLabels[]) {
		labels.clear();
		for (int x = 0; x < newLabels.length; x++)
			labels.add(newLabels[x]);

	}

	public void setLabels(ArrayList<String> newLabels) {
		labels.clear();
		for (int x = 0; x < newLabels.size(); x++)
			labels.add(newLabels.get(x));

	}

	public double getMeasurement(int pointIndex, int valueIndex) {
		return ((TimeSeriesPoint) tsArray.get(pointIndex)).get(valueIndex);
	}

	public double getMeasurement(int pointIndex, String valueLabel) {
		int valueIndex = labels.indexOf(valueLabel);
		if (valueIndex < 0)
			throw new InternalError("ERROR:  the label '" + valueLabel
					+ "' was " + "not one of:  " + labels);
		else
			return ((TimeSeriesPoint) tsArray.get(pointIndex))
					.get(valueIndex - 1);
	}

	public double getMeasurement(double time, int valueIndex) {
		return 0.0D;
	}

	public double getMeasurement(double time, String valueLabel) {
		int valueIndex = labels.indexOf(valueLabel);
		if (valueIndex < 0)
			throw new InternalError("ERROR:  the label '" + valueLabel
					+ "' was " + "not one of:  " + labels);
		else
			return getMeasurement(time, valueIndex);
	}

	public List<Float> getMeasurementVector(int pointIndex) {
		return ((TimeSeriesPoint) tsArray.get(pointIndex)).toList();
	}

	public double[] getMeasurementVector(double time) {
		return null;
	}

	public void setMeasurement(int pointIndex, int valueIndex, double newValue) {
		((TimeSeriesPoint) tsArray.get(pointIndex)).set(valueIndex, newValue);
	}

	public void addFirst(double time, TimeSeriesPoint values) {
		if (labels.size() != values.size() + 1)
			throw new InternalError("ERROR:  The TimeSeriesPoint: " + values
					+ " contains the wrong number of values. " + "expected:  "
					+ labels.size() + ", " + "found: " + values.size());
		if (time >= ((Double) timeReadings.get(0)).doubleValue()) {
			throw new InternalError(
					"ERROR:  The point being inserted into the beginning of the time series does not have the correct time sequence. ");
		} else {
			timeReadings.add(0, new Double(time));
			tsArray.add(0, values);
			return;
		}
	}

	public void addLast(double time, TimeSeriesPoint values) {
		if (labels.size() != values.size() + 1)
			throw new InternalError("ERROR:  The TimeSeriesPoint: " + values
					+ " contains the wrong number of values. " + "expected:  "
					+ labels.size() + ", " + "found: " + values.size());
		if (size() > 0
				&& time <= ((Double) timeReadings.get(timeReadings.size() - 1))
						.doubleValue()) {
			throw new InternalError(
					"ERROR:  The point being inserted at the end of the time series does not have the correct time sequence. ");
		} else {
			timeReadings.add(new Double(time));
			tsArray.add(values);
			return;
		}
	}

	public void removeFirst() {
		if (size() == 0) {
			System.err
					.println("WARNING:  TimeSeriesPoint:removeFirst() called on an empty time series!");
		} else {
			timeReadings.remove(0);
			tsArray.remove(0);
		}
	}

	public void removeLast() {
		if (size() == 0) {
			System.err
					.println("WARNING:  TimeSeriesPoint:removeLast() called on an empty time series!");
		} else {
			timeReadings.remove(timeReadings.size() - 1);
			tsArray.remove(timeReadings.size() - 1);
		}
	}

	public void normalize() {
		double mean[] = new double[numOfDimensions()];
		for (int col = 0; col < numOfDimensions(); col++) {
			double currentSum = 0.0D;
			for (int row = 0; row < size(); row++)
				currentSum += getMeasurement(row, col);

			mean[col] = currentSum / (double) size();
		}

		double stdDev[] = new double[numOfDimensions()];
		for (int col = 0; col < numOfDimensions(); col++) {
			double variance = 0.0D;
			for (int row = 0; row < size(); row++)
				variance += Math.abs(getMeasurement(row, col) - mean[col]);

			stdDev[col] = variance / (double) size();
		}

		for (int row = 0; row < size(); row++) {
			for (int col = 0; col < numOfDimensions(); col++)
				if (stdDev[col] == 0.0D)
					setMeasurement(row, col, 0.0D);
				else
					setMeasurement(row, col,
							(getMeasurement(row, col) - mean[col])
									/ stdDev[col]);

		}

	}

	public String toString() {
		StringBuffer outStr = new StringBuffer();
		for (int r = 0; r < timeReadings.size(); r++) {
			TimeSeriesPoint values = (TimeSeriesPoint) tsArray.get(r);
			for (int c = 0; c < values.size(); c++)
				outStr.append(values.get(c));

			if (r < timeReadings.size() - 1)
				outStr.append("\n");
		}

		return outStr.toString();
	}

//	private static char determineDelimiter(String filePath) {
		// char DEFAULT_DELIMITER = ',';
		// String line;
		// int x;
		// BufferedReader in = new BufferedReader(new FileReader(filePath));
		// line = in.readLine().trim();
		// if(!Character.isDigit(line.charAt(0)))
		// line = in.readLine();
		// in.close();
		// x = 0;
		// _L1:
		// if(x >= line.length())
		// break MISSING_BLOCK_LABEL_120;
		// if(!Character.isDigit(line.charAt(x)) && line.charAt(x) != '.' &&
		// line.charAt(x) != '-' && Character.toUpperCase(line.charAt(x)) !=
		// 'E')
		// return line.charAt(x);
		// x++;
		// goto _L1
		// return ',';
		// IOException e;
		// e;
//		return ',';
//	}

//	private static double extractFirstNumber(String str) {
//		StringBuffer numStr = new StringBuffer();
//		for (int x = 0; x < str.length(); x++)
//			if (Character.isDigit(str.charAt(x)) || str.charAt(x) == '.'
//					|| str.charAt(x) == '-'
//					|| Character.toUpperCase(str.charAt(x)) == 'E')
//				numStr.append(str.charAt(x));
//			else
//				Double.parseDouble(numStr.toString());
//
//		return -1D;
//	}

//	private static boolean determineIsFirstColTime(String filePath) {
		// boolean DEFAULT_VALUE = false;
		// Vector possibleTimeValues;
		// BufferedReader in = new BufferedReader(new FileReader(filePath));
		// double EQUALITY_FLEXIBILITY_PCT = 0.001D;
		// int NUM_OF_VALUES_TO_CMP = 100;
		// possibleTimeValues = new Vector(100);
		// for(String line = in.readLine(); possibleTimeValues.size() < 100 &&
		// (line = in.readLine()) != null; possibleTimeValues.add(new
		// Double(extractFirstNumber(line))));
		// if(possibleTimeValues.size() <= 1)
		// return false;
		// if(possibleTimeValues.size() > 1 &&
		// possibleTimeValues.get(1).equals(possibleTimeValues.get(0)))
		// return false;
		// double expectedDiff;
		// double flexibility;
		// int x;
		// expectedDiff = ((Double)possibleTimeValues.get(1)).doubleValue() -
		// ((Double)possibleTimeValues.get(0)).doubleValue();
		// flexibility = expectedDiff * 0.001D;
		// x = 1;
		// _L1:
		// if(x >= possibleTimeValues.size())
		// break MISSING_BLOCK_LABEL_224;
		// if(Math.abs(((Double)possibleTimeValues.get(x)).doubleValue() -
		// ((Double)possibleTimeValues.get(x - 1)).doubleValue() - expectedDiff)
		// > Math.abs(flexibility))
		// return false;
		// x++;
		// goto _L1
		// return true;
		// IOException e;
		// e;
//		return false;
//	}

	protected void setMaxCapacity(int capacity) {
		timeReadings.ensureCapacity(capacity);
		tsArray.ensureCapacity(capacity);
	}

	private static final int ZERO_ARRAY[] = new int[0];
	private static final boolean DEFAULT_IS_TIME_1ST_COL = true;
	private static final char DEFAULT_DELIMITER = 44;
	private static final boolean DEFAULT_IS_LABELED = true;
	private final ArrayList<String> labels;
	private final ArrayList<Double> timeReadings;
	private final ArrayList<TimeSeriesPoint> tsArray;

}
