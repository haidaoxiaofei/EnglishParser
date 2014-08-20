/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package englishparser;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import static englishparser.EnglishParser.printer_NN;
import static englishparser.EnglishParser.printer_NNP;
import static englishparser.EnglishParser.printer_NP;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.AsyncFileWriter;
import util.Printer;

/**
 *
 * @author bigstone
 */
public class ParserTask extends Thread {

    static int count = 0;
    List<HasWord> sentence = null;
    public static LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    public static Printer printer_NP;
    public static Printer printer_NN;
    public static Printer printer_NNP;

    public static void init() {
        try {
            printer_NP = new AsyncFileWriter(new File("/home/bigstone/Documents/medicine_NP.txt"));
            printer_NN = new AsyncFileWriter(new File("/home/bigstone/Documents/medicine_NN.txt"));
            printer_NNP = new AsyncFileWriter(new File("/home/bigstone/Documents/medicine_NNP.txt"));
        } catch (IOException ex) {
            Logger.getLogger(ParserTask.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ParserTask(List<HasWord> sentence) {
        this.sentence = sentence;
    }

    public void run() {
        count++;
        if (count % 1000 == 0) {
            System.out.println(count / 1000);
        }
        Tree parse = lp.apply(sentence);
        extractNP(parse);
        extractNN(parse);
        extractNNP(parse);
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

    public static void end() {
        printer_NP.close();
        printer_NN.close();
        printer_NNP.close();
    }
}
