# Chess
This is a chess application that supports single-player and multiplayer. It includes features such as annotations, undoing moves, saving and loading to/from files, and multiplayer chat.

I created the base game, annotations, saving, and sound myself. The multiplayer support and GUI were created during a high school AP CS project along with Amine and Sanaa.

How To Play
-
*Make sure to open the folder containing the code, and not any parent folder

Playing single-player: 
1. Run Coordinator.java

Playing multiplayer:
- Server setup
    1. Run getIP.bat (Windows) or getIP.sh (Mac)
    2. Run Server.java in the terminal with a port number (ex. "java Server 1234")
- Player connection
    1. Run Coordinator.java in the terminal with the IP address, port number, username, and color (0 for white, 1 for black) in that order (ex. "java Coordinator 127.0.0.1 1234 RH809 0")
