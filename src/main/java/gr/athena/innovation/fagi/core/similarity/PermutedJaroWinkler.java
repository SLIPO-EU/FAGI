package gr.athena.innovation.fagi.core.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.lang.reflect.Array;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * A more complex version of the sorted Jaro-Winkler approach. 
 * Comparisons are performed over all possible permutations of words,
 * and the maximum of all calculated similarity values is
 * returned.
 * 
 * @author nkarag
 */
public class PermutedJaroWinkler {
    /**
     * Computes the permuted version of Jaro-Winkler. 
     * 
     * @param a the first string.
     * @param b the second string.
     * @return the result score.
     */
    public double computeSimilarity(String a, String b){

        //TODO: this is not optimized at all
        
        String[] wordsA = tokenize(a);
        String[] wordsB = tokenize(b);
        
        Permutations<String> permA = new Permutations<>(wordsA);
        Permutations<String> permB = new Permutations<>(wordsB);
        
        Set<String> permSetA = new HashSet();
        Set<String> permSetB = new HashSet();

        int count = 0;
        while(permA.hasNext()){
            String[] permArrayA = permA.next();
            String permStringA = String.join(" ", permArrayA);
            permSetA.add(permStringA);
            count++;
        }
        
        count = 0;
        while(permB.hasNext()){
            String[] permArrayB = permB.next();
            String permStringB = String.join(" ", permArrayB);
            permSetB.add(permStringB);
            count++;
        }        
        
        double maxSimilarity = 0;
        for (String stringA : permSetA ) {
            for (String stringB : permSetB) {
                double tempSimilarity = JaroWinkler.compute(stringA, stringB);
                if(tempSimilarity > maxSimilarity){
                    maxSimilarity = tempSimilarity;
                }
            }
        }

        return maxSimilarity;
    }
    
    /**
     * Returns an array of tokens. Utilizes regex to find words. It applies a regex
     * {@code}(\w)+{@code} over the input text to extract words from a given character
     * sequence. Implementation taken from org.apache.commons.text.similarity
     *
     * @param text input text
     * @return array of tokens
     */
    private static String[] tokenize(final CharSequence text) {
        Validate.isTrue(StringUtils.isNotBlank(text), "Invalid text");
        final Pattern pattern = Pattern.compile("(\\w)+");
        final Matcher matcher = pattern.matcher(text.toString());
        final List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return tokens.toArray(new String[0]);
    }
    
    /**
     * Helper class used to calculate the permutations of the string arrays.
     * 
     * Modified from this solution: https://stackoverflow.com/questions/2920315/permutation-of-array
     * To get permutations of an integer array, we start with an array sorted in ascending order. 
     * The goal is to make it descending. 
     * To generate next permutation we are trying to find the first index from the bottom 
     * where sequence fails to be descending, and improves value in that index 
     * while switching order of the rest of the tail from descending to ascending in this case.
     * 
     * Constructor accepts an array of objects, and maps them into an array of integers using HashMap. 
     * 
     */    
    private class Permutations<E> implements  Iterator<E[]>{

        private E[] array;
        private int[] index;
        private boolean hasNext;

        public E[] output;//next() returns this array, make it public

        Permutations(E[] array){
            this.array = array.clone();
            index = new int[array.length];
            //convert an array of any elements into array of integers - first occurrence is used to enumerate
            Map<E, Integer> hashMap = new HashMap<>();
            for(int i = 0; i < array.length; i++){
                Integer n = hashMap.get(array[i]);
                if (n == null){
                    hashMap.put(array[i], i);
                    n = i;
                }
                index[i] = n;
            }
            Arrays.sort(index);//start with ascending sequence of integers

            //output = new E[array.length]; <-- cannot do in Java with generics, so use reflection
            output = (E[]) Array.newInstance(array.getClass().getComponentType(), array.length);
            hasNext = true;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        /**
         * Computes next permutations. Same array instance is returned every time!
         * @return
         */
        @Override
        public E[] next() {
            if (!hasNext)
                throw new NoSuchElementException();

            for(int i = 0; i < index.length; i++){
                output[i] = array[index[i]];
            }

            //get next permutation
            hasNext = false;
            for(int tail = index.length - 1;tail > 0;tail--){
                if (index[tail - 1] < index[tail]){//still increasing

                    //find last element which does not exceed index[tail-1]
                    int s = index.length - 1;
                    while(index[tail-1] >= index[s]){
                        s--;
                    }
                    swap(index, tail-1, s);

                    //reverse order of elements in the tail
                    for(int i = tail, j = index.length - 1; i < j; i++, j--){
                        swap(index, i, j);
                    }
                    hasNext = true;
                    break;
                }
            }
            return output;
        }

        private void swap(int[] array, int i, int j){
            int t = array[i];
            array[i] = array[j];
            array[j] = t;
        }

        @Override
        public void remove() {
            //necessary override, not used.
        }
    }
}
