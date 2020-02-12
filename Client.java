/*
 * Gomoku - Mouad Douieb
 */
 
package gomoku;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import gomoku.Case.Marque;

public class Client extends JFrame implements Runnable {
	private JTextField identite;
	private JTextArea affichage;
	private JPanel panneauPlateau = new JPanel();
	private JPanel panneau2 = new JPanel();
	private Case plateau;
	private Marque monMarque;
	private Socket s;
	private DataOutputStream dout;
	private DataInputStream din;

	public enum GameState {
		PLAYING, DRAW, CROSS_WON, NOUGHT_WON
	}

	private GameState currentState;

	private Marque currentPlayer; // the current player

	public Client(String host, int port, int type, char monMarque) {
		this.setTitle("Gomoku");
		this.setSize(570, 710);
		this.setLocationRelativeTo(null);
		

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panneauPlateau.setLayout(new BorderLayout());

		this.setBackground(Color.GRAY);

		plateau = new Case();

		plateau.board = new Marque[Case.LIGNES][Case.COLS];

		plateau.addMouseListener(new EcouteurCase());
		panneauPlateau.add(plateau, BorderLayout.CENTER);

		affichage = new JTextArea(2, 20);
		affichage.setFont(new Font("Arial", Font.BOLD, 30));

		affichage.setEditable(false);
		panneau2.add(affichage);

		this.getContentPane().add(panneauPlateau);
		this.getContentPane().add(panneau2, BorderLayout.SOUTH);

		this.setVisible(true);

		this.monMarque = Marque.NOUGHT;
		affichage.setForeground(Color.GRAY);
		affichage.setText("Your Enemy Turn");

		if (monMarque == 'X' || monMarque == 'x') {
			this.monMarque = Marque.CROSS;
			affichage.setText("Your Turn");
		}

		currentPlayer = Marque.CROSS;

		this.initGame();

		try {
			s = new Socket(host, port);
			din = new DataInputStream(s.getInputStream());
			dout = new DataOutputStream(s.getOutputStream());

		} catch (Exception e) {
			System.out.println("Erreur de connextion du client\n" + e);
			e.printStackTrace();
		}

		new Thread(this).start();
	}

	public void updateGame(int rowSelected, int colSelected) {
		if (currentState == GameState.PLAYING) {
			if (rowSelected >= 0 && rowSelected < Case.LIGNES && colSelected >= 0 && colSelected < Case.COLS
					&& plateau.board[rowSelected][colSelected] == Marque.EMPTY) {
				plateau.board[rowSelected][colSelected] = currentPlayer;
				if (gagne(rowSelected, colSelected)) {
					currentState = (currentPlayer == Marque.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;

					if (monMarque == currentPlayer) {
						affichage.setText("You Won");
						affichage.setForeground(Color.GREEN);
					} else {
						affichage.setText("You Lost");
						affichage.setForeground(Color.RED);
					}

				} else if (isDraw()) {
					currentState = GameState.DRAW;
				} else {
					affichage.setForeground(Color.GRAY);

					affichage.setText("Your Enemy Turn");

					if (monMarque != currentPlayer) {
						affichage.setForeground(Color.BLACK);
						affichage.setText("Your Turn");
					}

				}

				currentPlayer = (currentPlayer == Marque.CROSS) ? Marque.NOUGHT : Marque.CROSS;
			}
		} else {
			initGame();
		}

		plateau.repaint();

	}

	public boolean gagne(int rowSelected, int colSelected) {

		int countH = 1;
		int countV = 1;
		int countD = 1;
		int countG = 1;

		for (int i = 1; i < colSelected; i++) {
			if (plateau.board[rowSelected][colSelected - i] == currentPlayer)
				countH++;
			else
				break;
		}

		for (int i = 1; i < Case.COLS - colSelected; i++)
			if (plateau.board[rowSelected][colSelected + i] == currentPlayer)
				countH++;
			else
				break;


		for (int i = 1; i < rowSelected; i++)
			if (plateau.board[rowSelected - i][colSelected] == currentPlayer)
				countV++;
			else
				break;


		for (int i = 1; i < Case.LIGNES - rowSelected; i++)
			if (plateau.board[rowSelected + i][colSelected] == currentPlayer)
				countV++;
			else
				break;

		for (int i = 1; i < 5; i++) {
			if (rowSelected - i < 0 || colSelected + i > Case.COLS - 1) break;
			if (plateau.board[rowSelected - i][colSelected + i] == currentPlayer)
				countD++;
			else
				break;
		}
		for (int i = 1; i < 5; i++) {
			if (rowSelected - i < 0 || colSelected - i < 0) break;
			if (plateau.board[rowSelected - i][colSelected - i] == currentPlayer)
				countG++;
			else
				break;
		}
		for (int i = 1; i < 5; i++) {
			if (rowSelected + i > Case.LIGNES - 1 || colSelected + i > Case.COLS - 1) break;
			if (plateau.board[rowSelected + i][colSelected + i] == currentPlayer)
				countG++;
			else
				break;
		}
		for (int i = 1; i < 5; i++) {
			if (rowSelected + i > Case.LIGNES - 1 || colSelected - i < 0) break;
			if (plateau.board[rowSelected + i][colSelected - i] == currentPlayer)
				countD++;
			else
				break;
		}
		if (countH >= 5 || countV >= 5 || countG >= 5 || countD >= 5)
			return true;

		return false;
	}

	public boolean isDraw() {
		for (int row = 0; row < Case.LIGNES; ++row) {
			for (int col = 0; col < Case.COLS; ++col) {
				if (plateau.board[row][col] == Marque.EMPTY) {
					return false; // an empty cell found, not draw, exit
				}
			}
		}
		return true; // no more empty cell, it's a draw
	}

	public void initGame() {
		for (int row = 0; row < Case.LIGNES; ++row) {
			for (int col = 0; col < Case.COLS; ++col) {
				plateau.board[row][col] = Marque.EMPTY; // all cells empty
			}
		}
		currentState = GameState.PLAYING; // ready to play

	}

	class EcouteurCase implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

			if (monMarque != currentPlayer && currentState == GameState.PLAYING)
				return;

			int mouseX = e.getX();
			int mouseY = e.getY();
			// Get the row and column clicked
			int rowSelected = mouseY / Case.TAILLE_CASE;
			int colSelected = mouseX / Case.TAILLE_CASE;

			try {

				dout.writeInt(rowSelected);
				dout.writeInt(colSelected);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				

				int ligne = din.readInt();
				int col = din.readInt();

				updateGame(ligne, col);

			}
		} catch (Exception ex) {
			System.out.println("Erreur ddans le client\n" + ex);
		}
	}

}
