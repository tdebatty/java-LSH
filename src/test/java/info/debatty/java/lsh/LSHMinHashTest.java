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
package info.debatty.java.lsh;

import java.util.Random;
import org.junit.Test;

/**
 *
 * @author Thibault Debatty
 */
public class LSHMinHashTest {

  /**
   * Test of hash method, of class LSHMinHash.
   */
  @Test
  public void testHash() {
    System.out.println("hash");

    // proportion of 0's in the vectors
    // if the vectors are dense (lots of 1's), the average jaccard similarity
    // will be very high (especially for large vectors), and LSH
    // won't be able to distinguish them
    // as a result, all vectors will be binned in the same bucket...
    double sparsity = 0.75;

    // Number and size of vectors
    int count = 10000;
    int n = 100000;

    int stages = 2;
    int buckets = 10;

    // Let's generate some random sets
    boolean[][] vectors = new boolean[count][n];
    Random rand = new Random();

    for (int i = 0; i < count; i++) {
      for (int j = 0; j < n; j++) {
        vectors[i][j] = rand.nextDouble() > sparsity;
      }
    }

    LSHMinHash lsh = new LSHMinHash(stages, buckets, n);
    int[][] counts = new int[stages][buckets];

    // Perform hashing
    for (boolean[] vector : vectors) {
      int[] hash = lsh.hash(vector);

      for (int i = 0; i < hash.length; i++) {
        // this will raise an ArrayIndexOutOfBoundsException
        // if the bin values are negatives or too large
        counts[i][hash[i]]++;
      }
    }
  }
}
