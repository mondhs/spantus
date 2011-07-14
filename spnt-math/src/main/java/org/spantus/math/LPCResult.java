package org.spantus.math;

import java.util.List;
/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Jun 1, 2009
 *
 */
public class LPCResult {
	private List<Float> result;
	private Float error;
	private List<Float> reflection;
	public List<Float> getResult() {
		return result;
	}
	public void setResult(List<Float> result) {
		this.result = result;
	}
	public Float getError() {
		return error;
	}
	public void setError(Float error) {
		this.error = error;
	}
	public List<Float> getReflection() {
		return reflection;
	}
	public void setReflection(List<Float> reflection) {
		this.reflection = reflection;
	}
}
