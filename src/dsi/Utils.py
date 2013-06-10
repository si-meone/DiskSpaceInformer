from dsi import UtilsType
from os.path import isfile, getsize, join
from os import walk
import platform
import sys
class Utils(UtilsType):
# class Utils(object):
   def __init__(self, path):
      self.path = path
      self.errors = ''

   def sizeof_fmt(self, num):
       for x in [' B',' KB',' MB',' GB']:
           if num < 1024.0:
               return "%3.1f%s" % (num, x)
           num /= 1024.0
       return "%3.1f%s" % (num, 'TB')


   def get_free_space(self):

       """ Return folder/drive free space (in bytes)
       """
       import java.lang.reflect.Array
       import java.io.File
       roots = java.io.File.listRoots()
       rows = len(roots) * 4
       str2d = java.lang.reflect.Array.newInstance(java.lang.String,[rows, 2])
       row_count = 0
       for i in roots:
           str2d[row_count][0] = str(i)
           str2d[row_count][1] = 'Total capacity: ' + self.sizeof_fmt(i.getTotalSpace())
           row_count += 1
           str2d[row_count][1] = 'Used Space:  ' + self.sizeof_fmt(i.getTotalSpace() - i.getFreeSpace())
           row_count += 1
           str2d[row_count][1] = 'Free Space:  ' + self.sizeof_fmt(i.getFreeSpace())
           row_count += 2
       return str2d

   def get_dir_size(self, file_or_folder):
       self.errors = ''
       size = 0
       if isfile(file_or_folder):
           return getsize(file_or_folder)
       for path, dirs, files in walk(file_or_folder):
           try:
               size += sum([getsize(join(path, name)) for name in files])
           except Exception, e:
               self.errors += "error with file:  " + name + '\n'
       return size

   def get_errors(self):
       return self.errors

def main():
    path = '/Users/snasrallah'
    utils = Utils(path)
    # #print('%s = %s \n' % (path, Utils(path).getFolderSizes()))
    # print(utils.get_free_space())
    import time, os
    start_time = time.time()
    folder = "/Users/snasrallah"
    folder_sizes = {}
    for child in os.listdir(folder):
        path = folder + os.sep + child
        folder_sizes[path] = utils.sizeof_fmt(utils.get_dir_size(path))
    print folder_sizes
    # print utils.sizeof_fmt(utils.get_dir_size(path))
    print time.time() - start_time, "seconds"

if __name__ == "__main__":
    main()
