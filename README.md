# java-LSH

A Java implementation of Locality Sensitive Hashing (LSH). Currently only the MinHash algorithm is implemented.

##Download

Using maven:
```
<dependency>
    <groupId>info.debatty</groupId>
    <artifactId>java-lsh</artifactId>
    <version>RELEASE</version>
</dependency>
```

See the [releases](https://github.com/tdebatty/java-LSH/releases) page.

##LSH

Locality Sensitive Hashing (LSH) is a family of hashing methods that tent to produce the same hash (or signature) for similar items. There exist different LSH functions, that each correspond to a similarity metric. For example, the MinHash algorithm is designed for Jaccard similarity (the number of elements that two sets have in common).

This project implements Locality Sensitive Hashing (LSH), as described in Leskovec, Rajaraman & Ullman (2014), "Mining of Massive Datasets", Cambridge University Press.

The example below relies on the [java-string-similarity package](https://github.com/tdebatty/java-string-similarity) to perform k-shingling of strings.

```java
import info.debatty.java.lsh.*;
import info.debatty.java.stringsimilarity.KShingling;

public class MyApp {

    public static void main(String[] args) {

        // Read all strings from file
        String[] strings = null;
        try {
            strings = readFile(args[0]);
        } catch (IOException ex) {
            Logger.getLogger(LSH.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        
        int n = strings.length;
        
        // Compute the dictionary of all shingles (4-grams)
        KShingling ks = new KShingling(4);
        for (String s : strings) {
            ks.parse(s);
        }
        System.out.println("Found " + ks.size() + " different 4-shingles...\n");
        
        // Compute boolean vector representation of each string
        boolean[][] strings_as_booleans = new boolean[n][];
        for (int i = 0; i < n; i++) {
            strings_as_booleans[i] = ks.booleanVectorOf(strings[i]);
        }
        
        // Compute minhash signatures
        MinHash mh = new MinHash(0.1, ks.size());
        int[][] minhash_signatures = new int[n][];
        for (int i = 0; i < n; i++) {
            minhash_signatures[i] = mh.hash(strings_as_booleans[i]);
        }
        
        // Perform LSH bucketting using 3 stages (bands) and 20 buckets per band
        LSH lsh = new LSH();
        lsh.setS(3);
        lsh.setB(20);
        int[][] lsh_buckets = new int[n][];
        for (int i = 0; i < n; i++) {
            lsh_buckets[i] = lsh.hash(minhash_signatures[i]);
        }
        
        // Display bucket values:
        for (int i = 0; i < n; i++) {
            System.out.print(strings[i] + "; ");
            for (int j = 0; j < lsh.s; j++) {
                System.out.print(lsh_buckets[i][j] + "; ");
            }
            System.out.print("\n");
        }
    }
}
```

```
Phentermin 37.5 mg as cheap as 120 pills $366.00 5l; 11; 2; 8; 
Phentermin 37.5 mg as cheap as 120 pills $366.00 8eg5; 11; 10; 10; 
Phentermin 37.5 mg as cheap as 120 pills $366.00 9j; 11; 2; 9; 
Phentermin 37.5 mg as cheap as 120 pills $366.00 aqye; 10; 2; 16; 
Phentermin 37.5 mg as cheap as 120 pills $366.00 efvb; 11; 9; 16; 
```

##MinHash

MinHash is a hashing scheme that tents to produce similar signatures for sets that have a high Jaccard similarity.

The Jaccard similarity between two sets is the relative number of elements these sets have in common: J(A, B) = |A ∩ B| / |A ∪ B| A MinHash signature is a sequence of numbers produced by multiple hash functions hi. It can be shown that the Jaccard similarity between two sets is also the probability that this hash result is the same for the two sets: J(A, B) = Pr[hi(A) = hi(B)]. Therefore, MinHash signatures can be used to estimate Jaccard similarity between two sets. Moreover, it can be shown that the expected estimation error is O(1 / sqrt(n)), where n is the size of the signature (the number of hash functions that are used to produce the signature).


```java
import info.debatty.java.lsh.*;


public class MyApp {

    public static void main(String[] args) {
        // Initialize the hash function for a similarity error of 0.1
        // and for sets built from a dictionary of 5 items
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

```
0.7070707070707071
2 1 3 1 3 3 1 1 1 3 0 0 0 0 0 2 2 3 ...
h0 : a=206828118 b=787613333
h1 : a=1267418977 b=991665166
h2 : a=760071140 b=1435168028
...
```