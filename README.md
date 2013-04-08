Disk Space Informer
================

Find where all your disk space has gone - works on mac, linux and windows 

Written in Java/Swing using JNLP to deliver it. 

To run it and continually get the latest versions
just double click on 

https://github.com/snasrallah/DiskSpaceInformer/raw/master/jar/DiskSpaceInformer.jnlp

(sometimes it will download and you run the jnlp file it may complain about java so you'll need to fetch that
plugin for your browser and operating system type)

or get the jar and run it

via
java -jar jar/DiskSpaceInformer.jar

or download the source and jar it up yourself

To jar up
=========
for intellij - other users go to where your classes got compiled

1. compile your module (creates new .classes)

2. cd out/production/DiskSpaceInformer/

3. jar cfe ../../../jar/DiskSpaceInformer.jar DiskSpaceInformer *

    4. check your jar java -jar ../../../jar/DiskSpaceInformer.jar

5. then check in the new jar and relaunch


Jar signing because it accesses the hdd resources
=================================================

see email folder under cafe....

TODO
====
- think about block sizes on different Operating systems 4kb seems standard maybe I should check.

- graphics or colors

- progress while loading 

- folders take space as well 

- Add some threading or callbacks possibly, the user interface hangs on large folders 
e.g. c:\ on a resonably spec'd XP machine with 32gb of space used took 4 mins (the interface hung till it completed)

- tests 
