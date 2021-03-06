/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.synthesis.sentence;

import static org.spantus.exp.synthesis.sentence.SentenceGenerateContext.ClauseEnum.attributive;
import static org.spantus.exp.synthesis.sentence.SentenceGenerateContext.ClauseEnum.conjunction;
import static org.spantus.exp.synthesis.sentence.SentenceGenerateContext.ClauseEnum.placeFactor;
import static org.spantus.exp.synthesis.sentence.SentenceGenerateContext.ClauseEnum.predicate;
import static org.spantus.exp.synthesis.sentence.SentenceGenerateContext.ClauseEnum.subject;
import static org.spantus.exp.synthesis.sentence.SentenceGenerateContext.ClauseEnum.timeFactor;
import static org.spantus.exp.synthesis.sentence.SentenceGenerateContext.ClauseEnum.keyword;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spantus.exp.synthesis.sentence.SentenceGenerateContext.ClauseEnum;
import org.spantus.logger.Logger;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
/**
 *
 * @author as
 */
public class GenerateSentenceLtImpl {
    

	public static final Logger LOG = Logger.getLogger(GenerateSentenceLtImpl.class);
    

    
    private static Map<ClauseEnum, List<String>> clauseRepository;
    private static Map<ClauseEnum,String> formatingSequene;
    private static List<List<ClauseEnum>> suggestedClauseSequence;

    public static void main(String[] args){
        try {
            GenerateSentenceLtImpl impl = new GenerateSentenceLtImpl();
             String command = MessageFormat.format(
                        "espeak -v mb-lt1  --stdin  -s100", 20);
                LOG.error(command);

                //write stdin
                Process process = Runtime.getRuntime().exec(command);
                try (OutputStream stdin = process.getOutputStream()) {
                	Collection<String> sentenceList = impl.generateBulkUnique(10);
                    for (String sentence : sentenceList) {
                        sentence =sentence + ". ";
                        System.out.println(sentence);
                        stdin.write(sentence.getBytes(Charset.forName("UTF-8")));
                    }
                }
                process.waitFor();
        } catch (InterruptedException | IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    
    
    public Collection<String> generateBulk(int size){
//        SentenceGenerateContext ctx = createContext();
        List<String> list = Lists.newArrayListWithCapacity(size);
        for (int i = 0; i < size; i++) {
            list.add(generate());
        }
        return list;
    }
   
    public Collection<String> generateBulkUnique(int size){

    	Set<String> list = Sets.newHashSet();
      for (int i = 0; i < size*10; i++) {
          list.add(generate());
          if(list.size()>=size){
        	  break;
          }
      }
      return list;
  }

    
    public String generate(){
        SentenceGenerateContext ctx = createContext();
        String generated = generateString(ctx);
        return generated;
    }
    
    private SentenceGenerateContext createContext(){
        if(clauseRepository == null){
            load();
        }
        SentenceGenerateContext ctx = new SentenceGenerateContext();
        Double randIndex = Math.random() * suggestedClauseSequence.size();
        ctx.setClauseSequence(suggestedClauseSequence.get(randIndex.intValue()));
        return ctx;
    }

    private String generateString(SentenceGenerateContext ctx) {
        StringBuilder message = new StringBuilder();
        String separator = ""; 
        for (ClauseEnum entry : ctx.getClauseSequence()) {
            String randomClause = randomWordByClause(entry);
//            if(attributive.equals(entry)){
//                randomClause = "Lietuvos " + randomClause;
//            }
            message.append(separator).append(randomClause);
            separator = " ";
        }
        return message.toString();
    }

    private String randomWordByClause(ClauseEnum clause){
        List<String> clauseList = clauseRepository.get(clause);
        Double randIndex = Math.random() * clauseList.size();
        return clauseList.get(randIndex.intValue());
    }
    
  
    
    public synchronized void load() {
        try {
            clauseRepository = Maps.newHashMapWithExpectedSize(ClauseEnum.values().length);
            for ( ClauseEnum entry : ClauseEnum.values()) {
                String fileName = getClass().getPackage().getName().replaceAll("\\.", "/") + "/" +entry+"List.txt";
                URL entryUrl = Resources.getResource(fileName);
                clauseRepository.put(entry, Resources.readLines(entryUrl,Charsets.UTF_8));
            }
            formatingSequene = Maps.newHashMap();
            int index = 0;
            for (ClauseEnum element : ClauseEnum.values()) {
            	formatingSequene.put(element, "{"+index+"}");
            	index++;
			}
            
            suggestedClauseSequence = Lists.newArrayList();
            suggestedClauseSequence.add(Lists.newArrayList(conjunction, keyword, timeFactor, keyword, attributive, subject));
            suggestedClauseSequence.add(Lists.newArrayList(placeFactor, keyword, conjunction, keyword, attributive, predicate));
            suggestedClauseSequence.add(Lists.newArrayList(conjunction, keyword, conjunction, keyword, attributive, subject));
            suggestedClauseSequence.add(Lists.newArrayList(conjunction, keyword, timeFactor, keyword, attributive, placeFactor));
//            suggestedClauseSequence.add(Lists.newArrayList(timeFactor, attributive, subject, predicate, placeFactor));
//            suggestedClauseSequence.add(Lists.newArrayList(timeFactor,  subject, placeFactor, attributive, predicate));
//            suggestedClauseSequence.add(Lists.newArrayList(conjunction, attributive, subject, timeFactor, predicate, placeFactor));
//            suggestedClauseSequence.add(Lists.newArrayList(timeFactor, attributive, placeFactor, subject, predicate));
            

        } catch (IOException ex) {
            LOG.error(ex);
        }

    }
    
}
