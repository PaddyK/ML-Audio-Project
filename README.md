# ML-Audio-Project
This is a student project in the scope of a machine learning lecture at university. This code is specific to our problem and
not really suitable for other projects.
Our goal is to recognize claps. For this we use opensmile to extract feautres from 50ms long wav files as arff files. It is
probably possible to do this a lot better than we did but it works ;).
We then use weka in conjunction with LibSVM to:
* do a grid search on c and gamma paramter to find the best one
* export a model to use it for live classification in opensmile

Like said before, it is very specific code, if you want to use it feel free to do so but no blame if it does not work. If you
have never worked with weka in java before the comments in the files might help you to understand it a little bit better.
