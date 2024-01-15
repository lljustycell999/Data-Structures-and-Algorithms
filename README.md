# CSC 365 - Data Structures and Algorithms
This repository will contain the three projects completed as part of the CSC 365 - Data Structures and Algorithms course at SUNY Oswego with Professor Doug Lea.

# Assignment 1 Specification:
* Create a similarity-based website recommendation system based on Wikipedia pages.
* The program reads 10 (or more) Wikipedia pages. The URLs for these webpages can be maintained in a control file that is read when the program starts. Use a framewwork such as JSoup to extract text bodies and other pieces of vital information from HTML.
* Establish a similarity metric, that must include information based on custom frequency tables, possibly weighted by or in conjunction with other attributes (I decided to go with Term Frequency-Inverse Document Frequency, or TF-IDF).
* Create a user interface that allows a user to indicate one topic or site, and displays two similar ones. The presentation details are up to you. It may be a web/browser-based (for example Spring) or GUI base (Swing, JavaFX, or Android) for the GUI (I decided to just to a basic JavaFX GUI).
* Use appropriate existing libraries for every component other than the custom similarity tracking.

# Assignment 2 Specification:
This extends Assignment 1 using persistent data structures and additional similarity metrics. It requires two programs:

Program 1: Loader
* For each of at least 200 sites (URLs), create a persistent record including its word frequencies and any other similarity information
* Create a persistent block-based file-based B-Tree or Hash Table mapping the site URL to its site record.
* Traverse this map to pre-categorize (and somehow store) records into 5 to 10 clusters using k-means, k-medoids, or a similar metric (I decided to use a k-medoids algorithm via the Java Machine Learning Library)

Program 2: Application
* Extend Assignment 1 to display a category (cluster) and most similar key from the above data structures.
