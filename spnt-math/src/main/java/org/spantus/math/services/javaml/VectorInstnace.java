package org.spantus.math.services.javaml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.fastdtw.timeseries.TimeSeries;

public class VectorInstnace implements Instance{
	
	TimeSeries timeSeries;

	public Instance add(Instance max) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Instance add(double value) {
		throw new IllegalArgumentException("Not Implemented");

	}

	public Object classValue() {
		return value;

	}

	public Instance copy() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Instance divide(double value) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Instance divide(Instance currentRange) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public int getID() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public SortedSet<Integer> keySet() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Instance minus(Instance min) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Instance minus(double value) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Instance multiply(double value) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Instance multiply(Instance value) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public int noAttributes() {
		return getTimeSeries().size();
	}

	public void removeAttribute(int i) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public void removeAttributes(Set<Integer> indices) {
		throw new IllegalArgumentException("Not Implemented");
	}
Object value;
	public void setClassValue(Object value) {
		this.value = value;
	}

	public int size() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Instance sqrt() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public double value(int pos) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public void clear() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public boolean containsKey(Object arg0) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public boolean containsValue(Object arg0) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Set<java.util.Map.Entry<Integer, Double>> entrySet() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Double get(Object arg0) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public boolean isEmpty() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Double put(Integer arg0, Double arg1) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public void putAll(Map<? extends Integer, ? extends Double> arg0) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Double remove(Object arg0) {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Collection<Double> values() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public Iterator<Double> iterator() {
		throw new IllegalArgumentException("Not Implemented");
	}

	public TimeSeries getTimeSeries() {
		return timeSeries;
	}

	public void setTimeSeries(TimeSeries timeSeries) {
		this.timeSeries = timeSeries;
	}

}
