import socket 

server_sock = socket.socket(socket.AF_INET,socket.SOCK_STREAM)

serverAddress = "192.168.43.10"
serverPort = 12345

server_sock.bind((serverAddress,serverPort))

s.listen(10)

while(1):
    conn,addr = s.accept()
    