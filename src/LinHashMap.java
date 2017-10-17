
/************************************************************************************
 * @file LinHashMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;

/************************************************************************************
 * This class provides hash maps that use the Linear Hashing algorithm.
 * A hash table is created that is an array of buckets.
 */
public class LinHashMap <K, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, Map <K, V>
{
    /** The number of slots (for key-value pairs) per bucket.
     */
    private static final int SLOTS = 4;

    /** The class for type K.
     */
    private final Class <K> classK;

    /** The class for type V.
     */
    private final Class <V> classV;

    /********************************************************************************
     * This inner class defines buckets that are stored in the hash table.
     */
    private class Bucket
    {
        int    nKeys;
        K []   key;
        V []   value;
        Bucket next;

        @SuppressWarnings("unchecked")
        Bucket (Bucket n)
        {
            nKeys = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
            next  = n;
        } // constructor
    } // Bucket inner class

    /** The list of buckets making up the hash table.
     */
    private final List <Bucket> hTable;

    /** The modulus for low resolution hashing
     */
    private int mod1;

    /** The modulus for high resolution hashing
     */
    private int mod2;

    /** Counter for the number buckets accessed (for performance testing).
     */
    private int count = 0;

    /** The index of the next bucket to split.
     */
    private int split = 0;

    /********************************************************************************
     * Construct a hash table that uses Linear Hashing.
     * @param classK    the class for keys (K)
     * @param classV    the class for keys (V)
     * @param initSize  the initial number of home buckets (a power of 2, e.g., 4)
     */
    public LinHashMap (Class <K> _classK, Class <V> _classV)    // , int initSize)
    {
        classK = _classK;
        classV = _classV;
        hTable = new ArrayList <> ();
        mod1   = 4;                        // initSize;
        mod2   = 2 * mod1;
        for(int i=0; i<mod1; i++){
            hTable.add(new Bucket(null));
} 
    } // constructor

    /********************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
        Set <Map.Entry <K, V>> enSet = new HashSet <> ();

        //  Implemented by Peter Choi
        for(int i=0; i<hTable.size();i++){//Bucket Loop
            Bucket t = hTable.get(i);
            int temp = 0;
            while(temp<t.nKeys){//put values and keys into a set
                enSet.add(new AbstractMap.SimpleEntry<>(t.key[temp],t.value[temp]));
                temp++;
            }//while
        }//for          
        return enSet;
    } // entrySet

    /********************************************************************************
     * Given the key, look up the value in the hash table.
     * @param key  the key used for look up
     * @return  the value associated with the key
     */
    public V get (Object key)
    {
        int i = h (key);

        //  Implemented by Peter Choi
        if(i<split){
            i = h2(key);
        }//if
        Bucket t = hTable.get(i);
        if(t.nKeys != 0){//if bucket isnt empty
            while(t!=null){
                count++;
                for(int z=0; z<t.nKeys; z++){
                    if(key.equals(t.key[z])){
                       return t.value[z];
                    }//if
                }//for
                t = t.next;//iterate through
            }//while

        }//if
        return null;
    } // get

    /********************************************************************************
     * Put the key-value pair in the hash table.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
        int i = h (key);
        out.println ("LinearHashMap.put: key = " + key + ", h() = " + i + ", value = " + value);

        //  Implemented by Peter Choi
        if(i<split)
            i = h2(key);
        if(key==null)
            return null;
        Bucket t = hTable.get(i);
        if(t.nKeys>=SLOTS){//if split is needed
            hTable.add(new Bucket(null));
            while(true){
                if(t.next!=null)
                    t = t.next;
                else
                    break;
            }//while
            if(t.nKeys>=SLOTS){//put key and value into new bucket
                t.next = new Bucket(null);
                t = t.next;
                t.value[t.nKeys] = value;
                t.key[t.nKeys] = key;
                t.nKeys = t.nKeys + 1;
            }//if
            else{
                t.value[t.nKeys] = value;
                t.key[t.nKeys] = key;
                t.nKeys = t.nKeys + 1;
            }//else
            Bucket b2 = new Bucket(null); 
            Bucket tempBucket = new Bucket(null);
            t = hTable.get(split);//the bucket we are splitting
            int lap = 0;
            while(lap<t.nKeys){
                if(i != split){
                    if(tempBucket.next==null){
                        tempBucket.next = new Bucket(null);
                        tempBucket = tempBucket.next;
                    }//if
                    tempBucket.key[tempBucket.nKeys] = t.key[lap];
                    tempBucket.value[tempBucket.nKeys] = t.value[lap];
                }//if
                else{
                    if(b2.next==null){
                        b2.next = new Bucket(null);
                        b2=b2.next;//maybe need fix
                    }//if
                    //new bucket
                    b2.key[b2.nKeys] = t.key[lap];
                    b2.value[b2.nKeys] = t.value[lap];
                    t.nKeys = t.nKeys + 1;
                }//else
                lap = lap + 1;
            }//while
            if(split != mod1-1)//updating split value
                split = split +1;
            else{
                split = 0;
                mod1 = mod1*2;
                mod2 = mod2*2;
            }
        }//if
        else{//if no new bucket is required
            t.value[t.nKeys] = value;
            t.key[t.nKeys] = key;
            t.nKeys = t.nKeys + 1;
        }//else
        return null;
    } // put

    /********************************************************************************
     * Return the size (SLOTS * number of home buckets) of the hash table. 
     * @return  the size of the hash table
     */
    public int size ()
    {
        return SLOTS * (mod1 + split);
    } // size

    /********************************************************************************
     * Print the hash table.
     */
    private void print ()
    {
        out.println ("Hash Table (Linear Hashing)");
        out.println ("-------------------------------------------");

        for(int i=0; i<hTable.size(); i++){
      		Bucket temp = hTable.get(i);
      		boolean chain = false;
          System.out.println("\nBucket: "+ i);
      		if( temp.next!=null ){
     			  chain = true; // chain exists if there is a next element
    		  }//if
          if(chain==false){
            for(int j=0;j<SLOTS; j++){
              out.print("");
              out.print(temp.key[j] + ":" + temp.value[j] + "\t");
              if(SLOTS!=j+1){
                if(temp.value[j]==null)
                  out.print("");
                else
                  out.print("\t");
              }//if
            }//for
          }//if
          else{
            for(int j=0; j<SLOTS; j++){
              out.print("");
              out.print(temp.key[j] + ":" + temp.value[j] + "\t");
              if(SLOTS!=j+1)
                out.print("\t");
              else{
                System.out.println("\nOverflow Bucket");
              }//else
            }//for
            for(int j=0; j<SLOTS; j++){
              out.print("");
              out.print(temp.next.key[j] + ":" + temp.next.value[j] + "\t");
              if(SLOTS!=j+1)
    					  out.print("\t");
            }//for
          }//else
        }//for
        out.println ("\n-------------------------------------------");
    } // print

    /********************************************************************************
     * Hash the key using the low resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h (Object key)
    {
        return key.hashCode () % mod1;
    } // h

    /********************************************************************************
     * Hash the key using the high resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h2 (Object key)
    {
        return key.hashCode () % mod2;
    } // h2

    /********************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {

        int totalKeys    = 30;
        boolean RANDOMLY = false;

        LinHashMap <Integer, Integer> ht = new LinHashMap <> (Integer.class, Integer.class);
        if (args.length == 1) totalKeys = Integer.valueOf (args [0]);

        if (RANDOMLY) {
            Random rng = new Random ();
            for (int i = 1; i <= totalKeys; i += 2) ht.put (rng.nextInt (2 * totalKeys), i * i);
        } else {
            for (int i = 1; i <= totalKeys; i += 2) ht.put (i, i * i);
        } // if

        ht.print ();
        for (int i = 0; i <= totalKeys; i++) {
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) totalKeys);
    } // main
    
} // LinHashMap class
