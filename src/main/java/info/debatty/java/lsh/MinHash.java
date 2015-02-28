package info.debatty.java.lsh;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;


/**
 * MinHash is a hashing scheme that tents to produce similar signatures for sets 
 * that have a high Jaccard similarity.
 * 
 * The Jaccard similarity between two sets is the relative number of elements 
 * these sets have in common:  J(A, B) = |A ∩ B| / |A ∪ B|
 * A MinHash signature is a sequence of numbers produced by multiple hash 
 * functions hi. It can be shown that the Jaccard similarity between two sets is 
 * also the probability that this hash result is the same for the two sets: 
 * J(A, B) = Pr[hi(A) = hi(B)]. Therefore, MinHash signatures can be used to 
 * estimate Jaccard similarity between two sets. Moreover, it can be shown that 
 * the expected estimation error is O(1 / sqrt(n)), where n is the size of the 
 * signature (the number of hash functions that are used to produce the 
 * signature).
 * 
 * @author Thibault Debatty http://www.debatty.info
 */
public class MinHash {

    public static void main(String[] args) {
        // Initialize the hash function for an similarity error of 0.1
        // For sets built from a dictionary of 5 items
        MinHash minhash = new MinHash(0.1, 5);
        
        //minhash.printCoefficients();
        
        // Sets can be defined as an array of booleans:
        // [1 0 0 1 0]
        boolean[] set1 = new boolean[5];
        set1[0] = true;
        set1[1] = false;
        set1[2] = false;
        set1[3] = true;
        set1[4] = false;
        int[] sig1 = minhash.hash(set1);
        minhash.printSignature(sig1);
        
        // Or as a set of integers:
        // set2 = [1 0 1 1 0]
        TreeSet<Integer> set2 = new TreeSet<Integer>();
        set2.add(0);
        set2.add(2);
        set2.add(3);
        int[] sig2 = minhash.hash(set2);
        
        System.out.println("Signature similarity: " + minhash.similarity(sig1, sig2));
        System.out.println("Real similarity (Jaccard index)" +
            JaccardIndex(Convert2Set(set1), set2));
        
        
    }
    
    public static double JaccardIndex(Set<Integer> s1, Set<Integer> s2) {
        Set<Integer>  intersection = new HashSet<Integer>(s1);
        intersection.retainAll(s2);
        
        Set<Integer>  union = new HashSet<Integer>(s1);
        union.addAll(s2);
        
        return (double) intersection.size() / union.size();
    }
    
    public static Set<Integer> Convert2Set(boolean[] array) {
        Set<Integer> set = new TreeSet<Integer>();
        for (int i = 0; i < array.length; i++) {
            if (array[i]) {
                set.add(i);
            }
        }
        return set;
    }
    
    /**
     * Computes the size of the signature required to achieve a given error
     * in similarity estimation (1 / error^2)
     * @param error
     * @return size of the signature
     */
    public static int size(double error) {
        if (error < 0 && error > 1) {
            throw  new IllegalArgumentException("error should be in [0 .. 1]");
        }
        return (int) (1 / (error * error));
    }
    

    /**
     * Signature size
     */
    private int n;
    
    /**
     * Random a and b coefficients for the random hash functions
     */
    private int[][] hash_coefs;
    
    /**
     * Dictionary size
     */
    private int dict_size;
    
    /**
     * Initializes hash functions to compute MinHash signatures for sets built
     * from a dictionary of dict_size elements
     * 
     * @param size the number of hash functions (and the size of resulting signatures)
     * @param dict_size 
     */
    public MinHash (int size, int dict_size) {
        init(size, dict_size);
    }
    
    /**
     * Initializes hash function to compute MinHash signatures for sets built
     * from a dictionary of dict_size elements, with a given similarity
     * estimation error
     * @param error
     * @param dict_size
     */
    public MinHash (double error, int dict_size) {
        init(size(error), dict_size);
    }
    
    private void init(int size, int dict_size) {
        this.dict_size = dict_size;
        n = size;
        // h = (a * x) + b
        // a and b should be randomly generated
        Random r = new Random();
        hash_coefs = new int[n][2];
        for (int i = 0; i < n; i++) {
            hash_coefs[i][0] = r.nextInt(Integer.MAX_VALUE); // a
            hash_coefs[i][1] = r.nextInt(Integer.MAX_VALUE); // b
        }
    }
    
    
    /**
     * Computes hi(x) as (a_i * x + b_i) % dict_size.
     * Computations are executed using long, then returned as an int
     * 
     * @param i
     * @param x
     * @return the hashed value of x, using ith hash function
     */
    private int h(int i, int x) {
        return (int) ((((long)hash_coefs[i][0]) * x +
                ((long)hash_coefs[i][1])) % dict_size);
    }
    
    /**
     * Computes the signature for this set
     * For example set = {0, 2, 3} 
     * @param set
     * @return the signature
     */
    public int[] hash(Set<Integer> set) {
        int[] sig = new int[n];
        
        for (int i = 0; i < n; i++) {
            sig[i] = Integer.MAX_VALUE;
        }
        
        for (int r = 0; r < dict_size; r++) {
            if (set.contains(r)) {
                for (int i = 0; i < n; i++) {
                    sig[i] = Math.min(
                            sig[i],
                            h(i, r));
                }
            }
        }
        
        return sig;
    }
    
    /**
     * Computes the signature for this set
     * The input set is represented as an array of booleans
     * For example the array [true, false, true, true, false]
     * corresponds to the set {0, 2, 3}
     * 
     * @param array
     * @return the signature
     */
    public int[] hash(boolean[] array) {
        if (array.length != dict_size) {
            throw new IllegalArgumentException("Size of array should be dict_size");
        }
        
        Set<Integer> set = Convert2Set(array);
       
        return hash(set);
    }

    /**
     * Print the coefficients used for hashing functions
     */
    public void printCoefficients() {
        for (int i = 0; i < n; i++) {
            System.out.println("h" + i + " : a=" + hash_coefs[i][0]
                + " b=" + hash_coefs[i][1]);
        }
    }
    
    /**
     * Computes an estimation of Jaccard similarity (the number of elements in
     * common) between two sets, using the MinHash signatures of these two sets
     * @param sig1 MinHash signature of set1
     * @param sig2 MinHash signature of set2 (produced using the same coefficients)
     * @return the estimated similarity
     */
    public double similarity(int[] sig1, int[] sig2) {
        if (sig1.length != sig2.length) {
            throw new IllegalArgumentException("Size of signatures should be the same");
        }
        
        double sim = 0;
        for (int i = 0; i < sig1.length; i++) {
            if (sig1[i] == sig2[i]) {
                sim += 1;
            }
        }
        
        return sim / sig1.length;
    }
    
    /**
     * Computes the expected error of similarity computed using signatures
     * @return the expected error
     */
    public double error() {
        return 1.0 / Math.sqrt(n);
    }
    
    public void printSignature(int[] s) {
        for (int i : s) {
            System.out.print(i + " ");
        }
        System.out.print("\n");
    }
    
}
