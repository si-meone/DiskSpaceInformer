Disk Space Informer
================

Find where all your disk space has gone - works on mac, linux and windows  

prequisite is Java 1.6 or above.

To run it click on the link below which will download the file link below and then double click on the file.

https://github.com/snasrallah/DiskSpaceInformer/raw/master/jar/DiskSpaceInformer.jnlp

Written in Java/Swing using JNLP to deliver it, upgrades are delivered on the fly by relaunching the jnlp file

(sometimes it will download and you run the jnlp file it may complain about java so you'll need to fetch that
plugin for your browser and operating system type)

or get the jar and run it

via
java -jar jar/DiskSpaceInformer.jar

or download the source and compile it up yourself using javac in the src folder


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


New In Versions
===============

1a - Intial commit a start

1b - Added ProgressLevel which is more accurate based on amount of files in total
   - Added extra ProgressLevel bar whilst scanning
   - Avoid symlinks

1c - added root drive checking.

1d - added tree to browse and you can interact with it to find space usage
 

TODO
====

- Tree structure for leafs don't need full path as you can see above what path you are in.
- Better text alignment in jtext area for scrolling data.
- Performance of getting file sizes - investigate nio2 or apache commons.
- Break out listeners into separate classes for testing.
- bad problem with sys folders on linux like /sys , /dev reporting as being huge - may have to do more.
- think about block sizes on different Operating systems 4kb seems standard maybe I should check.
- mac swing chooser doesn't let you choose root drive, only folders below.
- graphics or colors
- more work on accuracy of file checking.
- folders take space as well ?
- add some threading or callbacks possibly, the user interface hangs on large folders 
  e.g. c:\ on a resonably spec'd XP machine with 32gb of space used took 1min (the interface hung till it completed)
- tests 
- cancel folder sizing needs a bit more investigation , notice CPU stays at 100 % if there is a problem e.g. processing /sys on linux
- look at bringing in nio2 jar in java 7

