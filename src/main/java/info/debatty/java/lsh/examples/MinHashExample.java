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

import info.debatty.java.lsh.MinHash;
import java.util.TreeSet;

/**
 *
 * @author Thibault Debatty
 */
public class MinHashExample {

    public static void main(String[] args) {
        // Initialize the hash function for an similarity error of 0.1
        // For sets built from a dictionary of 5 items
        MinHash minhash = new MinHash(0.1, 5);
        
        // Sets can be defined as an vector of booleans:
        // [1 0 0 1 0]
        boolean[] vector1 = {true, false, false, true, false};
        int[] sig1 = minhash.signature(vector1);
        
        // Or as a set of integers:
        // set2 = [1 0 1 1 0]
        TreeSet<Integer> set2 = new TreeSet<Integer>();
        set2.add(0);
        set2.add(2);
        set2.add(3);
        int[] sig2 = minhash.signature(set2);
        
        System.out.println("Signature similarity: " + minhash.similarity(sig1, sig2));
        System.out.println("Real similarity (Jaccard index)" +
            MinHash.jaccardIndex(MinHash.convert2Set(vector1), set2));
    }
}
