package org.spantus.exp.recognition.dao.chart;

import java.io.Serializable;

import org.jfree.util.ObjectUtilities;

public class MeanWithAsymmetricErrorBar implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 7413468697315721515L;
	
    /** The mean. */
    private Number mean;

    /** The upper value. */
    private Number upperValue;
    
    /** The upper value. */
    private Number lowerValue;
    
    public MeanWithAsymmetricErrorBar(double mean, double upperValue, double lowerValue) {
        this(new Double(mean), new Double(upperValue), new Double(lowerValue));
    }
    
    /**
     * Creates a new mean, upper and lower bound record.
     */
    public MeanWithAsymmetricErrorBar(Number mean, Number upperValue, Number lowerValue) {
        this.mean = mean;
        this.upperValue = upperValue;
        this.lowerValue = lowerValue;
    }
    
    /**
     * Returns the mean.
     *
     * @return The mean.
     */
    public Number getMean() {
        return this.mean;
    }
    
    /**
     * Returns the mean as a double primitive.  If the underlying mean is
     * <code>null</code>, this method will return <code>Double.NaN</code>.
     *
     * @return The mean.
     *
     * @see #getMean()
     *
     * @since 1.0.7
     */
    public double getMeanValue() {
        double result = Double.NaN;
        if (this.mean != null) {
            result = this.mean.doubleValue();
        }
        return result;
    }
    
    /**
     * Returns the upper value.
     *
     * @return The upper value of CI.
     */
    public Number getUpper() {
        return this.upperValue;
    }
    
    /**
     * Returns the upper value as a double primitive.  If the underlying
     * upper value is <code>null</code>, this method will return
     * <code>Double.NaN</code>.
     *
     * @return The upper value of CI.
     */
    public double getUpperValue() {
        double result = Double.NaN;
        if (this.upperValue != null) {
            result = this.upperValue.doubleValue();
        }
        return result;
    }
	
    /**
     * Returns the lower value.
     *
     * @return The lower value of CI.
     */
    public Number getLower() {
        return this.lowerValue;
    }
    
    /**
     * Returns the lower value as a double primitive.  If the underlying
     * lower value is <code>null</code>, this method will return
     * <code>Double.NaN</code>.
     *
     * @return The lower value of CI.
     */
    public double getLowerValue() {
        double result = Double.NaN;
        if (this.lowerValue != null) {
            result = this.lowerValue.doubleValue();
        }
        return result;
    }
    /**
     * Tests this instance for equality with an arbitrary object.
     * @param obj  the object (<code>null</code> permitted).
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MeanWithAsymmetricErrorBar)) {
            return false;
        }
        MeanWithAsymmetricErrorBar that = (MeanWithAsymmetricErrorBar) obj;
        if (!ObjectUtilities.equal(this.mean, that.mean)) {
            return false;
        }
        if (!ObjectUtilities.equal(
            this.upperValue, that.upperValue)
        ) {
            return false;
        }
        if (!ObjectUtilities.equal(
                this.lowerValue, that.lowerValue)
            ) {
                return false;
        }
        return true;
    }

    /**
     * Returns a string representing this instance.
     *
     * @return A string.
     *
     * @since 1.0.7
     */
    public String toString() {
        return "[" + this.mean + ", " + this.upperValue + ", " + this.lowerValue + "]";
    }
	
}
