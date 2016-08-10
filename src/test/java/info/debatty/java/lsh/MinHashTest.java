package info.debatty.java.lsh;

import static org.junit.Assert.assertArrayEquals;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

/**
 *
 * @author Thibault Debatty
 */
public class MinHashTest {

    /**
     * Test with initial seed.
     */
    @Test
    public void testSeed() {
        MinHash mh = new MinHash(100, 100, 123456);
        MinHash mh2 = new MinHash(100, 100, 123456);

        Random r = new Random();

        Set<Integer> ints = new HashSet<Integer>();
        for (int i = 0; i < 50; i++) {
            ints.add(r.nextInt());
        }

        assertArrayEquals(mh.signature(ints), mh2.signature(ints));
    }
}
