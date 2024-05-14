# Data Structures and Algorithms
This repository contains the three projects completed as part of a data structures and algorithms course.

# Assignment 1 Specification:
* Create a similarity-based website recommendation system based on Wikipedia pages.
* The program reads 10 (or more) Wikipedia pages. The URLs for these webpages can be maintained in a control file that is read when the program starts. Use a framewwork such as JSoup to extract text bodies and other pieces of vital information from HTML.
* Establish a similarity metric, that must include information based on custom frequency tables, possibly weighted by or in conjunction with other attributes (I decided to go with Term Frequency-Inverse Document Frequency, or TF-IDF).
* Create a user interface that allows a user to indicate one topic or site, and displays two similar ones. The presentation details are up to you. It may be a web/browser-based (for example Spring) or GUI base (Swing, JavaFX, or Android) for the GUI (I decided to just use a basic Swing GUI).
* Use appropriate existing libraries for every component other than the custom similarity tracking.

# Assignment 2 Specification:
This extends Assignment 1 using persistent data structures and additional similarity metrics. It requires two programs:

Program 1: Loader
* For each of at least 200 sites (URLs), create a persistent record including its word frequencies and any other similarity information.
* Create a persistent block-based file-based B-Tree or Hash Table mapping the site URL to its site record.
* Traverse this map to pre-categorize (and somehow store) records into 5 to 10 clusters using k-means, k-medoids, or a similar metric (I decided to use a k-medoids algorithm via the Java Machine Learning Library).

Program 2: Application
* Extend Assignment 1 to display a category (cluster) and most similar key from the above data structures.

# Assignment 3 Specification:
* Extend Assignments 1 and/or 2 to record links from each site to its neighbors, for at least 1000 total sites (ignore Wikipedia navigation links). Store the edges along with their similarity metrics persistently (possibly just in a Serialized file).
* Write a program (either GUI or web-based) that recreates the graph from step 1 and reports the number of disjoint sets as a connectivity check. Allow a user to select any two sites and display the shortest (with resepect to simiiarity weights) path between them. The path can be indicated by a series of sites, but you are encouraged to also graphically display links not taken for each site along the path.
