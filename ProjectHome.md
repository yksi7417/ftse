# Stock Market Analysis and Prediction #

Stock Market Analysis and Prediction is one of the interesting areas that professionals in the industry of finance and stock exchange are after. This project is actually done for an assignment for the course [Databases and Data Mining, 2009](http://www.liacs.nl/~erwin/dbdm2009/) in [LIACS](http://www.liacs.nl) at [University of Leiden](http://www.leiden.edu).

To achieve the goals, the concepts of Hidden Markov Models (HMM) are utilized for this project. To train an HMM, tuning an HMM's parameters to best match an observation, Baum-Welch Algorithm is used and to predict future data, find the probability of an observation, the Forward Backward algorithm is used. Actually, an HMM API is available with [JAHMM](http://code.google.com/p/jahmm/) to provide the above algorithms. However, JAHMM has some shortcomings regarding the implementation of mixture of multivariate Gaussian distribution for which an extension is made to provide the API. In addition, in this problem, a version of HMM called Left-right HMM (Bakis HMM) is used for which again JAHMM does provide an implementation.

Theoretical backgrounds with details could be found in [Dr. Rafiul Hassan's](http://ww2.cs.mu.oz.au/~mrhassan/index.html) Phd Thesis [here](http://ww2.cs.mu.oz.au/~mrhassan/thesis_v2.pdf). Also, further information could be retrieved from Yin Jian Rocky Zhang M.Sc. Thesis [here](ftp://fas.sfu.ca/pub/cs/TH/2004/YingjianRockyZhangMSc.pdf).

**NOTE**
As I have done this project for one of my courses in my masters and this is not a field of my profession, I may not be able to answer some specific questions on the related fields.
