package org.spantus.work.ui.container;

import org.spantus.core.extractor.IExtractorInputReader;
import org.spantus.core.io.ProcessedFrameLinstener;

public interface SampleChangeListener extends ProcessedFrameLinstener {
	public void changedReader(IExtractorInputReader reader);
	public void refresh();
        public void refreshValue(Float value);

}
