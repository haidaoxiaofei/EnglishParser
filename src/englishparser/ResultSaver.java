/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package englishparser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bigstone
 */
public class ResultSaver implements printer {
    private String filePath = "/home/bigstone/Documents/data_taxonomy/medicine_NP.txt";
    FileWriter fw = null;
    private static int count = 0;
    public ResultSaver(){
        try {
//            File f = new File(filePath);
//            f.createNewFile();
            fw = new FileWriter(filePath);
        } catch (IOException ex) {
            Logger.getLogger(ResultSaver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void print(String content) {
        count++;
        try {
            fw.write(content);
            fw.write("\n");
            if(count % 100 == 0) {
                fw.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(ResultSaver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close(){
        try {
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(ResultSaver.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
    

}
