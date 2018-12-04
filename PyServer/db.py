import mysql.connector

def add_cred(credentials):
    try:
        print("add_cred called")
        server = mysql.connector.connect(
        host="localhost",
        user="root",
        password="",
        database="temp"
        )
        print("config done for mySQL");
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
    server = mysql.connector.connect(
    host="localhost",
    user="root",
    password="",
    database="temp"
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


