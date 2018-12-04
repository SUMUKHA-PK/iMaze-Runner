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
SERVER_PORT_CRED=12345
FILE_SIZE = 8192

def signal_handler(signal,frame):
    global isInterrupted
    isInterrupted = True


class FileThread(threading.Thread):
    def __init__(self,threadID, name):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name
    def run(self):
        print("imageReceiver server is running\n")
        server_sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        serverAddress = SERVER_IP
        serverPort = SERVER_PORT_FILES
        try:
            server_sock.bind((serverAddress,serverPort))
        except Exception as e:
            print(e)

        server_sock.listen(10)

        while True:
            client,addr = server_sock.accept()
            print("client connected for sending files")
            cur_time = str(strftime("%Y-%m-%d %H:%M:%S", gmtime())).replace(" ","_").replace(":","_")
            file_path = "./images/"+cur_time+".jpg"
            directory = os.path.dirname(file_path)
            if not os.path.exists(directory):
                os.makedirs(directory)
            myfile=open(file_path,'wb')
            signal.signal(signal.SIGINT, signal_handler)

            while True:
                data=client.recv(FILE_SIZE)
                if not data:
                    break
                myfile.write(data)
            myfile.close()
            
            # Open matlab, run the script for this image and get results, send back to client

            client.close()
            if isInterrupted:
                print("imageReceiver server shutting down")
                break

class LoginRegisterThread(threading.Thread):
    def __init__(self,threadID,name):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name

    def run(self):
        print("handshake server is running\n")
        server_sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        serverAddress = SERVER_IP
        serverPort = SERVER_PORT_LOGIN
        try:
            server_sock.bind((serverAddress,serverPort))
        except Exception as e:
            print(e)
        server_sock.listen(10)


        while True:
            conn,addr = server_sock.accept()
            print("client connected for login/register")
            data = conn.recv(4096)
            print("DATA received:\n_____________________")
            print(data)
            print("_________________________")
            result = self.process(data)
            out = str(result).encode()
            conn.send(out)
            print(out)
            

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


def main():

    cur_threads = []
    while True:
        thread_no = random.randint(1,1000)
        
        file_server_sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
        cred_server_sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)

        file_server_sock.bind((SERVER_IP,SERVER_PORT_FILES))
        cred_server_sock.bind((SERVER_IP,SERVER_PORT_CRED))

        signal.signal(signal.SIGINT, signal_handler)
        if isInterrupted:
                print("Server shutting down!")
                break

    files = FileThread(1, "FT")
    loginRegister = LoginRegisterThread(2, "LR")

    files.start()
    loginRegister.start()
    files.join()
    loginRegister.join()

    print("Exiting the main program")

if __name__ == "__main__":
    main()