/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package englishparser;

import java.util.Collection;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author bigstone
 */
public class EnglishParser {

    public static ResultSaver printer_NP;
    public static ResultSaver printer_NN;
    public static ResultSaver printer_NNP;

    public static void main(String[] args) throws IOException {
        LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

        if (args.length > 0) {
            demoDP(lp, args[0]);
        } else {
            demoAPI(lp);
        }
    }

    public static void demoDP(LexicalizedParser lp, String filename) throws FileNotFoundException, IOException {
        printer_NP = new ResultSaver("/home/bigstone/Documents/medicine_NP.txt");
        printer_NN = new ResultSaver("/home/bigstone/Documents/medicine_NN.txt");
        printer_NNP = new ResultSaver("/home/bigstone/Documents/medicine_NNP.txt");
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

        for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
            Tree parse = lp.apply(sentence);
            extractNP(parse);
            extractNN(parse);
            extractNNP(parse);
        }

        printer_NP.close();
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

    /**
     * demoAPI demonstrates other ways of calling the parser with already
     * tokenized text, or in some cases, raw text that needs to be tokenized as
     * a single sentence. Output is handled with a TreePrint object. Note that
     * the options used when creating the TreePrint can determine what results
     * to print out. Once again, one can capture the output by passing a
     * PrintWriter to TreePrint.printTree.
     */
    public static void demoAPI(LexicalizedParser lp) {
        // This option shows parsing a list of correctly tokenized words
        String[] sent = {"This", "is", "an", "easy", "sentence", "."};
        List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
        Tree parse = lp.apply(rawWords);
        parse.pennPrint();
        System.out.println();

        // This option shows loading and using an explicit tokenizer
        String sent2 = "This is another sentence.";
        TokenizerFactory<CoreLabel> tokenizerFactory
                = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tok
                = tokenizerFactory.getTokenizer(new StringReader(sent2));
        List<CoreLabel> rawWords2 = tok.tokenize();
        parse = lp.apply(rawWords2);

        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        System.out.println(tdl);
        System.out.println();

        // You can also use a TreePrint object to print trees and dependencies
        TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
        tp.printTree(parse);
    }

    private EnglishParser() {
    } // static methods only

}
