package info.debatty.java.lsh;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

/**
 * 
 * @author Thibault Debatty
 */
public class SuperBitTest {

    /**
     * Test with initial seed.
     */
    @Test
    public final void testSeed() {
        int d = 50;
        SuperBit sb = new SuperBit(d, 25, 100, 123456);
        SuperBit sb2 = new SuperBit(d, 25, 100, 123456);

        Random r = new Random();

        double[] vector = new double[d];
        for (int i = 0; i < d; i++) {
            vector[i] = r.nextDouble();
        }

        boolean[] sig1 = sb.signature(vector);
        boolean[] sig2 = sb2.signature(vector);

        for (int i = 0; i < sig1.length; i++) {
            assertEquals(
                    "Signatures are different at index " + i, sig1[i], sig2[i]);
        }
    }
}
