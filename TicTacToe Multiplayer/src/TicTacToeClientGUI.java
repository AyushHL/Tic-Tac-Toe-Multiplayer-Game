import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class TicTacToeClientGUI extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private String playerName;
    private String playerSymbol;
    private String opponentSymbol;
    private PrintWriter out;
    private JButton[][] buttons = new JButton[3][3];
    private String currentTurn;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String name = JOptionPane.showInputDialog("Enter Your Name:");
            String symbol = JOptionPane.showInputDialog(name + ", Choose Your Symbol (X or O):").toUpperCase();
            new TicTacToeClientGUI(name, symbol).setVisible(true);
        });
    }

    public TicTacToeClientGUI(String name, String symbol) {
        this.playerName = name;
        this.playerSymbol = symbol;
        this.opponentSymbol = (symbol.equals("X")) ? "O" : "X"; // Determine opponent's symbol
        this.currentTurn = "Wait For Opponent"; // Initially, wait for opponent's move

        setTitle("Tic-Tac-Toe - " + playerName + " (Symbol: " + playerSymbol + ") - " + currentTurn);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));

        initializeBoard();
        connectToServer();
    }

    private void initializeBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton("");
                buttons[row][col].setFont(new Font("Arial", Font.BOLD, 60));
                buttons[row][col].addActionListener(new ButtonClickListener(row, col));
                add(buttons[row][col]);
            }
        }
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(playerName); // Send the player name to the server
            out.println(playerSymbol); // Send the symbol (X or O) to the server
            listenForMessages(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages(Socket socket) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    handleServerMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleServerMessage(String message) {
        if (message.contains("wins") || message.equals("It's a draw!")) {
            JOptionPane.showMessageDialog(this, message);
            askToQuitOrReset(); // Ask if the player wants to quit or reset
        } else if (message.startsWith("Invalid Move")) {
            JOptionPane.showMessageDialog(this, message); // Show error message for invalid move
        } else if (message.length() == 9) {
            updateBoard(message);
            updateTurnIndicator(); // Update the turn indicator
        } else {
            // Update whose turn it is
            currentTurn = message;
            setTitle("Tic-Tac-Toe - " + playerName + " (Symbol: " + playerSymbol + ") - " + currentTurn);
        }
    }

    private void updateBoard(String boardState) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char symbol = boardState.charAt(row * 3 + col);
                buttons[row][col].setText(symbol == '_' ? "" : String.valueOf(symbol));
            }
        }
    }

    private void askToQuitOrReset() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Do you want to Quit?", "Game Over!", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0); // Terminate the application
        } else {
            resetBoard(); // Reset the board for a new game
        }
    }

    private void resetBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("");
            }
        }
        currentTurn = "Wait For Opponent"; // Reset the turn indicator
        setTitle("Tic-Tac-Toe - " + playerName + " (Symbol: " + playerSymbol + ") - " + currentTurn);
    }

    private void updateTurnIndicator() {
        setTitle("Tic-Tac-Toe - " + playerName + " (Symbol: " + playerSymbol + ") - " + currentTurn);
    }

    private class ButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!buttons[row][col].getText().isEmpty()) {
                JOptionPane.showMessageDialog(TicTacToeClientGUI.this, 
                    "Invalid Move! Cell Already Taken.");
                return;
            }
            out.println(row + "," + col); // Send the move to the server
        }
    }
}