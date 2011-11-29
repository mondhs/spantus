package org.spantus.exp.recognition.dao.chart;

import java.util.List;

import org.jfree.data.KeyedObjects2D;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.util.PublicCloneable;

@SuppressWarnings("serial")
public class AsymmetricStatisticalCategoryDataset extends AbstractDataset
	implements StatisticalCategoryDataset, RangeInfo, PublicCloneable {
   		
	    /** Storage for the data. */
	    private KeyedObjects2D data;

	    /** The minimum range value. */
	    private double minimumRangeValue;

	    /** The row index for the minimum range value. */
	    private int minimumRangeValueRow;

	    /** The column index for the minimum range value. */
	    private int minimumRangeValueColumn;

	    /** The minimum range value including the upper value. */
	    private double minimumRangeValueIncUpper;

	    /**
	     * The row index for the minimum range value (including the upper value).
	     */
	    private int minimumRangeValueIncUpperRow;

	    /**
	     * The column index for the minimum range value (including the upper value).
	     */
	    private int minimumRangeValueIncUpperColumn;
	    
	    /** The minimum range value including the lower value. */
	    private double minimumRangeValueIncLower;

	    /**
	     * The row index for the minimum range value (including the lower value).
	     */
	    private int minimumRangeValueIncLowerRow;

	    /**
	     * The column index for the minimum range value (including the lower value).
	     */
	    private int minimumRangeValueIncLowerColumn;

	    /** The maximum range value. */
	    private double maximumRangeValue;

	    /** The row index for the maximum range value. */
	    private int maximumRangeValueRow;

	    /** The column index for the maximum range value. */
	    private int maximumRangeValueColumn;

	    /** The maximum range value including the upper value. */
	    private double maximumRangeValueIncUpper;

	    /**
	     * The row index for the maximum range value (including the upper value).
	     */
	    private int maximumRangeValueIncUpperRow;

	    /**
	     * The column index for the maximum range value (including the upper value).
	     */
	    private int maximumRangeValueIncUpperColumn;
	    
	    /** The maximum range value including the lower value. */
	    private double maximumRangeValueIncLower;

	    /**
	     * The row index for the maximum range value (including the lower value).
	     */
	    private int maximumRangeValueIncLowerRow;

	    /**
	     * The column index for the maximum range value (including the lower value).
	     */
	    private int maximumRangeValueIncLowerColumn;


	    /**
	     * Creates a new dataset.
	     */
	    public AsymmetricStatisticalCategoryDataset() {
	        this.data = new KeyedObjects2D();
	        this.minimumRangeValue = Double.NaN;
	        this.minimumRangeValueRow = -1;
	        this.minimumRangeValueColumn = -1;
	        this.maximumRangeValue = Double.NaN;
	        this.maximumRangeValueRow = -1;
	        this.maximumRangeValueColumn = -1;
	        this.minimumRangeValueIncUpper = Double.NaN;
	        this.minimumRangeValueIncUpperRow = -1;
	        this.minimumRangeValueIncUpperColumn = -1;
	        this.maximumRangeValueIncUpper = Double.NaN;
	        this.maximumRangeValueIncUpperRow = -1;
	        this.maximumRangeValueIncUpperColumn = -1;
	        
	        this.minimumRangeValueIncLower = Double.NaN;
	        this.minimumRangeValueIncLowerRow = -1;
	        this.minimumRangeValueIncLowerColumn = -1;
	        this.maximumRangeValueIncLower = Double.NaN;
	        this.maximumRangeValueIncLowerRow = -1;
	        this.maximumRangeValueIncLowerColumn = -1;
	    }
	     
        
        
	    /**
	     * Returns the mean value for an item.
	     *
	     * @param row  the row index (zero-based).
	     * @param column  the column index (zero-based).
	     *
	     * @return The mean value (possibly <code>null</code>).
	     */
	    public Number getMeanValue(int row, int column) {
	        Number result = null;
	        MeanWithAsymmetricErrorBar masd = (MeanWithAsymmetricErrorBar)
	                this.data.getObject(row, column);
	        if (masd != null) {
	            result = masd.getMean();
	        }
	        return result;
	    }
	    
	    /**
	     * Returns the value for an item (for this dataset, the mean value is
	     * returned).
	     *
	     * @param row  the row index.
	     * @param column  the column index.
	     *
	     * @return The value (possibly <code>null</code>).
	     */
	    public Number getValue(int row, int column) {
	        return getMeanValue(row, column);
	    }
	    
	    /**
	     * Returns the value for an item (for this dataset, the mean value is
	     * returned).
	     *
	     * @param rowKey  the row key.
	     * @param columnKey  the columnKey.
	     *
	     * @return The value (possibly <code>null</code>).
	     */
	    public Number getValue(Comparable rowKey, Comparable columnKey) {
	        return getMeanValue(rowKey, columnKey);
	    }
	    
	    /**
	     * Returns the mean value for an item.
	     *
	     * @param rowKey  the row key.
	     * @param columnKey  the columnKey.
	     *
	     * @return The mean value (possibly <code>null</code>).
	     */
	    public Number getMeanValue(Comparable rowKey, Comparable columnKey) {
	        Number result = null;
	        MeanWithAsymmetricErrorBar masd = (MeanWithAsymmetricErrorBar)
	                this.data.getObject(rowKey, columnKey);
	        if (masd != null) {
	            result = masd.getMean();
	        }
	        return result;
	    }
	    /**
	     * returns the lower bound of the error bar
	     *
	     * @param row
	     * @param column
	     * @return the lower value of the error bar
	     */
	    public Number getLowerValue(int row, int column) {
	            Number result = null;
	            MeanWithAsymmetricErrorBar mwaeb
	                = (MeanWithAsymmetricErrorBar) this.data.getObject(row, column);
	            if (mwaeb != null) {
	                result = mwaeb.getLower();
	            }
	            return result;
	        }

	       /**
	        * returns the upper bound of the error bar
	        *
	        * @param row
	        * @param column
	        * @return the upper value of the error bar
	        */
	        public Number getUpperValue(int row, int column) {
	            Number result = null;
	            MeanWithAsymmetricErrorBar mwaeb
	                = (MeanWithAsymmetricErrorBar) this.data.getObject(row, column);
	            if (mwaeb != null) {
	                result = mwaeb.getUpper();
	            }
	            return result;
	        }	  
	        
	        /**
	         * Returns the standard deviation value for an item.
	         *
	         * @param rowKey  the row key.
	         * @param columnKey  the columnKey.
	         *
	         * @return The standard deviation (possibly <code>null</code>).
	         */
	        public Number getUpperValue(Comparable rowKey, Comparable columnKey) {
	            Number result = null;
	            MeanWithAsymmetricErrorBar masd = (MeanWithAsymmetricErrorBar)
	                    this.data.getObject(rowKey, columnKey);
	            if (masd != null) {
	                result = masd.getUpper();
	            }
	            return result;
	        }
	        
	        /**
	         * Returns the standard deviation value for an item.
	         *
	         * @param rowKey  the row key.
	         * @param columnKey  the columnKey.
	         *
	         * @return The standard deviation (possibly <code>null</code>).
	         */
	        public Number getLowerValue(Comparable rowKey, Comparable columnKey) {
	            Number result = null;
	            MeanWithAsymmetricErrorBar masd = (MeanWithAsymmetricErrorBar)
	                    this.data.getObject(rowKey, columnKey);
	            if (masd != null) {
	                result = masd.getLower();
	            }
	            return result;
	        }
    
	    /**
	     * Returns the column index for a given key.
	     *
	     * @param key  the column key (<code>null</code> not permitted).
	     *
	     * @return The column index.
	     */
	    public int getColumnIndex(Comparable key) {
	        // defer null argument check
	        return this.data.getColumnIndex(key);
	    }

	    /**
	     * Returns a column key.
	     *
	     * @param column  the column index (zero-based).
	     *
	     * @return The column key.
	     */
	    public Comparable getColumnKey(int column) {
	        return this.data.getColumnKey(column);
	    }

	    /**
	     * Returns the column keys.
	     *
	     * @return The keys.
	     */
	    public List getColumnKeys() {
	        return this.data.getColumnKeys();
	    }

	    /**
	     * Returns the row index for a given key.
	     *
	     * @param key  the row key (<code>null</code> not permitted).
	     *
	     * @return The row index.
	     */
	    public int getRowIndex(Comparable key) {
	        // defer null argument check
	        return this.data.getRowIndex(key);
	    }

	    /**
	     * Returns a row key.
	     *
	     * @param row  the row index (zero-based).
	     *
	     * @return The row key.
	     */
	    public Comparable getRowKey(int row) {
	        return this.data.getRowKey(row);
	    }

	    /**
	     * Returns the row keys.
	     *
	     * @return The keys.
	     */
	    public List getRowKeys() {
	        return this.data.getRowKeys();
	    }

	    /**
	     * Returns the number of rows in the table.
	     *
	     * @return The row count.
	     *
	     * @see #getColumnCount()
	     */
	    public int getRowCount() {
	        return this.data.getRowCount();
	    }

	    /**
	     * Returns the number of columns in the table.
	     *
	     * @return The column count.
	     *
	     * @see #getRowCount()
	     */
	    public int getColumnCount() {
	        return this.data.getColumnCount();
	    }

	    /**
	     * Adds a mean, upper and lower bound to the table.
	     *
	     * @param mean  the mean.
	     * @param upperValue  the upper CI value.
	     * @param lowerValue  the lower CI value.
	     * @param rowKey  the row key.
	     * @param columnKey  the column key.
	     */
	    public void add(double mean, double upperValue, double lowerValue,
	                    Comparable rowKey, Comparable columnKey) {
	        add(new Double(mean), new Double(upperValue), new Double (lowerValue),
	        		rowKey, columnKey);
	    }
	    
	    /**
	     * Adds a mean and upper and lower bound to the table.
	     *
	     * @param mean  the mean.
	     * @param upperValue  the upper CI value.
	     * @param lowerValue  the lower CI value.
	     * @param rowKey  the row key.
	     * @param columnKey  the column key.
	     */
	    public void add(Number mean, Number upperValue, Number lowerValue,
	                    Comparable rowKey, Comparable columnKey) {
	    	MeanWithAsymmetricErrorBar item = new MeanWithAsymmetricErrorBar(
	                mean, upperValue, lowerValue);
	        this.data.addObject(item, rowKey, columnKey);

	        double m = Double.NaN;
	        double ul = Double.NaN;
	        double ll = Double.NaN;
	        
	        if (mean != null) {
	            m = mean.doubleValue();
	        }
	        if (upperValue != null) {
	            ul = upperValue.doubleValue();
	        }
	        if (lowerValue != null) {
	            ll = lowerValue.doubleValue();
	        }

	        // update cached range values
	        int r = this.data.getColumnIndex(columnKey);
	        int c = this.data.getRowIndex(rowKey);
	        if ((r == this.maximumRangeValueRow && c
	                == this.maximumRangeValueColumn) || (r
	                == this.maximumRangeValueIncUpperRow && c
	                == this.maximumRangeValueIncUpperColumn) || (r
	                == this.maximumRangeValueIncLowerRow && c
	     	        == this.maximumRangeValueIncLowerColumn) || (r
	                == this.minimumRangeValueRow && c
	                == this.minimumRangeValueColumn) || (r
	                == this.minimumRangeValueIncUpperRow && c
	                == this.minimumRangeValueIncUpperColumn) || (r
	                == this.minimumRangeValueIncLowerRow && c
	    	        == this.minimumRangeValueIncLowerColumn)){

	            // iterate over all data items and update mins and maxes
	            updateBounds();
	        }
	        else {
	            if (!Double.isNaN(m)) {
	                if (Double.isNaN(this.maximumRangeValue)
	                        || m > this.maximumRangeValue) {
	                    this.maximumRangeValue = m;
	                    this.maximumRangeValueRow = r;
	                    this.maximumRangeValueColumn = c;
	                }
	            }

	            if (!Double.isNaN(m + ul)) {
	                if (Double.isNaN(this.maximumRangeValueIncUpper)
	                        || (m + ul) > this.maximumRangeValueIncUpper) {
	                    this.maximumRangeValueIncUpper = m + ul;
	                    this.maximumRangeValueIncUpperRow = r;
	                    this.maximumRangeValueIncUpperColumn = c;
	                }
	            }
	            
	           if (!Double.isNaN(m + ll)) {
	                if (Double.isNaN(this.maximumRangeValueIncLower)
	                        || (m + ll) > this.maximumRangeValueIncLower) {
	                    this.maximumRangeValueIncLower = m + ll;
	                    this.maximumRangeValueIncLowerRow = r;
	                    this.maximumRangeValueIncLowerColumn = c;
	                }
	            }
	           
	            if (!Double.isNaN(m)) {
	                if (Double.isNaN(this.minimumRangeValue)
	                        || m < this.minimumRangeValue) {
	                    this.minimumRangeValue = m;
	                    this.minimumRangeValueRow = r;
	                    this.minimumRangeValueColumn = c;
	                }
	            }

	            if (!Double.isNaN(m - ul)) {
	                if (Double.isNaN(this.minimumRangeValueIncUpper)
	                        || (m - ul) < this.minimumRangeValueIncUpper) {
	                    this.minimumRangeValueIncUpper = m - ul;
	                    this.minimumRangeValueIncUpperRow = r;
	                    this.minimumRangeValueIncUpperColumn = c;
	                }
	            }
	            
	            if (!Double.isNaN(m - ll)) {
	                if (Double.isNaN(this.minimumRangeValueIncLower)
	                        || (m - ll) < this.minimumRangeValueIncLower) {
	                    this.minimumRangeValueIncLower = m - ll;
	                    this.minimumRangeValueIncLowerRow = r;
	                    this.minimumRangeValueIncLowerColumn = c;
	                }
	            }
	        }
	        fireDatasetChanged();
	    }

	    /**
	     * Removes an item from the dataset and sends a {@link DatasetChangeEvent}
	     * to all registered listeners.
	     *
	     * @param rowKey  the row key (<code>null</code> not permitted).
	     * @param columnKey  the column key (<code>null</code> not permitted).
	     *
	     * @see #add(double, double, Comparable, Comparable)
	     *
	     * @since 1.0.7
	     */
	    public void remove(Comparable rowKey, Comparable columnKey) {
	        // defer null argument checks
	        int r = getRowIndex(rowKey);
	        int c = getColumnIndex(columnKey);
	        this.data.removeObject(rowKey, columnKey);

	        // if this cell held a maximum and/or minimum value, we'll need to
	        // update the cached bounds...
	        if ((r == this.maximumRangeValueRow && c
	                == this.maximumRangeValueColumn) || (r
	                == this.maximumRangeValueIncUpperRow && c
	                == this.maximumRangeValueIncUpperColumn) || (r
	                == this.maximumRangeValueIncLowerRow && c
	    	        == this.maximumRangeValueIncLowerColumn) || (r
	                == this.minimumRangeValueRow && c
	                == this.minimumRangeValueColumn) || (r
	                == this.minimumRangeValueIncUpperRow && c
	                == this.minimumRangeValueIncUpperColumn) || (r
	                == this.minimumRangeValueIncLowerRow && c
	     	        == this.minimumRangeValueIncLowerColumn)){

	            // iterate over all data items and update mins and maxes
	            updateBounds();
	        }

	        fireDatasetChanged();
	    }


	    /**
	     * Removes a row from the dataset and sends a {@link DatasetChangeEvent}
	     * to all registered listeners.
	     *
	     * @param rowIndex  the row index.
	     *
	     * @see #removeColumn(int)
	     *
	     * @since 1.0.7
	     */
	    public void removeRow(int rowIndex) {
	        this.data.removeRow(rowIndex);
	        updateBounds();
	        fireDatasetChanged();
	    }

	    /**
	     * Removes a row from the dataset and sends a {@link DatasetChangeEvent}
	     * to all registered listeners.
	     *
	     * @param rowKey  the row key (<code>null</code> not permitted).
	     *
	     * @see #removeColumn(Comparable)
	     *
	     * @since 1.0.7
	     */
	    public void removeRow(Comparable rowKey) {
	        this.data.removeRow(rowKey);
	        updateBounds();
	        fireDatasetChanged();
	    }

	    /**
	     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
	     * to all registered listeners.
	     *
	     * @param columnIndex  the column index.
	     *
	     * @see #removeRow(int)
	     *
	     * @since 1.0.7
	     */
	    public void removeColumn(int columnIndex) {
	        this.data.removeColumn(columnIndex);
	        updateBounds();
	        fireDatasetChanged();
	    }

	    /**
	     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
	     * to all registered listeners.
	     *
	     * @param columnKey  the column key (<code>null</code> not permitted).
	     *
	     * @see #removeRow(Comparable)
	     *
	     * @since 1.0.7
	     */
	    public void removeColumn(Comparable columnKey) {
	        this.data.removeColumn(columnKey);
	        updateBounds();
	        fireDatasetChanged();
	    }

	    /**
	     * Clears all data from the dataset and sends a {@link DatasetChangeEvent}
	     * to all registered listeners.
	     *
	     * @since 1.0.7
	     */
	    public void clear() {
	        this.data.clear();
	        updateBounds();
	        fireDatasetChanged();
	    }

	    /**
	     * Iterate over all the data items and update the cached bound values.
	     */
	    private void updateBounds() {
	        this.maximumRangeValue = Double.NaN;
	        this.maximumRangeValueRow = -1;
	        this.maximumRangeValueColumn = -1;
	        this.minimumRangeValue = Double.NaN;
	        this.minimumRangeValueRow = -1;
	        this.minimumRangeValueColumn = -1;
	        
	        this.maximumRangeValueIncUpper = Double.NaN;
	        this.maximumRangeValueIncUpperRow = -1;
	        this.maximumRangeValueIncUpperColumn = -1;
	        this.minimumRangeValueIncUpper = Double.NaN;
	        this.minimumRangeValueIncUpperRow = -1;
	        this.minimumRangeValueIncUpperColumn = -1;
	        
	        this.maximumRangeValueIncLower = Double.NaN;
	        this.maximumRangeValueIncLowerRow = -1;
	        this.maximumRangeValueIncLowerColumn = -1;
	        this.minimumRangeValueIncLower = Double.NaN;
	        this.minimumRangeValueIncLowerRow = -1;
	        this.minimumRangeValueIncLowerColumn = -1;

	        int rowCount = this.data.getRowCount();
	        int columnCount = this.data.getColumnCount();
	        for (int r = 0; r < rowCount; r++) {
	            for (int c = 0; c < columnCount; c++) {
	                double m = Double.NaN;
	                double ul = Double.NaN;
	                double ll = Double.NaN;
	                
	                MeanWithAsymmetricErrorBar masd = (MeanWithAsymmetricErrorBar)
	                        this.data.getObject(r, c);
	                if (masd == null) {
	                    continue;
	                }
	                m = masd.getMeanValue();
	                ul = masd.getUpperValue();
	                ll = masd.getLowerValue();

	                if (!Double.isNaN(m)) {

	                    // update the max value
	                    if (Double.isNaN(this.maximumRangeValue)) {
	                        this.maximumRangeValue = m;
	                        this.maximumRangeValueRow = r;
	                        this.maximumRangeValueColumn = c;
	                    }
	                    else {
	                        if (m > this.maximumRangeValue) {
	                            this.maximumRangeValue = m;
	                            this.maximumRangeValueRow = r;
	                            this.maximumRangeValueColumn = c;
	                        }
	                    }

	                    // update the min value
	                    if (Double.isNaN(this.minimumRangeValue)) {
	                        this.minimumRangeValue = m;
	                        this.minimumRangeValueRow = r;
	                        this.minimumRangeValueColumn = c;
	                    }
	                    else {
	                        if (m < this.minimumRangeValue) {
	                            this.minimumRangeValue = m;
	                            this.minimumRangeValueRow = r;
	                            this.minimumRangeValueColumn = c;
	                        }
	                    }

	                    if (!Double.isNaN(ul)) {
	                        // update the max value
	                        if (Double.isNaN(this.maximumRangeValueIncUpper)) {
	                            this.maximumRangeValueIncUpper = m + ul;
	                            this.maximumRangeValueIncUpperRow = r;
	                            this.maximumRangeValueIncUpperColumn = c;
	                        }
	                        else {
	                            if (m + ul > this.maximumRangeValueIncUpper) {
	                                this.maximumRangeValueIncUpper = m + ul;
	                                this.maximumRangeValueIncUpperRow = r;
	                                this.maximumRangeValueIncUpperColumn = c;
	                            }
	                        }

	                        // update the min value
	                        if (Double.isNaN(this.minimumRangeValueIncUpper)) {
	                            this.minimumRangeValueIncUpper = m - ul;
	                            this.minimumRangeValueIncUpperRow = r;
	                            this.minimumRangeValueIncUpperColumn = c;
	                        }
	                        else {
	                            if (m - ul < this.minimumRangeValueIncLower) {
	                                this.minimumRangeValueIncUpper = m - ul;
	                                this.minimumRangeValueIncUpperRow = r;
	                                this.minimumRangeValueIncUpperColumn = c;
	                            }
	                        }
	                      }

	                    if (!Double.isNaN(ll)) {
	                        // update the max value
	                        if (Double.isNaN(this.maximumRangeValueIncLower)) {
	                            this.maximumRangeValueIncLower = m + ll;
	                            this.maximumRangeValueIncLowerRow = r;
	                            this.maximumRangeValueIncLowerColumn = c;
	                        }
	                        else {
	                            if (m + ll > this.maximumRangeValueIncLower) {
	                                this.maximumRangeValueIncLower = m + ll;
	                                this.maximumRangeValueIncLowerRow = r;
	                                this.maximumRangeValueIncLowerColumn = c;
	                            }
	                        }

	                        // update the min value
	                        if (Double.isNaN(this.minimumRangeValueIncLower)) {
	                            this.minimumRangeValueIncLower = m - ll;
	                            this.minimumRangeValueIncLowerRow = r;
	                            this.minimumRangeValueIncLowerColumn = c;
	                        }
	                        else {
	                            if (m - ll < this.minimumRangeValueIncLower) {
	                                this.minimumRangeValueIncLower = m - ll;
	                                this.minimumRangeValueIncLowerRow = r;
	                                this.minimumRangeValueIncLowerColumn = c;
	                            }
	                        }
	                    }
	                }
	            }
	        }
	    }

	    /**
	     * Returns the minimum y-value in the dataset.
	     *
	     * @param includeInterval  a flag that determines whether or not the
	     *                         y-interval is taken into account.
	     *
	     * @return The minimum value.
	     *
	     * @see #getRangeUpperBound(boolean)
	     */
	    public double getRangeLowerBound(boolean includeInterval) {
	        if (includeInterval) {
	        	if (this.minimumRangeValueIncLower < this.minimumRangeValueIncUpper){
	        		return this.minimumRangeValueIncLower;
	        	}
	        	else{
	            return this.minimumRangeValueIncUpper;
	        	}
	        }
	        else {
	            return this.minimumRangeValue;
	        }
	    }

	    /**
	     * Returns the maximum y-value in the dataset.
	     *
	     * @param includeInterval  a flag that determines whether or not the
	     *                         y-interval is taken into account.
	     *
	     * @return The maximum value.
	     *
	     * @see #getRangeLowerBound(boolean)
	     */
	    public double getRangeUpperBound(boolean includeInterval) {
	        if (includeInterval) {
	        	if (this.maximumRangeValueIncLower > this.maximumRangeValueIncUpper){
	        		return this.maximumRangeValueIncLower;
	        	}
	        	else{
	        		return this.maximumRangeValueIncUpper;
	        	}
	        }
	        else {
	            return this.maximumRangeValue;
	        }
	    }

	    /**
	     * Returns the range of the values in this dataset's range.
	     *
	     * @param includeInterval  a flag that determines whether or not the
	     *                         y-interval is taken into account.
	     *
	     * @return The range.
	     */
	    public Range getRangeBounds(boolean includeInterval) {
	        Range result = null;
	        double up = 0;
	        double low = 0;
	        
	        if (includeInterval) {
	            if ((!Double.isNaN(this.minimumRangeValueIncUpper)
	                    && !Double.isNaN(this.maximumRangeValueIncUpper)) ||
	                    (!Double.isNaN(this.minimumRangeValueIncLower)
	    	                    && !Double.isNaN(this.maximumRangeValueIncLower))){
	            	up = this.maximumRangeValueIncUpper - this.minimumRangeValueIncUpper;
	            	low = this.maximumRangeValueIncLower - this.minimumRangeValueIncLower;
	                if(up > low){ 
	                	result = new Range(this.minimumRangeValueIncUpper,
	                        this.maximumRangeValueIncUpper);
	                }
	                else {
	                	result = new Range(this.minimumRangeValueIncLower,
		                        this.maximumRangeValueIncLower);
	                }
	            }    
	        }
	        else {
	            if (!Double.isNaN(this.minimumRangeValue)
	                    && !Double.isNaN(this.maximumRangeValue)) {
	                result = new Range(this.minimumRangeValue,
	                        this.maximumRangeValue);
	            }
	        }
	        return result;
	    }

	    /**
	     * Tests this instance for equality with an arbitrary object.
	     *
	     * @param obj  the object (<code>null</code> permitted).
	     *
	     * @return A boolean.
	     */
	    public boolean equals(Object obj) {
	        if (obj == this) {
	            return true;
	        }
	        if (!(obj instanceof AsymmetricStatisticalCategoryDataset)) {
	            return false;
	        }
	        AsymmetricStatisticalCategoryDataset that
	                = (AsymmetricStatisticalCategoryDataset) obj;
	        if (!this.data.equals(that.data)) {
	            return false;
	        }
	        return true;
	    }

	    /**
	     * Returns a clone of this dataset.
	     *
	     * @return A clone of this dataset.
	     *
	     * @throws CloneNotSupportedException if cloning cannot be completed.
	     */
	    public Object clone() throws CloneNotSupportedException {
	        AsymmetricStatisticalCategoryDataset clone
	                = (AsymmetricStatisticalCategoryDataset) super.clone();
	        clone.data = (KeyedObjects2D) this.data.clone();
	        return clone;
	    }

	    public Number getStdDevValue(int row, int column) {
	        throw new UnsupportedOperationException("getStdDevValue method is not supported. " +
	              "Use the getUpper and getLower methods in stead.");
	     }

	     public Number getStdDevValue(Comparable rowKey, Comparable columnKey) {
	        throw new UnsupportedOperationException("getStdDevValue method is not supported. " +
	              "Use the getUpper and getLower methods in stead.");
	     }
				
}