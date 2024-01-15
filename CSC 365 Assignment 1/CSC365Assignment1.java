package csc.pkg365.assignment.pkg1;

import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;

import java.util.*;
import static java.util.Map.Entry.comparingByValue;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CSC365Assignment1 {

    public static void main(String[] args) throws IOException {
        
        // Get URLs from File and Text Extractions from Jsoup
        
        // This ArrayList will contain the URLs of each Wikipedia webpage 
        List<String> docs = new ArrayList<String>();
        
        // This ArrayList will contain the keywords of each Wikipedia webpage
        List<List<String>> keyWordsFromAllDocs = new ArrayList<List<String>>();
        
        // This ArrayList with contain all of the links from each Wikipedia webpage
        List<List<String>> linksFromAllDocs = new ArrayList<List<String>>();
        
        String page;
        int numDocuments = 0;
        
        File wikipediaFile = new File("wikipediaPages.txt");
        Scanner wikipediaFileSC = new Scanner(wikipediaFile);
        while(wikipediaFileSC.hasNext()){
            
            // Get the current Wikipedia webpage
            page = wikipediaFileSC.nextLine(); // from file
            
            // Add the current Wikipedia webpage to the docs ArrayList
            docs.add(page);
            
            // Get the text extractions from Jsoup with respect to the current 
            // Wikipedia webpage
            Document doc = Jsoup.connect(docs.get(numDocuments)).get();
            
            // Most (hopefully all) Wikipedia webpages will have a body and a
            // categories section. The .split(" ") helps with allocating only 
            // one word per index. However, the word may be combined with a 
            // number or letter if it is next to one or more citation blocks, 
            // like [1] or [a]. URLs will also look weird as well.
            
            String [] keyWords = doc.select("div.mw-body-content\n").text(
                ).split(" "); 
            String [] categories = doc.select("div.mw-normal-catlinks\n").text(
                ).split(" ");
            
            // Add all keywords and categories from each Wikipedia webpage
            // to the keyWordsFromAllDocs ArrayList.
            ArrayList <String> keyWordsFromCurrentDoc = new ArrayList<String>();
            
            // Removes some annoying special characters, but keeps all
            // alphanumeric characters and some important special characters, 
            // including  #, $, %, !, ?, +, _, and -
            for(String extraction : keyWords)
                keyWordsFromCurrentDoc.add(extraction.replaceAll("[^a-zA-Z0-9#"
                    + "$%!?+_-]", ""));          
            for(String moreExtraction : categories)
                keyWordsFromCurrentDoc.add(moreExtraction.replaceAll("[^a-zA-Z0"
                    + "-9#$%!?+_-]", ""));
            keyWordsFromAllDocs.add(keyWordsFromCurrentDoc);
            
            // Get the relevant links on the current Wikipedia page
            Elements linksOnPage = doc.select("#bodyContent a[href^=\"/wiki/\"]");
            
            // Add all links from each Wikipedia webpage to the links ArrayList.
            List<String> links = new ArrayList<String>();
            
            for(Element currentLink : linksOnPage){
                links.add(currentLink.attr("href"));
            }
            linksFromAllDocs.add(links);
            
            // Go to the next Wikipedia webpage.
            numDocuments++;

        }
        wikipediaFileSC.close();

        // Get the topic or webpage from the user.
        String topicOrWebPage = JOptionPane.showInputDialog("Enter your "
            + "favorite topic or webpage");
       
        // Split the input into multiple strings based on how many words are 
        // in it.
        String[] wordsToFind = topicOrWebPage.split(" ");
        
        // Calculate the tf-idf value for each document with respect to the
        // topicOrWebPage variable. Store them with some data structure.
        Map<Integer, Double> unsortedTfidfMap = new HashMap<Integer, Double>();
        TFIDFCalculator calculator = new TFIDFCalculator();      
        for(int i = 0; i < numDocuments; i++){
            
            // Passing in the keywords from the current Wikipedia page we are 
            // on, the keywords from all Wikipedia pages from the starting file,
            // and the array of strings we are interested in finding.
            double tfidf = calculator.tfIdf(keyWordsFromAllDocs.get(i), 
                keyWordsFromAllDocs, wordsToFind);
            unsortedTfidfMap.put(i, tfidf);

        }
        // Use a LinkedHashMap to sort the HashMap by value in 
        // ascending order.
        HashMap<Integer, Double> sortedTfidfMap = new LinkedHashMap<>();
        unsortedTfidfMap.entrySet().stream().sorted(comparingByValue()
            ).forEachOrdered(x -> sortedTfidfMap.put(x.getKey(), x.getValue()));
        
        // A HashTable will be used where the keys are the tfidfValues.
        HT tfidfTable = new HT();
        
        // Create an Integer set that contains all the page indexes 
        // (in increasing tfidf value order)
        Set<Integer> pageIndexes = new HashSet<>();
        pageIndexes = sortedTfidfMap.keySet();
        
        // Convert the Integer set to an Integer array
        Integer[] arrayPageIndexes = pageIndexes.toArray(new 
            Integer[pageIndexes.size()]);
        
        // Insert each tfidf value as a key
        for(int i = 0; i < numDocuments; i++)
            tfidfTable.add(sortedTfidfMap.get(i));
        
        // Get the tfidf values from the HT and put them into an ArrayList
        List<Double> tfidfValues = new ArrayList<Double>();
        
        // Check each table Node and retrieve the key.
        int j = 0;
        for(int i = 0; i < numDocuments; i++){
            if(j < tfidfTable.table.length){
                if(tfidfTable.table[j] != null){
                    
                    // Convert from an object to a double
                    tfidfValues.add((Double)tfidfTable.table[j].key);
                    
                    // Check to see if there are additional keys in the current
                    // table Node via the next Node. Continue doing this until 
                    // the next Node is null.
                    while(tfidfTable.table[j].next != null){
                        i++;
                        tfidfTable.table[j] = tfidfTable.table[j].next;
                        tfidfValues.add((Double)tfidfTable.table[j].key);
                    }
                    j++;
                }
                else{
                    j++;
                    i--;
                }
            }
        }
        // Sort the tfidfValues
        Collections.sort(tfidfValues);
        
        // Get the page indexes of the top two webpages
        Integer topPickIndex = arrayPageIndexes[numDocuments - 1];
        Integer secondTopPickIndex = arrayPageIndexes[numDocuments - 2];
        
        // Get the tfidf values of the top two webpages
        Double[] topMetricValues = new Double[2];
        topMetricValues[0] = tfidfValues.get(tfidfValues.size() - 1);
        
        // If the topic was not found, make the second top tfidf value NaN.
        if(topMetricValues[0].isNaN())
            topMetricValues[1] = topMetricValues[0];
        else
            topMetricValues[1] = tfidfValues.get(tfidfValues.size() - 2);
        
        Random random = new Random();
        String linkTitle = null;
        int linkDocIndex = -1;
        
        // Check if user input is a webpage from the starting file so we can 
        // update topMetricValues[0] and topPickIndex accordingly.
        Scanner wikipediaFileSCTwo = new Scanner(wikipediaFile);
        int currentDocument = 0;
        while(wikipediaFileSCTwo.hasNext()){
            
            String currentWebPage = wikipediaFileSCTwo.nextLine();
            if(currentWebPage.equalsIgnoreCase(topicOrWebPage)){
                // Typing in a webpage will almost certainly result in all 
                // tf-idf values ending up as NaN, so we will change the top 
                // value to any non-zero (preferably positive) number and the 
                // topPickIndex to whatever document the scanner is on, making 
                // this Wikipedia webpage the top priority.
                topMetricValues[0] = 1.0;
                topPickIndex = currentDocument;
                
                // Prepare a random link that was found in the entered webpage.
                // Ensure at least one link is found.
                if(linksFromAllDocs.get(topPickIndex).size() > 0){
                    linkDocIndex = random.nextInt(linksFromAllDocs.get(
                        topPickIndex).size());
                    Document linkDoc = Jsoup.connect("https://en.wikipedia.org/"
                        + linksFromAllDocs.get(topPickIndex).get(linkDocIndex)
                        ).get();
                    linkTitle = linkDoc.title();
                }
                // If the Wikipedia page has no links, then just display a 
                // random second webpage
                else
                    topMetricValues[1] = 0.0;
                
                break;
            }
            currentDocument++;
                
        }
        wikipediaFileSCTwo.close();
        
        // Preparing the top two recommended Wikipedia pages via Jsoup
        Document recDoc1 = Jsoup.connect(docs.get(topPickIndex)).get();
        Document recDoc2 = Jsoup.connect(docs.get(secondTopPickIndex)).get();
        String recTitle1 = recDoc1.title();
        String recTitle2 = recDoc2.title();
        
        // Preparing two random Wikipedia entries from the starting file in the
        // event there is either only one document with a nonzero tf-idf value 
        // or none at all.
        int firstPage = random.nextInt(numDocuments);
        int secondPage = random.nextInt(numDocuments);
        
        // Ensure secondPage is different from firstPage and topPickIndex
        while((secondPage == firstPage) || (secondPage == topPickIndex))
            secondPage = random.nextInt(numDocuments);
        Document doc1 = Jsoup.connect(docs.get(firstPage)).get();
        Document doc2 = Jsoup.connect(docs.get(secondPage)).get();
        String title1 = doc1.title();
        String title2 = doc2.title();
        
        // Case 1: All tf-idf metric values are NaNs (Keyword / website was 
        // not found)
        if((topMetricValues[0].isNaN() && topMetricValues[1].isNaN()))
            
            // Display the two random webpages
            JOptionPane.showMessageDialog(null, "We could not find any webpages"
                + " related to your topic, but maybe you'll like these: \n\n" 
                + "Title: " + title1 + "\n" + "URL: " + docs.get(firstPage) + 
                "\n\n" + "Title: " + title2 + "\n" + "URL: " + 
                docs.get(secondPage));
        
        // Case 2: Only one td-idf value is nonzero
        else if((topMetricValues[0] != 0.0 && topMetricValues[1] == 0.0))
            
            // Display the top webpage and the random second page
            JOptionPane.showMessageDialog(null, "Here's something you may "
                + "like!\n\n" + "Title: " + recTitle1 + "\n" + "URL: " + 
                docs.get(topPickIndex) + "\n\n" + "Here's something you may or "
                + "may not like!\n\n" + "Title: " + title2 + "\n" + "URL: " + 
                docs.get(secondPage));
        
        // Case 3: A webpage was entered (expecting topMetricValues[1] to not 
        // be a number
        else if(topMetricValues[0] != 0.0 && topMetricValues[1].isNaN()){
            
            // Display the top webpage and the random second page
            JOptionPane.showMessageDialog(null, "Here's something you may "
                + "like!\n\n" + "Title: " + recTitle1 + "\n" + "URL: " + 
                docs.get(topPickIndex) + "\n\n" + "Here's something from that "
                + "webpage you may also like!\n\n" + "Title: " + linkTitle + 
                "\n" + "URL: https://en.wikipedia.org" + 
                linksFromAllDocs.get(topPickIndex).get(linkDocIndex));
            
        }
        // Case 4: Two or more td-idf values are nonzero
        else
            
            // Display the top two webpages
            JOptionPane.showMessageDialog(null, "Here's something you may "
                + "like!\n\n" + "Title: " + recTitle1 + "\n" + "URL: " + 
                docs.get(topPickIndex) + "\n\n" + "Here's something else you "
                + "may like!\n\n" + "Title: " + recTitle2 + "\n" + "URL: " + 
                docs.get(secondTopPickIndex));     
    }   
}

/*
                                    References

Some Body, & Mureinik. (2018, September 19). Java sort hashmap by double value 
    not working. StackOverflow.com. 
    https://stackoverflow.com/questions/52405743/java-sort-hashmap-by-double-value-not-working 

Guendouz, M. (n.d.). a simple implementation of TF-IDF algorithm in Java. 
    Gist.GitHub.com. https://gist.github.com/guenodz/d5add59b31114a3a3c66

Paul, J. (2016, February). How to remove all special characters from String in 
    Java? Example Tutorial. Javarevisited.blogspot.com. 
    https://javarevisited.blogspot.com/2016/02/how-to-remove-all-special-characters-of-String-in-java.html#axzz8DUgL2m5w

samba, & juzraai. (2017, September 17). jsoup - how to obtain links from a text
    of an article in Wikipedia. StackOverflow.com. 
    https://stackoverflow.com/questions/46257841/jsoup-how-to-obtain-links-from-a-text-of-an-article-in-wikipedia

*/
