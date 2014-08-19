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

    public static ResultSaver printer;

    public static void main(String[] args) throws IOException {
        LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");

        if (args.length > 0) {
            demoDP(lp, args[0]);
        } else {
            demoAPI(lp);
        }
    }

    public static void demoDP(LexicalizedParser lp, String filename) throws FileNotFoundException, IOException {
        printer = new ResultSaver();
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();

        int life = 10;
        for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
            Tree parse = lp.apply(sentence);
            extractNP(parse);
            if (life == 0) {
                break;
            }
            life--;
        }

        printer.close();
    }

    private static void extractNP(Tree t) {
        for (Tree child : t.children()) {
            if (isNoun(child)) {
                if (isLowestNoun(child) == true) {
//                    System.out.println(Sentence.listToString(child.yield()));
                    printer.print(Sentence.listToString(child.yield()));
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

    private static boolean isNoun(Tree t) {
//        return t.label().value().equals("NNS") || t.label().value().equals("NP") || t.label().value().equals("NN");
        return t.label().value().equals("NP");
    }

    private static boolean isLowestNoun(Tree t) {
        for (Tree child : t.children()) {
            if (isNoun(child)) {
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
