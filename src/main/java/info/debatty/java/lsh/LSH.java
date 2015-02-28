package info.debatty.java.lsh;

/**
 * Implementation of Locality Sensitive Hashing (LSH) principle, as described in
 * Leskovec, Rajaraman & Ullman (2014), "Mining of Massive Datasets", 
 * Cambridge University Press.
 * 
 * @author Thibault Debatty http://www.debatty.info
 */
public class LSH {
    
    protected int s = 3;
    protected int b = 10;
    
    /**
     * 
     * @return the number of stages (bands)
     */
    public int getS() {
        return s;
    }

    /**
     * Set the number of stages (also sometimes called bands).
     * Default value is 3
     * @param s 
     */
    public void setS(int s) {
        this.s = s;
    }

    /**
     * 
     * @return the number of buckets (per stage)
     */
    public int getB() {
        return b;
    }

    /**
     * Set the number of buckets per stage.
     * Default value is 10.
     * @param b 
     */
    public void setB(int b) {
        this.b = b;
    }
    
    /**
     * Hash a signature (array of integers).
     * The signature is divided in s stages (or bands). Each stage is hashed to
     * one of the b buckets.
     * @param signature
     * @return An array of s integers (between 0 and b-1)
     */
    public int[] hash(int[] signature) {
        
        // Create an accumulator for each stage
        long[] acc = new long[s];
        for (int i = 0; i < s; i++) {
            acc[i] = 0;
        }
        
        // Number of rows per stage
        int rows = signature.length / s;
        
        for (int i = 0; i < signature.length; i++) {
            long v = ((long) signature[i] * (long) (b-1)) % Integer.MAX_VALUE;
            
            // current stage
            int j = Math.min(i / rows, s-1);
            acc[j] = (acc[j] + v) % Integer.MAX_VALUE;
        }
        
        int[] r = new int[s];
        for (int i = 0; i < s; i++) {
            r[i] = (int) (acc[i] % b);
        }
        
        return r;
    }
}
