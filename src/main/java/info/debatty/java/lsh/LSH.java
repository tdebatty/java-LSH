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
    
    protected int s = 3;
    protected int b = 10;
    protected int n;
    
    /**
     * Instantiates a LSH instance with s stages (or bands) and b buckets (per 
     * stage), in a space with n dimensions.
     * 
     * @param s stages
     * @param b buckets (per stage)
     * @param n dimensionality
     */
    public LSH(int s, int b, int n) {
        this.s = s;
        this.b = b;
        this.n = n;
        
    }
    
    public LSH() {
        
    }
    
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
     * Hash a signature.
     * The signature is divided in s stages (or bands). Each stage is hashed to
     * one of the b buckets.
     * @param signature
     * @return An vector of s integers (between 0 and b-1)
     */
    public int[] hashSignature(int[] signature) {
                
        // Create an accumulator for each stage
        int[] r = new int[s];
        
        // Number of rows per stage
        int rows = signature.length / s;
        
        for (int i = 0; i < signature.length; i++) {
            int stage = Math.min(i / rows, s-1);
            r[stage] = (int) ((r[stage] + (long) signature[i] * LARGE_PRIME) % b);
            
        }
        
        return r;
    }
    
    /**
     * Hash a signature.
     * The signature is divided in s stages (or bands). Each stage is hashed to
     * one of the b buckets.
     * @param signature
     * @return An vector of s integers (between 0 and b-1)
     */
    public int[] hashSignature(boolean[] signature) {
        /*int hashCode = Arrays.hashCode(signature);
        if (hashCode < 0) {
            hashCode += Integer.MAX_VALUE;
        }
        return new int[] { hashCode % b};*/
        
        // Create an accumulator for each stage
        long[] acc = new long[s];
        for (int i = 0; i < s; i++) {
            acc[i] = 0;
        }
        
        // Number of rows per stage
        int rows = signature.length / s;
        
        for (int i = 0; i < signature.length; i++) {
            long v = (signature[i] ? (i+1) * LARGE_PRIME : 0);
            
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
