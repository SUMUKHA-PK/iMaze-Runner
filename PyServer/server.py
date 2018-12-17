import socket 
import pickle
import os
import sys
import io
import threading
from db import add_cred
from db import authenticate as auth
from time import gmtime,strftime
import signal
import random
from pathlib import Path

path_to_script = 'D:\\ACADEMICS\\NTC-1819-MiniProject-16CO145-234\\src\\Analysis'
sys.path.append(path_to_script)

print(sys.path)

from EAMRSA_Test import script

SERVER_IP="192.168.43.10"
SERVER_PORT = 12345
SIZE = 4096

cur_threads = []

last_file = ""

class FileThread(threading.Thread):
    def __init__(self,threadID, name,conn,addr,data):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.conn = conn
        self.addr = addr
        self.data = data

    def run(self):
        print("File transfer begins: ")
        cur_time = str(strftime("%Y-%m-%d %H:%M:%S", gmtime())).replace(" ","_").replace(":","_")
        file_path = "./images/"+cur_time+".png"
        last_file = file_path
        directory = os.path.dirname(file_path)
        if not os.path.exists(directory):
            os.makedirs(directory)
        myfile=open(file_path,'wb')

        while True:
            if not self.data:
                break
            myfile.write(self.data)
            self.data = self.conn.recv(SIZE)
        print("Image transferred!")
        myfile.close()

        self.conn.close()
        cur_threads.remove(self.threadID)
        
class LoginRegisterThread(threading.Thread):

    def __init__(self,threadID,name,conn,addr,data):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.conn = conn
        self.addr = addr
        self.data = data

    def run(self):
        print("DATA received:\n_____________________")
        print(self.data)
        print("_________________________")
        result = self.process()
        out = str(result).encode()
        self.conn.send(out)
        print(out)

        self.conn.close()
        cur_threads.remove(self.threadID)
        

    def process(self):
        try:
            credentials = self.data.decode().split("/0")
            if(credentials[0]=="register"):
                try:
                    add_cred(credentials)
                except:
                    return False
                return True
            elif(credentials[0]=="login"):
                try:
                    result = auth(credentials)
                except:
                    return False
                return result
        except Exception as e:
            print("Decoding error in process:",e)

class ActionThread(threading.Thread):

    def __init__(self,threadID,name,conn,addr,data,result):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.conn = conn
        self.addr = addr
        self.data = data
        self.result = result

    def run(self):
        print("Action working!")
        if(self.result=="action1"):
            script.runner()
        self.conn.close()
        cur_threads.remove(self.threadID)

class PingThread(threading.Thread):

    def __init__(self,threadID,name,conn,addr):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.conn = conn
        self.addr = addr

    def run(self):
        out = str("true").encode()
        self.conn.send(out)
        self.conn.close()
        cur_threads.remove(self.threadID)


def check_dest(data):
    try:
        input = data.decode().split("/0")
        if((input[0]=="register")|(input[0]=="login")):
            return "cred"
        elif((input[0]=="action1")):
            return "action1"
        elif((input[0]=="action2")):
            return "action2"
        elif((input[0]=="ping")):
            return "ping"
    except Exception as e:
        return "file"


def call_thread(threadID,conn,addr):
    data = conn.recv(SIZE)
    result = check_dest(data)
    if(result == "cred"):
        cred = LoginRegisterThread(threadID,"LR",conn,addr,data)
        cred.start()
    elif(result == "file"):
        files = FileThread(threadID,"FT",conn,addr,data)
        files.start()
    elif((result == "action1")|(result=="action2")):
        print("ACTION")
        action = ActionThread(threadID,"AT",conn,addr,data,result)
        action.start()
    elif(result=="ping"):
        ping = PingThread(threadID,"PT",conn,addr)
        ping.start()
    

def main():

    server_sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    server_sock.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)

    try: 
        server_sock.bind((SERVER_IP,SERVER_PORT))
    except Exception as e:
        print(e)
    
    server_sock.listen(10)

    while True :
        print("Server is listening for incoming connections")

        conn = None
        conn,addr = server_sock.accept()

        print("Connection accepted from : ",end=" ")
        print(addr)

        if(conn != None):
            thread_no = random.randint(1,1000)
            while thread_no in cur_threads:
                thread_no = random.randint(1,1000)
            cur_threads.append(thread_no)
            call_thread(thread_no,conn,addr)    
        print(cur_threads)

if __name__ == "__main__":
    main()




    