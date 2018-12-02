import socket 
import pickle

server_sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)

serverAddress = "192.168.43.10"
serverPort = 12345

server_sock.bind((serverAddress,serverPort))

server_sock.listen(10)

while(1):
    conn,addr = server_sock.accept()
    data = conn.recv(4096)
    result = process(data)
    out = pickle.dumps(result)
    conn.send(out)
    print(out)

def process(data):
    # Basically this must check the hash of the password (currently just match strings and return a value)
    return true