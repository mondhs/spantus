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
	List<Float> result;
	Float error;
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
}
