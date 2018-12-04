import mysql.connector

def add_cred(credentials):
    server = mysql.connector.connect(
    host="localhost",
    user="server_db",
    passwd="password",
    database="server_data"
    )

    username = credentials[1]
    password = credentials[2]

    mycursor = server.cursor()
    
    query = "INSERT INTO credentials VALUES(\""+username+"\",\""+password+"\")"

    mycursor.execute(query)
    server.commit()

def authenticate(credentials):
    server = mysql.connector.connect(
    host="localhost",
    user="server_db",
    passwd="password",
    database="server_data"
    )

    username = credentials[1]
    password = credentials[2]

    mycursor = server.cursor()
    
    query = "SELECT * FROM credentials WHERE(username=\""+username+"\" AND password=\""+password+"\")"

    mycursor.execute(query)

    print(query)
    result = mycursor.fetchall()

    print(result)
    print(type(mycursor.rowcount))
    if(mycursor.rowcount==0):
        return False
    else:
        return True
