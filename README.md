# iMaze Runner

This is not any plain old android application. Its a dash of all the technologies that are necessary to build an app from the ground up, a server, a frontend android application and a backend database to store user credentials.
<br>
<br>
**The server :** 
<br>
This is written in python(just because its easy, wish to write an ERLANG server soon). 
<br>
**Key features -**
*  This is a basic server that registers users, checks whether an user exists and provides access on providing registered credentials, supports image transfer once a user is logged in and also can run any scripts(currently aimed at AI and Image Processing) after the image is transferred from the mobile device.
* The server listens on one port for incoming connections, on receipt of a connection, determines what functionality it has to serve, and then launches a separate thread to continue in processing that functionality while the main thread continues listening. (The actual way how servers work) 
* The value of the server IP is not hardcoded, it has a feature which allows the client to ping and the server responds so that the client can register a running server.
<br>
<br>

**The database :** Is a MySQL database that holds all the registered user credentials. Supports hashed values upto 256 bits. 
<br>
<br>

**The frontend application :** 
<br>
This is an android application written using JAVA, Android Studio.
<br>
**Key features:**
* This provides clear interfaces to user login, registration, sending images once the user is logged in and also dedicated buttons to run scripts in the server system.
* The client app need not have a preset value of Server IP in it but can search for servers in the same subnet. 
<br>
<br>

**Future ideas :** <br>
* Provide hashing of all username and passwords before they even get into the sockets.
* Add a module of funtionalities (AI and Image Proccessing) that the app can run and provide results for.
* ~~Make the application search for the IP of the server (DHCP style).~~
* OAuth and account confirmation features.
<br>
<br>
** Any contributions or ideas that can be implemented are welcome **
<br>
<br>
Disclaimer : This is just a side project undertaken so that I can apply what I've learnt and also explore new things and figure out how servers and other features are implemented in real life.
