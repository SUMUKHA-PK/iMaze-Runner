import socket 
import pickle
import os
import io
import threading
from db import add_cred
from db import authenticate as auth
from PIL import Image
from PIL import ImageFile
ImageFile.LOAD_TRUNCATED_IMAGES = True
SERVER_IP="192.168.43.125"
SERVER_PORT_LOGIN=12346
SERVER_PORT_FILES=12345

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
        server_sock.bind((serverAddress,serverPort))
        server_sock.listen(10)
        size = 4096
        while True:
            client,addr = server_sock.accept()
            print("client connected for files")
            #this below must be changed to the image name
            file_path = "./images/some.jpg"
            directory = os.path.dirname(file_path)
            if not os.path.exists(directory):
                os.makedirs(directory)
            myfile=open('./images/some.jpg','wb')
            while True:
                data=client.recv(size)
                if not data:
                    break
                myfile.write(data)
                print("writing the file")
            myfile.close()
            client.close()
            print("done")

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
        server_sock.bind((serverAddress,serverPort))
        server_sock.listen(10)
        while True:
            conn,addr = server_sock.accept()
            print("client connected for login/register")
            data = conn.recv(4096)
            print("DATA IS BELOW:\n_____________________")
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
    print("Attempting to start the servers\n")
    
    files = FileThread(1, "FT")
    loginRegister = LoginRegisterThread(2, "LR")

    files.start()
    loginRegister.start()
    files.join()
    loginRegister.join()

    print("Exiting the main program")

if __name__ == "__main__":
    main()