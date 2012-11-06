/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.synthesis.sentence.test;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.spantus.exp.synthesis.sentence.GenerateSentenceLtImpl;
import org.spantus.logger.Logger;

/**
 *
 * @author as
 */
public class GenerateSentenceLtImplTest {

    public static final Logger LOG = Logger.getLogger(GenerateSentenceLtImplTest.class);
    private GenerateSentenceLtImpl generator;

    @Before
    public void onSetup() {
        generator = new GenerateSentenceLtImpl();
    }

    @Test
    public void testGenerate() {
        //given
        //when
        List<String> messages = generator.generateBulk(10);
        //then
        LOG.debug("Messages: {0}", messages);
        Assert.assertEquals("Size", 10, messages.size());
    }
}
