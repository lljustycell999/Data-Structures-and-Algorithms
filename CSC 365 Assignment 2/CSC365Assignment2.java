package csc.pkg365.assignment.pkg2;

import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;

import java.util.*;
import net.sf.javaml.clustering.KMedoids;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.distance.EuclideanDistance;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CSC365Assignment2 {
    
    // This ArrayList will contain the URLs of each Wikipedia webpage 
    static List<String> docs = new ArrayList<String>();
    
    // This ArrayList will contain the keywords of each Wikipedia webpage
    static List<List<String>> keyWordsFromAllDocs = new ArrayList<List<String>>();
    
    // This ArrayList with contain all of the links from each Wikipedia webpage
    static List<List<String>> linksFromAllDocs = new ArrayList<List<String>>();
    
    private static int COUNT_OFFSET = 0;
    
    static int numDocuments = 0;
    
    // True if the user typed in a webpage in the Wikipedia file, false otherwise.
    // Keep track of the index as well.
    static boolean isAWebpage = false;
    static int webpageIndex = -1;
    
    // Should change to a non-negative number if the entered topic is apparent in
    // at least one wikipedia page (but not all of them)
    static double topTfidf = -9.99;

    // True if the topic is apparent in at least one wikipedia page, false otherwise
    static boolean easyCase = false;
    
    public static void main(String[] args) throws Exception {
        
        // Get the topic or webpage from the user
        String topicOrWebpage = getTopicOrWebpage();
        
        // Split the input into multiple strings based on how many words are in it.
        String[] wordsToFind = topicOrWebpage.split(" ");
        
        // Get the URLs from the file and the text extractions from Jsoup
        getURLsAndTextExtractions(topicOrWebpage);
        
        /* Start of Additions for Assignment 2 */
        
        // Create a B-Tree that holds each document and its tfidf-value
        BTree btree = new BTree();
        createBTree(btree, wordsToFind);
        
        // Store the B-Tree data into a RandomAccessFile
        RandomAccessFile raf = new RandomAccessFile("BTreeBlocking.dat", "rw");
        saveToBlockFile(btree, raf);
        
        // Now retrieve the B-Tree data
        double[] tfidfValues = loadBlockFile(btree, raf);
        
        // Use some number of clusters (between 5-10) for the K-Medoids algorithm
        int numClusters = 5;
        
        // The K-Medoids algorithm is initialized via a Java Machine Learning library, 
        // passing in the number of clusters, number of iterations, and the 
        // distance metric.
        KMedoids kmedoids = new KMedoids(numClusters, 100, new EuclideanDistance());
        
        // Prepare the Dataset to contain all tfidf-values for clustering
        Dataset dataset = prepareKMedoidsAlgorithm(tfidfValues);
        
        // Perform and get the clusers
        Dataset[] clusters = performCluster(kmedoids, dataset);
            
        // Find the cluster that contains the highest tfidf-value (this cluster
        // should also contain similarly high tfidf-values).
        Dataset topCluster = new DefaultDataset();
        topCluster = getTopCluster(clusters);
           
        int[] docIndexes = new int[topCluster.size()];
        Document[] theDocs = new Document[topCluster.size()];
        String[] theTitles = new String[topCluster.size()];

        int topDocID = -1;
        Document theTopDoc = null;
        String theTopTitle = null;
            
        // Obtain the pages and titles from the top cluster to display
        for(int i = 0; i < topCluster.size(); i++){
            
            // Different ways are utilized to get doc indexes and the documents
            // based on if we have a webpage, an easy case, or neither.
            if(isAWebpage){
                docIndexes[i] = topCluster.get(i).getID() - numDocuments;
                theDocs[i] = Jsoup.connect("https://en.wikipedia.org/" + linksFromAllDocs.get(webpageIndex).get(docIndexes[i])).get();
            }
            else if(easyCase){
                docIndexes[i] = topCluster.get(i).getID();
                theDocs[i] = Jsoup.connect(docs.get(docIndexes[i])).get();
            }
            else{
                docIndexes[i] = topCluster.get(i).getID() - numDocuments;
                theDocs[i] = Jsoup.connect(docs.get(docIndexes[i])).get();
            }
            theTitles[i] = theDocs[i].title();
            
            // Reserve the document data that has the top tfidf value
            if(topCluster.get(i).value(0) == topTfidf && easyCase){
                topDocID = docIndexes[i];
                theTopDoc = Jsoup.connect(docs.get(docIndexes[i])).get();
                theTopTitle = theTopDoc.title();
            }   
        }
        
        // Finally, display the top cluster
        displayCluster(topCluster, theTitles, theDocs, theTopTitle, theTopDoc);
        
    }
    
    public static String getTopicOrWebpage(){
        
        // Get the topic or webpage from the user.
        String topicOrWebPage = JOptionPane.showInputDialog("Enter your "
            + "favorite topic or webpage");
        
        return topicOrWebPage;
    }
    
    public static void getURLsAndTextExtractions(String topicOrWebpage) throws 
        IOException{
        
        String page;
        
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
            
            // If our input matches a webpage from the file, note that we have a webpage
            // and record the index of the webpage
            if(page.equalsIgnoreCase(topicOrWebpage)){
                isAWebpage = true;
                webpageIndex = numDocuments;
            }
                
            // Go to the next Wikipedia webpage.
            numDocuments++;

        }
        wikipediaFileSC.close();
    }
    
    public static void createBTree(BTree btree, String[] wordsToFind) throws IOException{

        // Calculate the tf-idf value for each document with respect to the wordsToFind variable.
        TFIDFCalculator calculator = new TFIDFCalculator();  
        for(int i = 0; i < numDocuments; i++){
            
            // Passing in the keywords from the current Wikipedia page we are 
            // on, the keywords from all Wikipedia pages from the starting file,
            // and the array of strings we are interested in finding.
            double tfidf = calculator.tfIdf(keyWordsFromAllDocs.get(i), 
                keyWordsFromAllDocs, wordsToFind);
            
            // Insert the doc and its metric into the B-Tree
            btree.put(docs.get(i), tfidf);   
        }
        
    }
    
    public static void saveToBlockFile(BTree btree, RandomAccessFile raf) throws IOException{
        
        raf.setLength(0);
        for(int i = 0; i < btree.size(); i++){
            raf.writeDouble((double) btree.get(docs.get(i))); // TFIDF
            raf.writeBytes("\r\n"); // New Line
        }

    }
    
    public static double[] loadBlockFile(BTree btree, RandomAccessFile raf) throws IOException{
        
        double[] tfidfValues = new double[btree.size()];
        for(int i = 0; i < btree.size(); i++){
           raf.seek(COUNT_OFFSET); // Start at first byte
           tfidfValues[i] = raf.readDouble();
           COUNT_OFFSET = COUNT_OFFSET + 10; // Advance 8 bytes for double and 2 bytes for next line
        }
        return tfidfValues;
    }
    
    public static Dataset prepareKMedoidsAlgorithm(double[] tfidfValues){
        
        // The K-Medoids algorithm takes a Dataset Interface as a parameter, 
        // which in this case will contain the tfidf-values we want to cluster.
        Dataset dataset = new DefaultDataset();
        Instance[] instances = new Instance[numDocuments];
        
        // Get each tfidf value in increasing doc index order by traversing the B-Tree.
        // The tfidf value will be stored as an instance (another interface) 
        // with the doc index playing as an ID. After that, the instance can 
        // be added to the dataset.
        
        // We also keep track of the highest tfidf value here as well.
        double curTfidf;
        for(int i = 0; i < numDocuments; i++){
            curTfidf = tfidfValues[i];
            instances[i] = new DenseInstance(new double[] {curTfidf}, i);
            dataset.add(instances[i]);   
            if(curTfidf >= topTfidf)
                topTfidf = curTfidf;
        }
        return dataset;
        
    }
    
    public static Dataset[] performCluster(KMedoids kmedoids, Dataset dataset){
        
        // Check if a clustering can be performed right now   
        if(topTfidf >= 0.0){
            easyCase = true;
            return kmedoids.cluster(dataset);
        }
        else{
            // If clustering can't happen right now, the user typed in something that made all tfidf-values
            // turn into NaNs (possibly a webpage)
            
            Random random = new Random();
            Dataset newDataset = new DefaultDataset();
            topTfidf = 0.0;
            
            // Check if the page entered was a webpage
            if(isAWebpage){
                
                // Try for a random cluster of links
                if(linksFromAllDocs.get(webpageIndex).size() > 0){
                    Instance[] moreInstances = new Instance[linksFromAllDocs.get(webpageIndex).size()];
                    JOptionPane.showMessageDialog(null, "Please be advised, you "
                        + "entered a webpage, so the cluster will be random links "
                        + "from that webpage"); 
                    for(int i = 0; i < moreInstances.length; i++){
                        double fakeTfidf = random.nextDouble();
                        moreInstances[i] = new DenseInstance(new double[] {fakeTfidf}, i);
                        newDataset.add(moreInstances[i]);   
                    }
                }
                
                else{
                    Instance[] moreInstances = new Instance[docs.size()];
                    for(int i = 0; i < docs.size(); i++){
                        double fakeTfidf = random.nextDouble();
                        moreInstances[i] = new DenseInstance(new double[] {fakeTfidf}, i);
                        newDataset.add(moreInstances[i]);   
                    }
                    JOptionPane.showMessageDialog(null, "Please be advised, you "
                        + "entered a webpage, but there are no links in it, so"
                        + " the cluster will be completely random Wikipedia pages"); 
                }
            }
            
            // If we get here, then we know the topic/webpage was not found at all
            else{
                
                // We will just use some random tfidf values and our original docs
                Instance[] moreInstances = new Instance[docs.size()];
                
                for(int i = 0; i < docs.size(); i++){
                    double fakeTfidf = random.nextDouble();
                    moreInstances[i] = new DenseInstance(new double[] {fakeTfidf}, i);
                    newDataset.add(moreInstances[i]);   
                }
                JOptionPane.showMessageDialog(null, "Please be advised, you "
                    + "entered a topic/webpage that is not recognized, so the "
                    + "cluster will be random Wikipedia pages"); 
                
            }
            return kmedoids.cluster(newDataset);
        }
    }
    
    public static Dataset getTopCluster(Dataset[] clusters){
        
        // Find the cluster that contains the highest tfidf-value (this cluster
        // should also contain similarly high tfidf-values).
        Dataset topCluster = new DefaultDataset();

        getTopCluster:
        for(int i = 0; i < clusters.length; i++){
            for(int j = 0; j < clusters[i].size(); j++){
                if(clusters[i].get(j).value(0) >= topTfidf){
                    topCluster = clusters[i];
                    break getTopCluster;
                }
            }
        }
        return topCluster;
    }
    
    public static void displayCluster(Dataset topCluster, String[] theTitles, Document[] theDocs, String theTopTitle,
        Document theTopDoc){
        
        JOptionPane.showMessageDialog(null, "Here's a cluster of Wikipedia "
            + "pages based on your search"); 
        
        // Add each title and doc to a JPanel
        int numPanels = 0;
        if(topCluster.size() % 10 == 0)
            numPanels = topCluster.size() / 10;
        else
            numPanels = (topCluster.size() / 10) + 1;    
        JPanel[] panels = new JPanel[numPanels];
        int currentDoc = 0;

        for(int i = 0; i < numPanels; i++){
            if(currentDoc == topCluster.size() && currentDoc % 10 != 0 && !easyCase){
                JOptionPane.showMessageDialog(new JFrame(), panels[i - 1], "A Wannabe Google "
                    + "(with clustering)", JOptionPane.PLAIN_MESSAGE);
            }
            panels[i] = new JPanel();
            panels[i].setLayout(new BoxLayout(panels[i], BoxLayout.PAGE_AXIS)); 
            for(int j = 0; j < 10; j++){
                if(currentDoc != topCluster.size()){
                    panels[i].add(new JLabel("Title: " + theTitles[currentDoc]));
                    panels[i].add(new JLabel("URL: " + theDocs[currentDoc].location()));
                    panels[i].add(new JLabel(" "));
                    currentDoc++;
                }
                else
                    break;
            }
            JOptionPane.showMessageDialog(new JFrame(), panels[i], "A Wannabe Google "
                + "(with clustering)", JOptionPane.PLAIN_MESSAGE);
                
        }
        if(easyCase){
            JOptionPane.showMessageDialog(null, "From the cluster, this is our top "
                + "recommendation: \n\n" + "Title: " + 
                theTopTitle + "\n" + "URL: " + 
                theTopDoc.location());
        }
        System.exit(0);
    }
    
}

/*                              References

Abeel, T. (2014, March 28). KMedoids.java. GitHub.com. 
    https://github.com/greenmoon55/textclustering/blob/master/src/net/sf/javaml/clustering/KMedoids.java

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

Sedgewick, R., &amp; Wayne, K. (2023, September 13). BTree.java. 
    algs4.cs.princeton.edu. 
    https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/BTree.java.html

*/
