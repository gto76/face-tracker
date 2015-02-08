# Face Tracker
Face tracking java app using OpenCV

How to run
----------
App uses [OpenCV 3.0.0] (http://sourceforge.net/projects/opencvlibrary/files/opencv-win/3.0.0-beta/opencv-3.0.0-beta.exe/download) and [JFreeChart 1.0.19] (http://sourceforge.net/projects/jfreechart/files/1.%20JFreeChart/1.0.19/jfreechart-1.0.19.zip/download) libraries. At the top of the `Main.java` file, the paths to the OpenCV components need to be set. To get input video from a webcam use input parameter `0`, or use a path to the video file.

Functionalities
---------------
App is displaying in real time the stats of the video in form of the graphs, specifically the positions, movement vectors, sizes and number of the faces.

How it works
------------
`main()` function of the class `Main`, after the initialization of all the necessary components, starts with execution of the `mainLoop()` method. This method in every circle first captures the frame and sends it to the classificator, that returns back a set of rectangles. This rectangles then get sent to a class called `FaceLogger`, that stores locations of the faces. Every rectangle is either assigned to an existing face, or the new face is created (if rectangle is too distant from the existing faces). After that the `updateCharts()` method gets called, that takes updated data from the `faceLogger`, averages it, and sends it to the charts. After that it sends the acquired frame to the `faceLogger`, so it can draw the rectangles on the frame, and finally the frame gets sent to the screen.

