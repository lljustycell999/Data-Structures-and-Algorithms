# CSC365
This repository will contain the three projects completed as part of the CSC 365 - Data Structures and Algorithms course at SUNY Oswego with Professor Doug Lea.

# Assignment 1 Specification:
* Create a similarity-based website recommendation system based on Wikipedia pages.
* The program reads 10 (or more) Wikipedia pages. The URLs for these webpages can be maintained in a control file that is read when the program starts. Use a framewwork such as JSoup to extract text bodies and other pieces of vital information from HTML.
* Establish a similarity metric, that must include information based on custom frequency tables, possibly weighted by or in conjunction with other attributes (I decided to go with Term Frequency-Inverse Document Frequency, or TF-IDF).
* Create a user interface that allows a user to indicate one topic or site, and displays two similar ones. The presentation details are up to you. It may be a web/browser-based (for example Spring) or GUI base (Swing, JavaFX, or Android) for the GUI (I decided to just to a basic JavaFX GUI).
* Use appropriate existing libraries for every component other than the custom similarity tracking.
