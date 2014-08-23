/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package englishparser;

import java.io.IOException;

/**
 *
 * @author bigstone
 */
public class Test {

    public static void main(String[] args) throws IOException {
        String s = "216$%%";
        if (s.startsWith("$") || Character.isDigit(s.charAt(0)) || s.startsWith("%")) {
            System.out.println("OK");
        }
    }
}
