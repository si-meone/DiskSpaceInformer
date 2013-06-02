from dsi import UtilsType
import os
import platform
import sys
class Utils(UtilsType):
# class Utils(object):
   def __init__(self, path):
      self.path = path

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


def main():
    path = '/'
    utils = Utils(path)
    #print('%s = %s \n' % (path, Utils(path).getFolderSizes()))
    print(utils.get_free_space())

if __name__ == "__main__":
    main()
