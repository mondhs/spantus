/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spantus.exp.synthesis.sentence;

import java.util.List;

/**
 *
 * @author as
 */
public class SentenceGenerateContext {
    
    /**
     * Clause Enum.
     *
     * The subject is usually a noun--a word that names a person, place, or thing. Lt: veiksnys
     * The predicate (or verb) usually follows the subject and identifies an action or a state of being. Lt: tarinys
     * Adjectives which precede the noun they modify are usually referred to as attributive adjectives. Lt: pažyminys 
     * timeFactor - Lt: laiko applinkybė
     * placeFactor - LT: vietos applinkybė
     * conjunction - LT: Jungtukas
     * keyword - this is word that is important for investigation
     */
    enum ClauseEnum{subject, predicate, attributive, timeFactor, placeFactor, conjunction, keyword};
        

    
    private List<ClauseEnum> clauseSequence;

    public List<ClauseEnum> getClauseSequence() {
        return clauseSequence;
    }

    public void setClauseSequence(List<ClauseEnum> clausesSequence) {
        this.clauseSequence = clausesSequence;
    }

}
