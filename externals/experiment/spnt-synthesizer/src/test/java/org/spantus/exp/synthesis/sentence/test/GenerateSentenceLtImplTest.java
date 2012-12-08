/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.synthesis.sentence.test;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

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
        Set<String> messages = new HashSet<>(generator.generateBulk(10));
        //then
        //LOG.debug("Messages: {0}", messages);
        Assert.assertEquals("Size", 10, messages.size());
    }
    
    @Test
    public void testUniqueGenerate() {
        //given
        //when
        Set<String> messages = new HashSet<>(generator.generateBulkUnique(400));
        int index = 0;
        for (String string : messages) {
			System.out.println(MessageFormat.format("{0}; {1}", index, string) );
			index++;
		}
        
        //then
        //LOG.debug("Messages: {0}", messages);
        Assert.assertEquals("Size", 400, messages.size());
    }
    
    
}
