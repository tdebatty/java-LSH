/*
 * The MIT License
 *
 * Copyright 2015 Thibault Debatty.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package info.debatty.java.lsh.examples;

import info.debatty.java.lsh.LSHSuperBit;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thibault Debatty
 */
public class LSHSuperBitExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
            int count = 100;
            
            // R^n
            int n = 3;
            
            int stages = 2;
            int buckets = 4;
            
            // Produce some vectors in R^n
            Random r = new Random();
            double[][] vectors = new double[count][];
            for (int i = 0; i < count; i++) {
                vectors[i] = new double[n];
                
                for (int j = 0; j < n; j++) {
                    vectors[i][j] = r.nextGaussian();
                }
            }
        try {
            LSHSuperBit lsh = new LSHSuperBit(stages, buckets, n);
            
            // Compute a SuperBit signature, and a LSH hash
            for (int i = 0; i < count; i++) {
                double[] vector = vectors[i];
                int[] hash = lsh.hash(vector);
                for (double v : vector) {
                    System.out.printf("%6.2f\t", v);
                }
                System.out.print(hash[0]);
                System.out.print("\n");
            }
        } catch (Exception ex) {
            Logger.getLogger(LSHSuperBitExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
