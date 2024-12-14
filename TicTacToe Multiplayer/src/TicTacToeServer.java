import java.io.*;
import java.net.*;

public class TicTacToeServer {
    private static final int PORT = 12345;
    private static final char EMPTY = '_';
    private static final String PLAYER_X_WINS = " wins!";
    private static final String PLAYER_O_WINS = " wins!";
    private static final String DRAW = "It's a draw!";

    private char[][] board = new char[3][3];
    private int currentPlayer;
    private int firstPlayer = 1;  // Track who plays first
    private PrintWriter out1, out2;
    private String player1Name, player2Name;
    private char player1Symbol, player2Symbol;

    public static void main(String[] args) {
        new TicTacToeServer().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Tic-Tac-Toe Server is Running...");

            // Accept player 1
            Socket player1Socket = serverSocket.accept();
            BufferedReader nameInput = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
            player1Name = nameInput.readLine();
            player1Symbol = nameInput.readLine().charAt(0); // Read the symbol (X or O)
            out1 = new PrintWriter(player1Socket.getOutputStream(), true);
            new PlayerHandler(player1Socket, 1, player1Name, player1Symbol).start();

            // Accept player 2
            Socket player2Socket = serverSocket.accept();
            nameInput = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));
            player2Name = nameInput.readLine();
            player2Symbol = nameInput.readLine().charAt(0); // Read the symbol (X or O)
            out2 = new PrintWriter(player2Socket.getOutputStream(), true);
            new PlayerHandler(player2Socket, 2, player2Name, player2Symbol).start();

            initBoard();
            currentPlayer = firstPlayer;  // Start with the designated first player
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = EMPTY;
            }
        }
    }

    private void notifyPlayers() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                sb.append(board[row][col]);
            }
        }
        out1.println(sb.toString());
        out2.println(sb.toString());

        out1.println((currentPlayer == 1) ? player1Name + "'s Turn" : player2Name + "'s Turn");
        out2.println((currentPlayer == 1) ? player1Name + "'s Turn" : player2Name + "'s Turn");
    }

    private boolean makeMove(int player, int row, int col) {
        if (currentPlayer == player && board[row][col] == EMPTY) {
            char symbol = (player == 1) ? player1Symbol : player2Symbol;
            board[row][col] = symbol;
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
            return true;
        }
        return false;
    }

    private String checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != EMPTY && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return (board[i][0] == player1Symbol) ? player1Name + PLAYER_X_WINS : player2Name + PLAYER_O_WINS;
            }
            if (board[0][i] != EMPTY && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return (board[0][i] == player1Symbol) ? player1Name + PLAYER_X_WINS : player2Name + PLAYER_O_WINS;
            }
        }
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return (board[0][0] == player1Symbol) ? player1Name + PLAYER_X_WINS : player2Name + PLAYER_O_WINS;
        }
        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return (board[0][2] == player1Symbol) ? player1Name + PLAYER_X_WINS : player2Name + PLAYER_O_WINS;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == EMPTY) return null;
            }
        }
        return DRAW;
    }

    private class PlayerHandler extends Thread {
        private Socket socket;
        private int player;
        private String playerName;
        private char playerSymbol;

        public PlayerHandler(Socket socket, int player, String name, char symbol) {
            this.socket = socket;
            this.player = player;
            this.playerName = name;
            this.playerSymbol = symbol;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] parts = inputLine.split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);

                    if (makeMove(player, row, col)) {
                        notifyPlayers();
                        String result = checkWinner();
                        if (result != null) {
                            out1.println(result);
                            out2.println(result);
                            String quitMessage = "Do you want to Quit? (Yes/No)";
                            out1.println(quitMessage);
                            out2.println(quitMessage);

                            String response1 = in.readLine();
                            String response2 = in.readLine();

                            if ("Yes".equalsIgnoreCase(response1) || "Yes".equalsIgnoreCase(response2)) {
                                System.exit(0); // Terminate the server
                            } else {
                                // Swap the starting player for the next game
                                firstPlayer = (firstPlayer == 1) ? 2 : 1;
                                initBoard();
                                currentPlayer = firstPlayer;  // Set new first player
                                notifyPlayers();
                            }
                        }
                    } else {
                        if (player == 1) {
                            out1.println("Invalid Move! It's " + player2Name + "'s Turn.");
                        } else {
                            out2.println("Invalid Move! It's " + player1Name + "'s Turn.");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}