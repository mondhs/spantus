package org.spantus.core.extractor.preemphasis;

import org.spantus.core.extractor.ProcessingFilter;


/**
 * 
 * @author Mindaugas Greibus
 * 
 * @since 0.0.1
 * Created Jun 3, 2009
 *
 */
public interface Preemphasis extends ProcessingFilter{
	public enum PreemphasisEnum {high, middle, full};
}
