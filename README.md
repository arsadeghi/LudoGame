# What is Ludo?
In this project a modified version of Ludo, named “[Mensch ärgere dich nicht](http://en.wikipedia.org/wiki/Mensch_%C3%A4rgere_dich_nicht)” , is implemented. This is originally a German game. In Farsi, we call it just [“Mench”](http://fa.wikipedia.org/wiki/%D9%85%D9%86%DA%86).  The game rules are quoted from its Wikipedia page :

> The game can be played by 2, 3 or 4 players – one player per board side. Each player has 4 game pieces, which are in the "out" area when the game starts, and which must be brought into the player's "home" row.
The rows are arranged in a cross position. They are surrounded and connected with a circle of fields, over which the game pieces move in clockwise direction. There are 3 fields nearest to each side of the board; the left one is the player's "start" field (highlighted with player’s color) and the middle one leads to the "home" row. This means that each game piece enters the circle at the "start" field, moves (clockwise) over the board and finally enters the "home" row. The first player with all of their pieces in their "home" row wins the game.
The players throw a dice in turn and can advance any of their pieces in the game by the thrown number of dots on the dice. Throwing a six means bringing a piece into the game (by placing one from the "out" area onto the "start" field) and throwing the dice again. If a piece is on the "start" field and there are still pieces in the "out" area, it must be moved as soon as possible. If a piece cannot be brought into the game then any other piece in the game must be moved by the thrown number, if that is possible.
Pieces can jump over other pieces, and throw out pieces from other players (into that player's "out" area) if they land on them. A player cannot throw out his own pieces though, and cannot advance further than the last field in the "home" row.

# Installation

## Requirements:
1. MySQL Community Server 5
2. Apache Tomcat 7

## Installation steps:
1. Install MySQL Community Server 5 and Apache Tomcat 7.
2. Database preparation:
  
  a. Execute the Ludo.sql script. This script will create a schema named ludo and the four tables.
  
  b.Create a database user with the access rights “SELECT, INSERT, UPDATE” for the schema 'ludo' created in the previous step. Note: Default username/password are swe681/Ludo@Swe_681 (the password is encrypted). If a user with the same username/password is created, ignore step 2.c.
  
  c. The database.property file (in /Ludo/src/main/resources) should be updated based on the created user’s properties. Note that the password is encrypted and you can use the use the encrypt method in EncryptedDataSource utility class to encrypt your password.

3. Tomcat preparation:

	a.	Insert the https port definition into the server.xml (TOMCAT_INSTALL/conf), as follows:

		< Connector port="8443" maxThreads="200" scheme="https" secure="true" SSLEnabled="true"keystoreFile="keyStorePath/.ludokeyStore" keystorePass="Swe681" clientAuth="false" sslProtocol="TLS" >
    
	b.	Copy the .ludo_keyStore  from the installation package to the path set in server.xml (keyStorePath) in the previous step.
    
   c.	Put the Ludo package in the (TOMCAT_INSTALL/webapps) and start the tomcat. (TOMCAT_INSTALL/bin/startup).

# Run
The project package name is Ludo. Hence, if the tomcat http port is (default) 8080, the project is accessible by following address:

> [http://localhost:8080/Ludo]()

Note: The http channel is automatically redirected to https (8443 default port).

Full documentation is availabe [here](https://github.com/arsadeghi/LudoGame/blob/master/Documents/ProjectReport.pdf?raw=true). 

# Assurance case
Please refer to the [accompanying documentation](https://github.com/arsadeghi/LudoGame/blob/master/Documents/ProjectReport.pdf?raw=true) that provides the arguments and justifications to show that this project fulfills the security constraints.


   


