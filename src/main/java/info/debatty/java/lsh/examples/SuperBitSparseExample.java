package info.debatty.java.lsh.examples;

/*
 * The MIT License
 *
 * Copyright 2015 tibo.
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


import info.debatty.java.lsh.SuperBit;
import info.debatty.java.utils.SparseIntegerVector;
import java.util.Random;

/**
 *
 * @author Thibault Debatty
 */
public class SuperBitSparseExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int n = 1000;
        
        // Initialize SuperBit algorithm for n dimensions
        SuperBit sb = new SuperBit(n);
        
        
        // Create some sparse vectors
        Random rand = new Random();
        
        int[] v = new int[n];
        for (int i = 0; i < n/10; i++) {
            v[rand.nextInt(n)] = rand.nextInt(100);
        }
        SparseIntegerVector v1 = new SparseIntegerVector(v);
        
        v = new int[n];
        for (int i = 0; i < n/10; i++) {
            v[rand.nextInt(n)] = rand.nextInt(100);
        }
        SparseIntegerVector v2 = new SparseIntegerVector(v);

        boolean[] sig1 = sb.signature(v1);
        boolean[] sig2 = sb.signature(v2);
        
        System.out.println("Signature (estimated) similarity: " + 
                sb.similarity(sig1, sig2));
        System.out.println("Real cosine similarity: " + v1.dotProduct(v2) / (v1.norm() * v2.norm()));
        
    }
    
}
