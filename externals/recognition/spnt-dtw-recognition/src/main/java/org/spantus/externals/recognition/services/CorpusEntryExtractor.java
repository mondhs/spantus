/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.spantus.externals.recognition.services;

import java.io.File;
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
    List<CorpusEntry> extractInMemory(File filePath);

}
