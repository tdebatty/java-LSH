package info.debatty.java.lsh;

import info.debatty.java.stringsimilarity.KShingling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of Locality Sensitive Hashing (LSH) principle, as described in
 * Leskovec, Rajaraman & Ullman (2014), "Mining of Massive Datasets", 
 * Cambridge University Press.
 * 
 * @author Thibault Debatty http://www.debatty.info
 */
public class LSH {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Read strings from file
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
    
    /**
     * Reads a file and returns the content as an array of strings
     * @param path
     * @return complete content of the file (can be heavy!)
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static String[] readFile(String path) throws FileNotFoundException, IOException {
        FileReader fileReader;
        fileReader = new FileReader(path);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return  lines.toArray(new String[lines.size()]);
    }
    
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
     * Set the number of buckets per stage (or band).
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
        long[] acc = new long[s];
        
        for (int i = 0; i < acc.length; i++) {
            acc[i] = 0;
        }
        
        for (int i = 0; i < signature.length; i++) {
            long v = ((long) signature[i] * (long) (b-1)) % Integer.MAX_VALUE;
            int j = Math.min(i/s, s-1);
            acc[j] = (acc[j] + v) % Integer.MAX_VALUE;
        }
        
        int[] r = new int[s];
        for (int i = 0; i < r.length; i++) {
            r[i] = (int) (acc[i] % b);
        }
        
        return r;
    }
}
