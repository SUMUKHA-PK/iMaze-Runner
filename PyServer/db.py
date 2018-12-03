import mysql.connector

def add_cred(credentials):
    server = mysql.connector.connect(
    host="localhost",
    user="server_db",
    passwd="password",
    database="server_data"
    )

    username = credentials[0]
    password = credentials[1]

    mycursor = server.cursor()

    print(username)
    print(password)
    
    query = "INSERT INTO credentials VALUES(\""+username+"\",\""+password+"\")"

    print(query)
    mycursor.execute(query)
    server.commit()
