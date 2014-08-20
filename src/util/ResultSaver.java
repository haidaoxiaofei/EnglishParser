/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bigstone
 */
public class ResultSaver implements Printer {
//    private String filePath = "/home/bigstone/Documents/medicine_NP.txt";
    FileWriter fw = null;
    private static int count = 0;
    public ResultSaver( String filePath){
        try {
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
            if(count % 1000 == 0) {
                System.out.println(count/1000);
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
