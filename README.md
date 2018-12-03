# Android application

Goals of this application is to implement all kinds of technologies I have a grip of till now and possibly create an useful application. 

It has the following technologies:
* Uses JAVA for the android application : Login activity, registration activity, creates sockets and connects to the server.
* Uses Python to host a server : Creates a server, checks whether the credentials match the existing ones in the database, connects to a MySQL database.
* MySQL : This has all the credentials of the users existing and using the application.  

Steps to reproduce:
* Run the python script in Server directory.
* Use android studio to install the application.
* Ensure both devices are on the same host and update this IP in both ```LoginActivity``` and in ```server.py```
* Setup a MySQL server with table ```credentials``` withe fields ```username``` (as PK) and ```password```
