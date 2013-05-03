Disk Space Informer
================

Find where all your disk space has gone - works on mac, linux and windows  

prequisite is Java 1.7 (web browser plugin is useful) or above (uses nio2).

To run it 3 ways 

1. click on the link below which will download the file link below and then double click on the file.

https://github.com/snasrallah/DiskSpaceInformer/raw/master/jar/DiskSpaceInformer.jnlp

(sometimes it will download and you run the jnlp file it may complain about java so you'll need to fetch that
plugin for your browser and operating system type)

2. download the jar from https://github.com/snasrallah/DiskSpaceInformer/raw/master/jar/DiskSpaceInformer.jar

and run:
java -jar jar/DiskSpaceInformer.jar

3. Download run sources in your IDE (I used itellij , should work in eclipse) and run 



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

1d - added tree to browse and you can interact with it to find space usage , switched to Nio2
     much faster but only supports java 7 and above .

1e - added file vistor pattern that has responsibilty for how the folder sizes are calculated, removed progress monitor



BUGS
====
- Mac swing chooser doesn't let you choose root drive, only folders below.
- Windows or Linux if you select a root drive it doesn't show the drive or root name in the root
  node.
- Cancel folder sizing needs a bit more investigation , notice CPU stays at 100 % if there is a problem e.g. processing /sys on linux
- Bad problem with sys folders on linux like /sys , /dev reporting as being huge - may have to do more.
- Block sizes on different Operating systems 4kb seems standard maybe I should check.
- More work on accuracy of file checking. Folders take space as well ?


TODO
====

Usability:
- Tree structure for leafs don't need full path as you can see above what path you are in.
- Better text alignment in text area for scrolling data.
- Pie chart representation of space used
- Add to right click menu , summary or break down of sub-folders.

Performance:
- Performance of getting file sizes - investigate nio2 or apache-commons-io.

Refactoring and Testing:
- Break out listeners into separate classes for testing.
- FindFileandFolderSizes constructor too long , maybe a progress
- Look at virtual or mock or real file system test ,needs to work across all OS's


New Functionality:
- Look at threading , maybe thread per folder ?
