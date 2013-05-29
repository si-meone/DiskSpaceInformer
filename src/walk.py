import os
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
    print('c:\\ = %i MB \n' % getDirectorySize('c:\\'))

if __name__ == "__main__":
    main()