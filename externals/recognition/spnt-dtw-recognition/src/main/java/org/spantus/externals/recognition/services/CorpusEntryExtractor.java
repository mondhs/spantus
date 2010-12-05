/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.externals.recognition.services;

import java.io.File;
import java.net.URL;
import java.util.List;
import org.spantus.externals.recognition.bean.CorpusEntry;

/**
 *
 * @author mondhs
 */
public interface CorpusEntryExtractor {

    /**
     * Find segments(markers), then put them to corpus
     * @param filePath
     * @return
     */
    public List<CorpusEntry> extractInMemory(File filePath);
    /**
     * Extract sements and saves in dir. This will not hold markers in memory
     * @param filePath
     * @return - dir where it will save learned samples
     */
    public int extractAndLearn(File filePath);

}
