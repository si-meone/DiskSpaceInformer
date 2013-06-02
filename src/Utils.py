from dsi import UtilsType
import os
import platform
import sys
class Utils(UtilsType):
# class Utils(object):
   def __init__(self, path):
      self.path = path

   def get_folder_sizes(self):
       folderSize = 0

       # for (path, dirs, files) in os.walk(self.path):
       #     for file in files:
       #         filename = os.path.join(path, file)
       #         try:
       #             folderSize += os.path.getsize(filename)
       #         except OSError as ose:
       #             print 'An OS error occured :%s' %ose
       return folderSize

   def sizeof_fmt(self, num):
       for x in [' B',' KB',' MB',' GB']:
           if num < 1024.0:
               return "%3.1f%s" % (num, x)
           num /= 1024.0
       return "%3.1f%s" % (num, 'TB')


   def get_free_space(self):
       """ Return folder/drive free space (in bytes)
       """
       # if platform.system() == 'Windows':
       #     free_bytes = ctypes.c_ulonglong(0)
       #     ctypes.windll.kernel32.GetDiskFreeSpaceExW(ctypes.c_wchar_p(self.path), None, None, ctypes.pointer(free_bytes))
       #     return free_bytes.value
       # else:
       #     # s = os.statvfs(self.path)
       #     # return self.sizeof_fmt((s.f_bavail * s.f_frsize) / 1024)
       return '0 B'

def main():
    path = '/'
    utils = Utils(path)
    #print('%s = %s \n' % (path, Utils(path).getFolderSizes()))
    print(utils.get_free_space())

if __name__ == "__main__":
    main()
