Disk Space Informer
================

Find where all your disk space has gone - works on mac, linux and windows  

prequisite is Java 1.7 (web browser plugin is useful) or above (uses nio2).

To run it 3 ways 

- Click on the link below which will download the file link below and then double click on the file.

https://github.com/snasrallah/DiskSpaceInformer/raw/master/jar/DiskSpaceInformer.jnlp

(sometimes it will download and you run the jnlp file it may complain about java so you'll need to fetch that
plugin for your browser and operating system type)

- Download the jar from https://github.com/snasrallah/DiskSpaceInformer/raw/master/jar/DiskSpaceInformer.jar

and run:
java -jar jar/DiskSpaceInformer.jar

- Download run sources in your IDE (I used itellij , should work in eclipse) and run 



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

1f to 1g - Added tests and dos tree style format


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
- Exportability for sysadmins would be useful, may be in debug mode everything is tab separated for easy excel export.

Performance:
- Performance of getting file sizes - using nio2 is there a better  way of scanning dir's

Refactoring and Testing:
- Break out listeners into separate classes for testing.
- FindFileandFolderSizes constructor too long, builder pattern ?
- More about testing below.

New Functionality to be put in:
- Add filtering of paths or directories e.g. properties 
- Pie chart
- Look at threading , maybe thread per folder ?

TESTING
=======
Using JUnit 4 and Fest
http://junit.org/

http://fest.easytesting.org/
http://docs.codehaus.org/display/FEST/Configuration
run test via command line:

run from the root of project:

on windows
java -classpath test/lib/fest-assert-1.2.jar;test/lib/junit-4.10.jar;test/lib/fest-util-1.1.2.jar;test/lib/fest-reflect-1.2.jar;test/lib/fest-swing-1.2.jar;out/production/DiskSpaceInformer;out/test/DiskSpaceInformer org.junit.runner.JUnitCore TestSuite
on mac unix:
java -classpath test/lib/fest-assert-1.2.jar:test/lib/junit-4.10.jar:test/lib/fest-util-1.1.2.jar:test/lib/fest-reflect-1.2.jar:test/lib/fest-swing-1.2.jar:out/production/DiskSpaceInformer:out/test/DiskSpaceInformer org.junit.runner.JUnitCore TestSuite

Build env:
 - use ant to do jaring, jar key signing ...etc

