/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.extr.wordspot.service.impl.test;

import java.io.File;

/**
 *
 * @author as
 */
public class WordSpottingServiceImplExp extends WordSpottingServiceImplTest {

    @Override
    protected void initPaths() {
        setWavFile(new File("/home/as/tmp/garsynas.lietuvos-mg/TRAIN/", "1.wav"));
        setRepositoryPathRoot(new File("/home/as/tmp/garsynas.lietuvos-mg/CORPUS/"));
        setSearchWord("Lietuvos");
    }
}
