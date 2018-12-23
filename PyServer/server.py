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

from EAMRSA_Test import script

SERVER_IP="192.168.43.10"
SERVER_PORT = 12345
SIZE = 4096

cur_threads = []

last_file = ""

#Basically each class represents a thread.
class FileThread(threading.Thread):
    def __init__(self,threadID, name,conn,addr,data):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.conn = conn
        self.addr = addr
        self.data = data

    def run(self):
        # Each image file gets a new name based on the current date-time
        print("File transfer begins: ")
        cur_time = str(strftime("%Y-%m-%d %H:%M:%S", gmtime())).replace(" ","_").replace(":","_")
        file_path = "./images/"+cur_time+".png"
        last_file = file_path # To process the image(Need a better way based in the time of processing)
        directory = os.path.dirname(file_path)
        if not os.path.exists(directory):
            os.makedirs(directory)
        myfile=open(file_path,'wb')

        # The image is sent in small segments until the mobile device stops sending
        while True:
            if not self.data:
                break
            myfile.write(self.data)
            self.data = self.conn.recv(SIZE)
        print("Image transferred!")
        myfile.close()

        self.conn.close()
        print("Log: Removing thread %d from current thread list" % self.threadID)
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
        result = self.process()
        out = str(result).encode()
        self.conn.send(out)

        self.conn.close()
        print("Log: Removing thread %d from current thread list" % self.threadID)
        cur_threads.remove(self.threadID)
        

    def process(self):
        # Checks what kind of request it needs to serve and routes to appropriate functions
        try:
            credentials = self.data.decode().split("/0")
            if(credentials[0]=="register"):
                try:
                    add_cred(credentials)
                    print("Successfully registered!")
                except:
                    return False
                return True
            elif(credentials[0]=="login"):
                try:
                    result = auth(credentials)
                    print(result)
                    if(result=="True"):
                        print("Logged in successfully")
                    else:
                        print("Error in logging in")
                except:
                    return False
                return result
        except Exception as e:
            print("Exception : Decoding error in process:",e)

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
        # Runs the necessary action based on demand
        if(self.result=="action1"):
            print("Executing action 1")
            script.runner()
        self.conn.close()
        print("Log: Removing thread %d from current thread list" % self.threadID)
        cur_threads.remove(self.threadID)

class PingThread(threading.Thread):

    def __init__(self,threadID,name,conn,addr):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.conn = conn
        self.addr = addr

    def run(self):
        # Replies back to a ping from the client searching for existance of a server
        print("Ping from : ",end=" ")
        print(self.addr)
        out = str("true").encode()
        self.conn.send(out)
        self.conn.close()
        print("Log: Removing thread %d from current thread list" % self.threadID)
        cur_threads.remove(self.threadID)


def check_dest(data):
    # Function that classifies the requests 
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
    # Function that starts the thread based on the requests
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

    # Server is listening all the time for incoming requests
    while True :
        print("Server is listening for incoming connections")

        # Whenever a connection is received, it accepts it and creates a new thread on classifying the job
        conn = None
        conn,addr = server_sock.accept()

        print("Log: Connection accepted from : ",end=" ")
        print(addr)

        if(conn != None):
            thread_no = random.randint(1,1000)
            while thread_no in cur_threads:
                thread_no = random.randint(1,1000)
            cur_threads.append(thread_no)
            call_thread(thread_no,conn,addr)   
        print("Log: Thread %d added to current thread list" % thread_no) 
        print(cur_threads)

if __name__ == "__main__":
    main()




    