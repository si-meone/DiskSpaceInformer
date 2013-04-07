DiskSpaceManager
================

Find where all your disk space has gone - works on mac, linux and windows just double click on jnlp/DirectorySizer.jnlp

locally

http://

via
java -jar jar/DiskSpaceInformer.jar

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