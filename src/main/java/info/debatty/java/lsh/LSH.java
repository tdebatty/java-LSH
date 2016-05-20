package info.debatty.java.lsh;

import java.io.Serializable;

/**
 * Implementation of Locality Sensitive Hashing (LSH) principle, as described in
 * Leskovec, Rajaraman & Ullman (2014), "Mining of Massive Datasets",
 * Cambridge University Press.
 *
 * @author Thibault Debatty http://www.debatty.info
 */
public abstract class LSH implements Serializable {

    protected static final long LARGE_PRIME =  433494437;
    private static final int DEFAULT_STAGES = 3;
    private static final int DEFAULT_BUCKETS = 10;

    private int stages = DEFAULT_STAGES;
    private int buckets = DEFAULT_BUCKETS;

    /**
     * Instantiates a LSH instance with s stages (or bands) and b buckets (per
     * stage), in a space with n dimensions.
     *
     * @param stages stages
     * @param buckets buckets (per stage)
     */
    public LSH(final int stages, final int buckets) {
        this.stages = stages;
        this.buckets = buckets;
    }

    /**
     * Instantiate an empty LSH instance (useful only for serialization).
     */
    public LSH() {

    }

    /**
     * Hash a signature.
     * The signature is divided in s stages (or bands). Each stage is hashed to
     * one of the b buckets.
     * @param signature
     * @return An vector of s integers (between 0 and b-1)
     */
    public final int[] hashSignature(final int[] signature) {

        // Create an accumulator for each stage
        int[] hash = new int[stages];

        // Number of rows per stage
        int rows = signature.length / stages;

        for (int i = 0; i < signature.length; i++) {
            int stage = Math.min(i / rows, stages - 1);
            hash[stage] = (int)
                    ((hash[stage] + (long) signature[i] * LARGE_PRIME)
                    % buckets);

        }

        return hash;
    }

    /**
     * Hash a signature.
     * The signature is divided in s stages (or bands). Each stage is hashed to
     * one of the b buckets.
     * @param signature
     * @return An vector of s integers (between 0 and b-1)
     */
    public final int[] hashSignature(final boolean[] signature) {
        /*int hashCode = Arrays.hashCode(signature);
        if (hashCode < 0) {
            hashCode += Integer.MAX_VALUE;
        }
        return new int[] { hashCode % b};*/

        // Create an accumulator for each stage
        long[] acc = new long[stages];
        for (int i = 0; i < stages; i++) {
            acc[i] = 0;
        }

        // Number of rows per stage
        int rows = signature.length / stages;

        for (int i = 0; i < signature.length; i++) {
            long v = 0;
            if (signature[i]) {
                v = (i + 1) * LARGE_PRIME;
            }

            // current stage
            int j = Math.min(i / rows, stages - 1);
            acc[j] = (acc[j] + v) % Integer.MAX_VALUE;
        }

        int[] r = new int[stages];
        for (int i = 0; i < stages; i++) {
            r[i] = (int) (acc[i] % buckets);
        }

        return r;
    }
}
