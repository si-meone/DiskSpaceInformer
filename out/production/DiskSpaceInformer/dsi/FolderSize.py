import threading
import time
from java.util.concurrent import Callable

from os.path import isfile, getsize, join
from os import walk, listdir
import platform
import sys
import time, os


class FolderSize(Callable):

    def __init__(self, folder):
        self.folder = folder
        self.started = None
        self.completed = None
        self.size = None
        self.thread_used = None
        self.exception = None
        self.total_size = 0

    def __str__(self):
        if self.exception:
            # print 'exception'
            return "[%s] %s error %s in %.2fs" % \
                (self.thread_used, self.folder, self.exception,
                 self.completed - self.started, ) #, self.result)
        elif self.completed:
            self.total_size = self.size
            return "[%s] %s %s in %.2fs" % \
                (self.thread_used, self.folder, self.sizeof_fmt(self.size),
                 self.completed - self.started, ) #, self.result)
        elif self.started:
            # print 'started'
            return "[%s] %s started at %s" % \
                (self.thread_used, self.folder, self.started)
        else:
            # print 'not scheduled'
            return "[%s] %s not yet scheduled" % \
                (self.thread_used, self.folder)

    def sizeof_fmt(self, num):
        for x in [' B',' KB',' MB',' GB']:
            if num < 1024.0:
                return "%3.1f%s" % (num, x)
            num /= 1024.0
        return "%3.1f%s" % (num, 'TB')

    # needed to implement the Callable interface;
    # any exceptions will be wrapped as either ExecutionException
    # or InterruptedException
    def call(self):
        self.thread_used = threading.currentThread().getName()
        self.started = time.time()
        self.size = 0
        self.exception = ''
        print self.thread_used,  self.folder

        if isfile(self.folder):
            # print self.folder, 'is file'
            self.total_size = getsize(self.folder)
            return self
        for path, dirs, files in walk(self.folder):
            # print 'walk path', path
            # print 'walk dirs ', dirs
            # print 'walk files ', files

            try:
                self.total_size += sum([getsize(join(path, name)) for name in files])
            except Exception, e:
                print e

        self.completed = time.time()
        # print 'total_size', self.total_size
        # self.total_size = self.size
        return self



# def get_folder_sizes(base='c:\\'):


if __name__ == "__main__":
    get_folder_sizes()
