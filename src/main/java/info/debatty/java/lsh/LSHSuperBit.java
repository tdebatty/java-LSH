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
public class LSHSuperBit extends LSH {
    private final SuperBit sb;

    public LSHSuperBit(int s, int b, int n) {
        super(s, b, n);
        
        // SuberBit code length
        int K = s * b / 2;
        int superbit; // superbit value
        for (superbit = n; superbit >= 1; superbit--) {
            if (K % superbit == 0) {
                break;
            }
        }
        
        this.sb = new SuperBit(n, superbit, K/superbit);
    }

    public int[] hash(double[] vector) {
        return hashSignature(sb.signature(vector));
    }
}
