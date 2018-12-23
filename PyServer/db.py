import mysql.connector

def add_cred(credentials):
    # Connects to the database, creats a new entry based on the credentials of the new user
    try:
        print("add_cred called")
        server = mysql.connector.connect(
        host="localhost",
        user="server_db",
        password="password",
        database="server_data"
        )
        print("config done for mySQL")
        username = credentials[1]
        password = credentials[2]

        mycursor = server.cursor()

        query = "INSERT INTO credentials VALUES(\""+username+"\",\""+password+"\")"
        print("INSERTING INTO DATABASE")

        mycursor.execute(query)
        server.commit()
    except Exception as e:
        print(e)
        
def authenticate(credentials):
    # Checks whether such a user exists in the database
    server = mysql.connector.connect(
    host="localhost",
    user="server_db",
    password="password",
    database="server_data"
    )

    username = credentials[1]
    password = credentials[2]

    mycursor = server.cursor()
    
    query = "SELECT * FROM credentials WHERE(username=\""+username+"\" AND password=\""+password+"\")"

    mycursor.execute(query)

    result = mycursor.fetchall()
    
    if(mycursor.rowcount==0):
        return False
    else:
        return True


