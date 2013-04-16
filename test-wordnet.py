#!/usr/bin/env python

# ------------------------------
# naed/test-wordnet.py
# Sample uses of nltk
# -------------------------------

"""
To run the program
    % test-wordnet.py

"""

from nltk.corpus import wordnet



def demoSimilarities():
    cb=wordnet.synset('cookbook.n.01')
    ib=wordnet.synset('instruction_book.n.01')
    
    print "Wu-Palmer similarity: " + str(cb.wup_similarity(ib))
    print "Path similarity: " + str(cb.path_similarity(ib))
    print "LCH similarity: " + str(cb.lch_similarity(ib))



def main():
    demoSimilarities()
    


main()