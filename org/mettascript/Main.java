/*
 * No copyright. No warranty. No liability accepted. Not tested.
 * Created 21/10/2014 by Zak Fenton.
 */
package org.mettascript;

import java.io.IOException;

import org.mettascript.export.java.SimpleJavaOutput;
import org.mettascript.parser.FormulaParser;
import org.mettascript.parser.Operation;
import org.mettascript.parser.Token;
import org.mettascript.runtime.*;

/**
 *
 * @author zak
 */
public class Main {
    static int fib(int x) {
        if (x < 2) {
            return x;
        } else {
            return fib(x-1) + fib(x-2);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Zak's Awesome Compiler version zero!");
        System.out.println("No fucking warranty cunt.");
        
        FormulaParser d = new FormulaParser("foo", "fib = [... < 2 & ... | fib of(...-2) + fib of(...-1)]; fib of 20", 1);
        for (Token t: d.getTokens(false)) {
            System.out.println("Token (" + t.type + "): " + t.toString());
        }
        System.out.println("Grouped tokens: " + d.getGroupedTokens());
        Operation o = d.getOperation();
        System.out.println(o.toString());
        for (Operation m: o.getSequenceMembers(true)) {
            System.out.println("Sequence Member: " + m);
            if (m.isSpecialEquals()) {
                System.out.println("  > Is special equals");
            }
        }
        
        for (String a: args) {
            System.out.println("arg: " + a);
        }
        
        System.out.println("Java Output:");
        new SimpleJavaOutput(d, System.out, "org.mettaweb.test", "MyFormula", false, false);
        
        System.out.println("Result of (previous) Java Output:");
        //System.out.println(new MyFormula()._invoke("", Value.NOTHING));
        
        System.out.println("Result of Java version of fib(20): " + fib(20));
        
    }
    
}
