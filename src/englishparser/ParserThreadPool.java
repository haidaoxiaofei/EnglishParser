/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package englishparser;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ThreadPool;

/**
 *
 * @author bigstone
 */
public class ParserThreadPool {

    public static void main(String[] args) throws IOException {
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        ParserTask.init();
        ThreadPool tPool = new ThreadPool(5, 50);
        
        String filename = "/home/bigstone/Documents/data_taxonomy/lang_tag.txt";
        
        int life = 100;
        for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
            tPool.excute(new ParserTask(sentence));
            
            if(life <= 0){
                break;
            }
            life--;
        }
        
        while(!tPool.isTaskFinished()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ParserThreadPool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        tPool.stop();
        ParserTask.end();
    }
}
