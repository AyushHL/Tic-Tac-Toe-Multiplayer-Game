# Tic-Tac-Toe-Multiplayer Game with Server-Client Architecture

A multiplayer Tic-Tac-Toe game with server-client communication and a graphical user interface (GUI) implemented in Java.

## **Features**  
- **Multiplayer Gameplay**: Play against another player via a server-client connection.  
- **Dynamic GUI**: Intuitive and interactive user interface using Java Swing.  
- **Game Logic Management**: Server manages player turns, move validation, and game outcomes (win/draw).  
- **Replay Option**: Start a new game or exit after the match ends.  

---

## **Installation**

1. Clone the repository:  
   ```bash  
   git clone https://github.com/AyushHL/Tic-Tac-Toe-Multiplayer-Game.git  
   ```

2. Open the project in NetBeans IDE.

3. Build the project:  
   - In NetBeans, open the **src** folder and navigate to **TicTacToeServer.java** and **TicTacToeClientGUI.java**.  
   - Right-click the project in the **Projects** panel and click **Build** to compile the code.

4. Start the server:  
   - Right-click **TicTacToeServer.java** in the **Projects** panel, then select **Run** to start the server.

5. Run the first client GUI:  
   - Right-click **TicTacToeClientGUI.java** in the **Projects** panel, then select **Run** to launch the first client.

6. Run the second client GUI:  
   - Right-click **TicTacToeClientGUI.java** again in the **Projects** panel and select **Run** to launch the second client.

---

## **Usage**  
- Launch the server to initialize the game and wait for client connections.  
- Each client enters their name and symbol (X or O) to join the game.  
- Both clients can now play the game via the GUI, with real-time updates for moves and outcomes.
- Replay or exit after the game concludes.

---

## **Files**  
- **TicTacToeServer.java**: Manages game logic, player turns, and board updates.  
- **TicTacToeClientGUI.java**: Provides the graphical interface for players to interact with the game.  
- **README.md**: Documentation for the project.  

---

## **Tech Stack**  
- Java Programming Language  
- Java Swing for GUI  
- Socket Programming for Networking  

---

## **Contributions**  
Contributions are welcome! If you have any ideas, improvements, or bug fixes, feel free to open an issue or submit a pull request.
