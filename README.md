Disk Space Informer
================

Find where all your disk space has gone - works on mac, linux and windows:

prequisite is Java 1.7 (web browser plugin is useful) or above (uses nio2).

run instructions further down - you can run it in two clicks

![Alt text](https://raw.github.com/snasrallah/DiskSpaceInformer/master/screenshot.png "Disk Space Informer")


To run, two ways
   first way:
   1. download the jar from ![Alt text](https://github.com/snasrallah/DiskSpaceInformer/blob/master/jar/DiskSpaceInformer.jar?raw=true "here" ) 
   2. then double click on the jar or from the command line java -jar DiskSpaceInformer.jar 
   
   second way:
   2. download the source
   ant deploy  
   java -jar jar/DiskSpaceInformer.jar  

- Download run sources in your IDE (I use intellij , should work in eclipse) and from project home run 
java -classpath out/production/DiskSpaceInformer/ dsi.DiskSpaceInformer

Note: out/production/DiskSpaceInformer may be different if you compile with another IDE or command-line.

Features:
==========
- You can select one more tree items before clicking on check space.
- There is a properties files to set more folders to ignore
- There is logger that can be set to different levels


To Build
=========
Use ant and Java 1.7.0_21 was done on mac.
my mac has ant 1.8.2

*was also tested on lubuntu 12.10

New In Versions
===============

0.1a - Intial commit a start

0.1b - Added ProgressLevel which is more accurate based on amount of files in total
   - Added extra ProgressLevel bar whilst scanning
   - Avoid symlinks

0.1c - added root drive checking.

0.1d - added tree to browse and you can interact with it to find space usage , switched to Nio2
     much faster but only supports java 7 and above .

0.1e - added file vistor pattern that has responsibilty for how the folder sizes are calculated, removed progress monitor

0.1g - Added tests and dos tree style format

0.1i - Added config.properties filtering paths e.g. /proc - you can put your own in

0.1j - Added logging.properties for log levels and now we have a dsi package

0.1n - Added ant for building

0.1j - Added filter for size or alphabetically

0.1s - Added table for output.

BUGS
====
-  Sorting only works intially on size because I add MB's String sorting is occuring 
-  More work on accuracy of file checking. Folders take space as well, sometimes file permssions get in the way ?


TODO
====

Usability:
- Exportability for sysadmins would be useful, may be in debug mode everything is tab separated for easy excel export.

Performance:
- Performance of getting file sizes - using nio2 is there a better way of scanning dir's

Refactoring:
- Break out listeners into separate classes for testing.

New Functionality to be put in:
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

unix/mac   
 java -classpath test/lib/fest-assert-1.2.jar:test/lib/junit-4.10.jar:test/lib/fest-util-1.1.2.jar:test/lib/fest-reflect-1.2.jar:test/lib/fest-swing-1.2.jar:out/production/DiskSpaceInformer/:out/test/DiskSpaceInformer/ org.junit.runner.JUnitCore dsi.TestSuite

windows (almost the same but with semicolons)   
 java -classpath test/lib/fest-assert-1.2.jar;test/lib/junit-4.10.jar;test/lib/fest-util-1.1.2.jar;test/lib/fest-reflect-1.2.jar;test/lib/fest-swing-1.2.jar;out/production/DiskSpaceInformer/;out/test/DiskSpaceInformer/ org.junit.runner.JUnitCore dsi.TestSuite


Build env:
 - use ant to do jaring, jar key signing ...etc

