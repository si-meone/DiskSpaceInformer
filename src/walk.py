import os
import sys
import time
import timeit

def getDirectorySize(directory):
    folder = directory
    folderSize = 0

    for (path, dirs, files) in os.walk(folder):
        for file in files:
            filename = os.path.join(path, file)
            try:
                folderSize += os.path.getsize(filename)
            except OSError as ose:
                print 'An OS error occured :%s' %ose

    return (folderSize/(1024*1024.0))

def main():
    print('/ = %i MB \n' % getDirectorySize('/'))

if __name__ == "__main__":
    import time
    start_time = time.time()
    main()
    end_time = time.time()
    print("Elapsed time was %g seconds" % (end_time - start_time))
