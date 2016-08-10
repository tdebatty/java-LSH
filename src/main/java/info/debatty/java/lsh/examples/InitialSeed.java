/*
 * The MIT License
 *
 * Copyright 2016 Thibault Debatty.
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
import java.util.Random;

/**
 *
 * @author Thibault Debatty
 */
public class InitialSeed {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Initialize two minhash objects, with the same seed
        int signature_size = 20;
        int dictionary_size = 100;
        long initial_seed = 123456;

        MinHash mh = new MinHash(signature_size, dictionary_size, initial_seed);
        MinHash mh2 = new MinHash(signature_size, dictionary_size, initial_seed);

        // Create a single vector of size dictionary_size
        Random r = new Random();
        boolean[] vector = new boolean[dictionary_size];
        for (int i = 0; i < dictionary_size; i++) {
            vector[i] = r.nextBoolean();
        }

        // The two minhash objects will produce the same signature
        println(mh.signature(vector));
        println(mh2.signature(vector));
    }

    static void println(final int[] array) {
        System.out.print("[");
        for (int v : array) {
            System.out.print("" + v + " ");
        }
        System.out.println("]");
    }
}
