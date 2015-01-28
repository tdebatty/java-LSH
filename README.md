# java-LSH

A Java implementation of Locality Sensitive Hashing (LSH)


Download
========

https://github.com/tdebatty/java-LSH/releases

MinHash
=====
MinHash is a hashing scheme that tents to produce similar signatures for sets that have a high Jaccard similarity.

The Jaccard similarity between two sets is the relative number of elements these sets have in common: J(A, B) = |A ∩ B| / |A ∪ B| A MinHash signature is a sequence of numbers produced by multiple hash functions hi. It can be shown that the Jaccard similarity between two sets is also the probability that this hash result is the same for the two sets: J(A, B) = Pr[hi(A) = hi(B)]. Therefore, MinHash signatures can be used to estimate Jaccard similarity between two sets. Moreover, it can be shown that the expected estimation error is O(1 / sqrt(n)), where n is the size of the signature (the number of hash functions that are used to produce the signature).


```
import info.debatty.java.lsh.*;


public class MyApp {

    public static void main(String[] args) {
        // Initialize the hash function for an similarity error of 0.1
        // For sets built from a dictionary of 5 items
        MinHash minhash = new MinHash(0.1, 5);
        
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
        
        System.out.println(minhash.similarity(sig1, sig2));
        
        minhash.printCoefficients();
    }
}
```