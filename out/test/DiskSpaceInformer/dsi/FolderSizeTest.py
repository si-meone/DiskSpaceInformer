import os
import time

def sizeof_fmt(num):
    for x in [' B',' KB',' MB',' GB']:
        if num < 1024.0:
            return "%3.1f%s" % (num, x)
        num /= 1024.0
    return "%3.1f%s" % (num, 'TB')

def get_dir_size(file_or_folder):
    size = 0
    if os.path.isfile(file_or_folder):
        return os.path.getsize(file_or_folder)
    for path, dirs, files in os.walk(file_or_folder):
        for f in files:
            try:
                size +=  os.path.getsize( os.path.join( path, f ) )
            except:
                print("error with file:  " + os.path.join( path, f ))
    return size

start_time = time.time()
folder = "c:\WINDOWS"
folder_sizes = {}
for child in os.listdir(folder):
    path = folder + os.sep + child
    folder_sizes[path] = sizeof_fmt(get_dir_size(path))
print folder_sizes
print time.time() - start_time, "seconds"

