package csc.pkg365.assignment.pkg3.pkg1;

import java.util.List;

public class TFIDFCalculator {
    /**
     * @param doc  A list of strings (Representing our keywords for a Wikipedia
     * page)
     * @param terms An array of strings indicating the term of interest to 
     * find
     * @return The term frequency of the given term of interest in the given 
     * document
     */
    public double tf(List<String> doc, String[] terms){
        double result = 0; // Set as a double to avoid integer division
        int i = 0;
        // Keep track of how many times the term of interest appears in the
        // given document, divide that value by the number of terms in the 
        // document, and return that value.
        for(String word : doc){
            if(!terms[i].equalsIgnoreCase(word))
                i = 0;
            else if(i == terms.length - 1){
                result++;
                i = 0;
                break;
            }
            else
                i++;    
    
        }
        return result / doc.size();
    }

    /**
     * @param docs A list of a list of strings (Representing our keywords for
     * all Wikipedia pages from the starting file)
     * @param terms An array of strings indicating the term of interest to find
     * @return The inverse term frequency of the given term of interest in the
     * given documents
     */
    public double idf(List<List<String>> docs, String[] terms) {
        // Set as a double to avoid integer division or possible errors
        double n = 0; 
        
        int i = 0;
        // Take the logarithm of the number of given documents divided by
        // the number of given documents that contain the term of interest, 
        // and return that value.
        for(List<String> doc : docs){
            for(String word : doc) {
                if(!terms[i].equalsIgnoreCase(word))
                    i = 0;
                else if(i == terms.length - 1){
                    n++;
                    i = 0;
                    break;
                }
                else
                    i++;  
            }
        }
        return Math.log(docs.size() / n);
    }

    /**
     * @param doc  A text document (The keywords from a single document)
     * @param docs All documents (The keywords from all given documents)
     * @param terms The term of interest to find (Can be one word, multiple 
     * words, a webpage URL, random special symbols, who knows). 
     * @return The TF-IDF value of the term of interest
     */
    public double tfIdf(List<String> doc, List<List<String>> docs, 
        String[] terms){
        return tf(doc, terms) * idf(docs, terms);
    }

}

