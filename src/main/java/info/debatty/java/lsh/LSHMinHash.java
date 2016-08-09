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

/**
 *
 * @author Thibault Debatty
 */
public class LSHMinHash extends LSH {
    private final MinHash mh;
    private static final double THRESHOLD = 0.5;

    /**
     * Instantiates a LSH instance that internally uses MinHash,
     * with s stages (or bands) and b buckets (per stage), for sets out of a
     * dictionary of n elements.
     *
     * Attention: the number of buckets should be chosen such that we have at
     * least 100 items per bucket.
     *
     * @param s stages
     * @param b buckets (per stage)
     * @param n dictionary size
     */
    public LSHMinHash(final int s, final int b, final int n) {
        super(s, b);
        this.mh = buildMinHash(s, n, null);
    }
    
    /**
     * Instantiates a LSH instance that internally uses MinHash,
     * with s stages (or bands) and b buckets (per stage), for sets out of a
     * dictionary of n elements.
     *
     * Attention: the number of buckets should be chosen such that we have at
     * least 100 items per bucket.
     *
     * @param s stages
     * @param b buckets (per stage)
     * @param n dictionary size
     * @param seed random number generator seed. using the same value will 
     * guarantee identical hashes across object instantiations
     */
    public LSHMinHash(final int s, final int b, final int n, final long seed) {
        super(s, b);
        this.mh = buildMinHash(s, n, seed);
    }
    
    private MinHash buildMinHash(final int s, final int n, Long seed) {
        /**
         * "Mining of Massive Datasets", p.88.
         * It can be shown that, using MinHash, the probability that the
         * signatures of 2 sets with Jaccard similarity s agree in all the
         * rows of at least one stage (band), and therefore become a candidate
         * pair, is 1−(1−s^R)^b
         * where R = signature_size / b (number of rows in a stage/band)
         * Thus, the curve that shows the probability that 2 items fall in the
         * same bucket for at least one of the stages, as a function of their
         * Jaccard index similarity, has a S shape.
         * The threshold (the value of similarity at which the probability of
         * becoming a candidate is 1/2) is a function of the number of stages
         * (s, or bands b in the book) and the signature size:
         * threshold ≃ (1/s)^(1/R)
         * Hence the signature size can be computed as:
         * R = ln(1/s) / ln(threshold)
         * signature_size = R * b
         */
        int r = (int) Math.ceil(Math.log(1.0 / s) / Math.log(THRESHOLD)) + 1;
        int signature_size = r * s;
        return seed != null ? new MinHash(signature_size, n, seed) : new MinHash(signature_size, n);
    }

    /**
     * Bin this vector to corresponding buckets.
     * @param vector
     * @return
     */
    public final int[] hash(final boolean[] vector) {
        return hashSignature(this.mh.signature(vector));
    }

    /**
     * Get the coefficients used by internal hashing functions.
     * @return
     */
    public final long[][] getCoefficients() {
        return mh.getCoefficients();
    }
}
