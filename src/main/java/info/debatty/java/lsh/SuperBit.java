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

package info.debatty.java.lsh;

import java.util.Random;

/**
 * Implementation of Super-Bit Locality-Sensitive Hashing.
 * Super-Bit is an improvement of Random Projection LSH.
 * It computes an estimation of cosine similarity.
 * 
 * Super-Bit Locality-Sensitive Hashing
 * Jianqiu Ji, Jianmin Li, Shuicheng Yan, Bo Zhang, Qi Tian
 * http://papers.nips.cc/paper/4847-super-bit-locality-sensitive-hashing.pdf
 * Advances in Neural Information Processing Systems 25, 2012
 * 
 * @author Thibault Debatty
 */
public class SuperBit {
    
    public static void main(String[] args) {
        
        int n = 10;
        
        SuperBit sb = new SuperBit(n, n, 100/n);
        
        Random rand = new Random();
        double[] v1 = new double[n];
        double[] v2 = new double[n];
        for (int i = 0; i < n; i++) {
            v1[i] = rand.nextInt();
            v2[i] = rand.nextInt();
        }

        boolean[] sig1 = sb.signature(v1);
        boolean[] sig2 = sb.signature(v2);
        
        System.out.println("Signature (estimated) similarity: " + sb.similarity(sig1, sig2));
        System.out.println("Real (cosine) similarity: " + cosineSimilarity(v1, v2));
        
        
        double var = 0;
        int iterations = 1000;
        for (int j = 0; j < iterations; j++) {
            for (int i = 0; i < n; i++) {
                v1[i] = rand.nextInt();
                v2[i] = rand.nextInt();
            }
            
            double similarity = cosineSimilarity(v1, v2);
            double estimated_similarity = sb.similarity(sb.signature(v1), sb.signature(v2));
            var += Math.pow(similarity - estimated_similarity, 2);
        }
        
        System.out.printf(
                "Variance after %d iterations: %f\n",
                iterations,
                var / iterations);
    }
    
    private double[][] hyperplanes;
    
    /**
     * Initialize SuperBit algorithm.
     * Super-Bit depth 1 <= N <= d
     * and number of Super-Bit L >= 1
     * 
     * The resulting code length K = N * L
     * The K vectors are orthogonalized in L batches of N vectors
     * 
     * @param d data space dimension
     * @param N Super-Bit depth [1 .. d]
     * @param L number of Super-Bit [1 ..
     */
    public SuperBit(int d, int N, int L) {
        init(d, N, L);
    }
    
    /**
     * Initialize SuperBit algorithm.
     * With code length K = 10
     * The K vectors are orthogonalized in d batches of 10/d vectors
     * The resulting variance is 0.01 rad
     * @param d 
     */
    public SuperBit(int d) {
        this(d, d, 10/d);
    }
    
    private void init(int d, int N, int L) {
        if (d <= 0) {
            throw new IllegalArgumentException("Dimension d must be >= 1");
        }
        
        if (N < 1 || N > d) {
            throw new IllegalArgumentException("Super-Bit depth N must be 1 <= N <= d");
        }
        
        if (L < 1) {
            throw  new IllegalArgumentException("Number of Super-Bit L must be >= 1");
        }
        
//        Input: Data space dimension d, Super-Bit depth 1 <= N <= d, number of Super-Bit L >= 1,
//        resulting code length K = N * L
        
//        Generate a random matrix H with each element sampled independently from the normal distribution
//        N (0, 1), with each column normalized to unit length. Denote H = [v1, v2, ..., vK].
        int K = N * L;
        
        double[][] v = new double[K][d];
        Random rand = new Random();
        
        for (int i = 0; i < K; i++) {
            double[] vector = new double[d];
            for (int j = 0; j < d; j++) {
                vector[j] = rand.nextGaussian();
            }
            
            normalize(vector);
            v[i] = vector;
        }
        
        
//        for i = 0 to L - 1 do
//          for j = 1 to N do
//            w_{iN+j} = v_{iN+j}
//            for k = 1 to j - 1 do
//              w_{iN+j} = w_{iN+j} - w_{iN+k} w^T_{iN+k} v_{iN+j}
//            end for
//            wiN+j = wiN+j / | wiN+j |
//          end for
//        end for
//        Output: H˜ = [w1, w2, ..., wK]
        
        double[][] w = new double[K][d];
        for (int i = 0; i <= L-1; i++) {
            for (int j = 1; j <= N; j++) {
                java.lang.System.arraycopy(
                        v[i*N+j-1],
                        0,
                        w[i*N+j-1],
                        0,
                        d);
                
                for (int k = 1; k <= (j-1); k++) {
                    w[i*N+j-1] = sub(
                            w[i*N+j-1],
                            product(dotProduct(w[i*N+k-1], v[i*N+j-1]), w[i*N+k-1]));
                }
                
                normalize(w[i*N+j-1]);
                
            }
        }
        
        this.hyperplanes = w;
    }
    
    /**
     * 
     * @param v
     * @return 
     */
    public boolean[] signature(double[] v) {
        
        boolean[] sig = new boolean[this.hyperplanes.length];
        for (int i = 0; i < this.hyperplanes.length; i++) {
            sig[i] = (dotProduct(v, this.hyperplanes[i]) >= 0);
        }
        return sig;
    }
    
    
    public double similarity(boolean[] sig1, boolean[] sig2) {
        
        double E = 0;
        for (int i = 0; i < sig1.length; i++) {
            E += (sig1[i] == sig2[i] ? 1 : 0);
        }
        
        E = E / sig1.length;
        
        return Math.cos((1 - E) * Math.PI);
    }
    
    /**
     * Computes the cosine similarity, computed as v1 . v2 / (|v1| x |v2|)
     * Cosine similarity of two vectors is the cosine of the angle between them.
     * The cosine of 0° is 1, and it is less than 1 for any other angle. It is 
     * based on the orientation and not on the magnitude of vectors: two vectors with the 
     * same orientation have a Cosine similarity of 1, two vectors at 90° have 
     * a similarity of 0, and two vectors diametrically opposed have a 
     * similarity of -1, independent of their magnitude.
     * 
     * @param v1
     * @param v2
     * @return 
     */
    public static double cosineSimilarity(double[]v1, double[] v2) {
        
        return dotProduct(v1, v2) / (norm(v1) * norm(v2));
    }
    
    protected static void disp(double[] V) {
        for (double v : V) {
            System.out.print("" + v + " ");
        }
        System.out.print("\n");
    }
    
    protected static void disp(double[][] M) {
        for (double[] V : M) {
            for (double v : V) {
                System.out.print("" + v + "  ");
            }
            System.out.print("\n");
        }
    }
    
    protected static void disp(boolean[] hash) {
        for (boolean b : hash) {
            System.out.print(b ? "1" : "0");
        }
        System.out.println();
    }
    
    protected static double[] product(double x, double[] v) {
        double[] r = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            r[i] = x * v[i];
        }
        return r;
    }
    
    protected static double[] sub(double[] a, double[] b) {
        double[] r = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            r[i] = a[i] - b[i];
        }
        return r;
    }

    protected static void normalize(double[] vector) {
        double norm = norm(vector);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i]/ norm;
        }
        
    }
    
    
    /**
     * Returns the norm L2 : sqrt(Sum_i( v_i^2))
     * @param v
     * @return 
     */
    protected static double norm(double[] v) {
        double agg = 0;
        
        for (int i = 0; i < v.length; i++) {
            agg += (v[i] * v[i]);
        }
        
        return Math.sqrt(agg);
    }
    
    protected static double dotProduct(double[] v1, double[] v2) {
        double agg = 0;
        
        for (int i = 0; i < v1.length; i++) {
            agg += (v1[i] * v2[i]);
        }
        
        return agg;
    }
}
