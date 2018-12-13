import socket 
import pickle
import os
import io
import threading
from db import add_cred
from db import authenticate as auth
from time import gmtime,strftime
import signal
import random

SERVER_IP="192.168.43.10"
SERVER_PORT_LOGIN=12346
SERVER_PORT_FILES=12345
SIZE = 8192

def signal_handler(signal,frame):
    global isInterrupted
    isInterrupted = True

cur_threads = []

class FileThread(threading.Thread):
    def __init__(self,threadID, name,conn,addr,data):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
        self.conn = conn
        self.addr = addr
        self.data = data

    def run(self):
        print("client connected for sending files")
        cur_time = str(strftime("%Y-%m-%d %H:%M:%S", gmtime())).replace(" ","_").replace(":","_")
        file_path = "./images/"+cur_time+".png"
        directory = os.path.dirname(file_path)
        if not os.path.exists(directory):
            os.makedirs(directory)
        myfile=open(file_path,'wb')

        while True:
            if not self.data:
                break
            myfile.write(self.data)
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
        print("client connected for login/register")
        print("DATA received:\n_____________________")
        print(self.data)
        print("_________________________")
        result = self.process(self.data)
        out = str(result).encode()
        self.conn.send(out)
        print(out)

        self.conn.close()
        cur_threads.remove(self.threadID)
        

    def process(self,data):
    # Basically this must check the hash of the password (currently just match strings and return a value)
        try:
            credentials = data.decode().split("/0")
            if(credentials[0]=="zero"):
                try:
                    add_cred(credentials)
                except:
                    return False
                return True
            elif(credentials[0]=="one"):
                try:
                    result = auth(credentials)
                except:
                    return False
                return result
        except Exception as e:
            print("Decoding error in process:",e)

def call_cred_thread(threadID,name,conn,addr,data):
    files = FileThread(threadID,name,conn,addr,data)
    files.start()

def call_file_thread(threadID,name,conn,addr,data):
    cred = LoginRegisterThread(threadID,name,conn,addr,data)
    cred.start()

def check_dest(data):
    input = data.decode().split("/0")
    if((input[0]=="zero")|(input[0]=="one")):
        return "cred"
    # elif(input[0]=="file"):
    else:
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

def main():

    server_sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
    server_sock.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)

    try: 
        server_sock.bind((SERVER_IP,SERVER_PORT_LOGIN))
    except Exception as e:
        print(e)
    
    server_sock.listen(10)

    print("Server is listening for incoming connections")

    while True :
        print("nothing happenning") 
        conn,addr = server_sock.accept()

        print("Accepting stage")

        if(conn != None):
            thread_no = random.randint(1,1000)
            while thread_no in cur_threads:
                thread_no = random.randint(1,1000)
            cur_threads.append(thread_no)
            call_thread(thread_no,conn,addr)    
        print(cur_threads)

if __name__ == "__main__":
    main()9