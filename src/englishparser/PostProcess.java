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
        String filePath = "/home/bigstone/Downloads/computer_NP.txt";
        String outPath = "/home/bigstone/Downloads/computer_NP_OUT.txt";
        String stopWords = "/home/bigstone/Documents/MATLAB/stopWordList";
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
        while ((line = br.readLine()) != null) {
            line = line.trim();
            String[] words = line.split(" ");
            
            for (int i = 0; i < words.length; i++) {
                boolean stayFlag = true;
                for (String stopword : stopWordsList) {
                    if(stopword.equals(words[i])){
                        stayFlag = false;
                        break;
                    }
                    
                    if(words[i].startsWith("$") || Character.isDigit(words[i].charAt(0)) || words[i].startsWith("%")){
                        stayFlag = false;
                        break;
                    }
                }
                
                if(stayFlag == true){
                    wordsList.add(words[i]);
                }
            }
            
            if(wordsList.size() >= 2){
                for(String s : wordsList){
                    fw.write(s);
                    fw.write(" ");
                }
                
                fw.write("\n");
            }
            
            
        }

        fw.flush();
        fw.close();
        br.close();
        br = null;
        fis = null;
    }
}
