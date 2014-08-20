/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package englishparser;

import util.ResultSaver;
import java.util.List;
import java.io.StringReader;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import util.AsyncFileWriter;
import util.Printer;

/**
 *
 * @author bigstone
 */
public class EnglishParser {

    public static Printer printer_NP;
    public static Printer printer_NN;
    public static Printer printer_NNP;

    public static void main(String[] args) throws IOException {
        LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

            demoDP(lp, "/home/bigstone/Documents/data_taxonomy/lang_tag.txt");
      
    }

    public static void demoDP(LexicalizedParser lp, String filename) throws FileNotFoundException, IOException {
//        printer_NP = new ResultSaver("/home/bigstone/Documents/medicine_NP.txt");
//        printer_NN = new ResultSaver("/home/bigstone/Documents/medicine_NN.txt");
//        printer_NNP = new ResultSaver("/home/bigstone/Documents/medicine_NNP.txt");
        printer_NP = new AsyncFileWriter(new File("/home/bigstone/Documents/medicine_NP.txt"));
        printer_NN = new AsyncFileWriter(new File("/home/bigstone/Documents/medicine_NN.txt"));
        printer_NNP = new AsyncFileWriter(new File("/home/bigstone/Documents/medicine_NNP.txt"));
        
        
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

        int life = 10;
        for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
            life--;
            System.out.println(life);
            Tree parse = lp.apply(sentence);
            extractNP(parse);
            extractNN(parse);
            extractNNP(parse);
            
            if(life <= 0){
                break;
            }
        }

        printer_NP.close();
        printer_NN.close();
        printer_NNP.close();
    }

    private static void extractNN(Tree t) {
        for (Tree child : t.children()) {
            if (isNN(child)) {
                printer_NN.print(Sentence.listToString(child.yield()));
                continue;
            }
            extractNN(child);
        }
    }

    private static void extractNNP(Tree t) {
        for (Tree child : t.children()) {
            if (isNNP(child)) {
                printer_NNP.print(Sentence.listToString(child.yield()));
                continue;
            }
            extractNNP(child);
        }
    }
    
    private static void extractNP(Tree t) {
        for (Tree child : t.children()) {
            if (isNP(child)) {
                if (isLowestNoun(child) == true) {
//                    System.out.println(Sentence.listToString(child.yield()));
                    printer_NP.print(Sentence.listToString(child.yield()));
                    continue;
                }
            }
            extractNP(child);
        }
    }

    private static void display(Tree t) {
        t.pennPrint();
        for (Tree child : t.children()) {
            if (!child.isLeaf()) {
                display(child);
            }
        }
    }

    private static boolean isNP(Tree t) {
//        return t.label().value().equals("NNS") || t.label().value().equals("NP") || t.label().value().equals("NN");
        return t.label().value().equals("NP");
    }

    private static boolean isNN(Tree t) {
//        return t.label().value().equals("NNS") || t.label().value().equals("NP") || t.label().value().equals("NN");
        return t.label().value().equals("NN") || t.label().value().equals("NNS");
    }

    private static boolean isNNP(Tree t) {
//        return t.label().value().equals("NNS") || t.label().value().equals("NP") || t.label().value().equals("NN");
        return t.label().value().equals("NNP");
    }

    private static boolean isLowestNoun(Tree t) {
        for (Tree child : t.children()) {
            if (isNP(child)) {
                return false;
            }
            if (isLowestNoun(child) == false) {
                return false;
            }
        }
        return true;
    }


    private EnglishParser() {
    } // static methods only

}
