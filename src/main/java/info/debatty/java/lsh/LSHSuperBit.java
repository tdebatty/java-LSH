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

import java.io.Serializable;

/**
 *
 * @author Thibault Debatty
 */
public class LSHSuperBit extends LSH implements Serializable {
    private SuperBit sb;

    /**
     * LSH implementation relying on SuperBit, to bin vectors s times (stages)
     * in b buckets (per stage), in a space with n dimensions. Input vectors
     * with a high cosine similarity have a high probability of falling in the
     * same bucket...
     *
     * Supported input types:
     * - double[]
     * - int[]
     * - others to come...
     *
     * @param stages stages
     * @param buckets buckets (per stage)
     * @param dimensions dimensionality
     */
    public LSHSuperBit(
            final int stages, final int buckets, final int dimensions) {

        super(stages, buckets);

        int code_length = stages * buckets / 2;
        int superbit = computeSuperBit(stages, buckets, dimensions);

        this.sb = new SuperBit(dimensions, superbit, code_length / superbit);
    }

    /**
     * LSH implementation relying on SuperBit, to bin vectors s times (stages)
     * in b buckets (per stage), in a space with n dimensions. Input vectors
     * with a high cosine similarity have a high probability of falling in the
     * same bucket...
     *
     * Supported input types:
     * - double[]
     * - int[]
     * - others to come...
     *
     * @param stages stages
     * @param buckets buckets (per stage)
     * @param dimensions dimensionality
     * @param seed random number generator seed. using the same value will
     * guarantee identical hashes across object instantiations
     *
     */
    public LSHSuperBit(
            final int stages,
            final int buckets,
            final int dimensions,
            final long seed) {

        super(stages, buckets);

        int code_length = stages * buckets / 2;
        int superbit = computeSuperBit(stages, buckets, dimensions);

        this.sb = new SuperBit(
                dimensions, superbit, code_length / superbit, seed);
    }

    /**
     * Compute the superbit value.
     * @param stages
     * @param buckets
     * @param dimensions
     * @return
     */
    private int computeSuperBit(
            final int stages,  final int buckets, final int dimensions) {

        // SuperBit code length
        int code_length = stages * buckets / 2;
        int superbit; // superbit value
        for (superbit = dimensions; superbit >= 1; superbit--) {
            if (code_length % superbit == 0) {
                break;
            }
        }

        if (superbit == 0) {
            throw new IllegalArgumentException(
                    "Superbit is 0 with parameters: s=" + stages
                            + " b=" + buckets + " n=" + dimensions);
        }

        return superbit;
    }

    /**
     * Empty constructor, used only for serialization.
     */
    public LSHSuperBit() {
    }

    /**
     * Hash (bin) a vector in s stages into b buckets.
     * @param vector
     * @return
     */
    public final int[] hash(final double[] vector) {
        return hashSignature(sb.signature(vector));
    }

    /**
     * Hash (bin) a vector in s stages into b buckets.
     * @param vector
     * @return
     */
    public final int[] hash(final int[] vector) {

        double[] d = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            d[i] = (double) vector[i];
        }
        return hash(d);
    }
}
