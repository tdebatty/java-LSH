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

import info.debatty.java.lsh.LSHMinHash;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 *
 * @author Thibault Debatty
 */
public class SerializeExample {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args)
            throws IOException, ClassNotFoundException {

        // Create a single random boolean vector
        int n = 100;
        double sparsity = 0.75;
        boolean[] vector = new boolean[n];
        Random rand = new Random();
        for (int j = 0; j < n; j++) {
            vector[j] = rand.nextDouble() > sparsity;
        }

        // Create and configure LSH
        int stages = 2;
        int buckets = 10;
        LSHMinHash lsh = new LSHMinHash(stages, buckets, n);
        println(lsh.hash(vector));

        // Create another LSH object
        // as the parameters of the hashing function are randomly initialized
        // these two LSH objects will produce different hashes for the same
        // input vector!
        LSHMinHash other_lsh = new LSHMinHash(stages, buckets, n);
        println(other_lsh.hash(vector));

        // Moreover, signatures produced by different LSH objects cannot
        // be used to compute estimated similarity!
        // The solution is to serialize and save the object, so it can be
        // reused later...
        File tempfile = File.createTempFile("lshobject", ".ser");
        FileOutputStream fout = new FileOutputStream(tempfile);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(lsh);
        oos.close();
        System.out.println(
                "LSH object serialized to " + tempfile.getAbsolutePath());

        FileInputStream fin = new FileInputStream(tempfile);
        ObjectInputStream ois = new ObjectInputStream(fin);
        LSHMinHash saved_lsh = (LSHMinHash) ois.readObject();
        println(saved_lsh.hash(vector));
    }

    static void println(int[] array) {
        System.out.print("[");
        for (int v : array) {
            System.out.print("" + v + " ");
        }
        System.out.println("]");
    }
}