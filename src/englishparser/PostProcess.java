/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package englishparser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bigstone
 */
public class PostProcess {

    public static void main(String[] args) throws IOException {
//        String filePath = "/home/bigstone/Documents/computer_NP_REFINE.txt";
//        String outPath = "/home/bigstone/Documents/computer_NP_OUT.txt";
//        String stopWords = "/home/bigstone/Documents/stopWordsList";
        
        String filePath = "/home/bigstone/tmp/allNP_less.txt";
        String outPath = "/home/bigstone/tmp/allNP_less_refined.txt";
        String stopWords = "/home/bigstone/tmp/stopWordsList";
        List<String> stopWordsList = new ArrayList<String>();

        FileWriter fw = new FileWriter(outPath);

        InputStream fis;
        BufferedReader br;
        String line;

        fis = new FileInputStream(stopWords);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        while ((line = br.readLine()) != null) {
            stopWordsList.add(line);
        }

        br.close();
        br = null;
        fis = null;

        List<String> wordsList = new ArrayList();

        fis = new FileInputStream(filePath);
        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        int count = 0;
        while ((line = br.readLine()) != null) {
            if (count++ % 1000 == 0) {
                System.out.println(count/1000);
            }
            line = line.trim();
            String[] words = line.split(" ");

            for (int i = 0; i < words.length; i++) {
                boolean stayFlag = true;

                if (words[i].startsWith(">")||words[i].startsWith("?")||words[i].startsWith("v6")||words[i].startsWith("v5") ||words[i].startsWith("v4")||words[i].startsWith("v3")||words[i].startsWith("v2")||words[i].startsWith("v1")||words[i].startsWith("a0")||words[i].startsWith("a9")||words[i].startsWith("a8")||words[i].startsWith("a7")||words[i].startsWith("\\")||words[i].contains(".")||words[i].contains("#")||words[i].startsWith("isbn")||words[i].startsWith(":")||words[i].startsWith(";")||words[i].startsWith("$")||words[i].startsWith("`")||words[i].startsWith(",")||words[i].startsWith("\'")||words[i].startsWith("h2")||words[i].startsWith("a6")||words[i].startsWith("a5")||words[i].startsWith("a3")||words[i].startsWith("a2")||words[i].startsWith("c6")||words[i].startsWith("c5")||words[i].startsWith("c4")||words[i].startsWith("c2")||words[i].startsWith("c3")||words[i].startsWith("c1")||words[i].startsWith("a1")||words[i].startsWith("~")||words[i].startsWith("+")||words[i].startsWith("<")||words[i].startsWith(".")||words[i].startsWith("-")||words[i].startsWith("&")||words[i].startsWith("$") || words[i].length() == 0 || Character.isDigit(words[i].charAt(0)) || words[i].startsWith("%")) {
                    stayFlag = false;
                    continue;
                }
                for (String stopword : stopWordsList) {
                    if (stopword.equals(words[i])) {
                        stayFlag = false;
                        break;
                    }
                }

                if (stayFlag == true) {
                    wordsList.add(words[i]);
                }
            }

            if (wordsList.size() >= 1) {
                for (String s : wordsList) {
                    fw.write(s);
                    fw.write(" ");
                }

                fw.write("\n");
            }
            
            wordsList.clear();

        }

        fw.flush();
        fw.close();
        br.close();
        br = null;
        fis = null;
    }
}
