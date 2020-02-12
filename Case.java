/*
 * Gomoku - Mouad Douieb
 */

package gomoku;

import java.awt.*;

import javax.swing.JPanel;


public class Case extends JPanel  {

	protected static final int LIGNES = 19; // Lignes
	protected static final int COLS = 19; // Colonnes

	// constantes des différentes dimensions utilisées pour le dessin graphique
	protected static final int TAILLE_CASE = 30;
	protected static final int LARGEUR_CANVAS = TAILLE_CASE * COLS;
	protected static final int HAUTEUR_CANVAS = TAILLE_CASE * LIGNES;
	protected static final int LARGEUR_GRID = 2;
	protected static final int MOITIE_LARGEUR_GRID = LARGEUR_GRID / 2;
	// Symbols (cross/nought) are displayed inside a cell, with padding from border
	protected static final int PADDING_CASE = TAILLE_CASE / 6;
	protected static final int TAILLE_SYMBOLE = TAILLE_CASE - PADDING_CASE * 2; // largeur/hauteur
	protected static final int LARGEUR_LIGNE_SYMBOLE = 5;
	

	// Use an enumeration (inner class) to represent the seeds and cell contents
	public enum Marque {
		EMPTY, CROSS, NOUGHT
	}


	Marque[][] board; // Game board of ROWS-by-COLS cells



	public void paintComponent(Graphics g) { // invoke via repaint()
		super.paintComponent(g); // fill background
		setBackground(Color.WHITE); // set its background color

		// Draw the grid-lines
		g.setColor(Color.LIGHT_GRAY);
		for (int ligne = 1; ligne < LIGNES; ++ligne) {
			g.fillRoundRect(0, TAILLE_CASE * ligne - MOITIE_LARGEUR_GRID, LARGEUR_CANVAS - 1, LARGEUR_GRID,
					LARGEUR_GRID, LARGEUR_GRID);
		}
		for (int col = 1; col < COLS; ++col) {
			g.fillRoundRect(TAILLE_CASE * col - MOITIE_LARGEUR_GRID, 0, LARGEUR_GRID, HAUTEUR_CANVAS - 1, LARGEUR_GRID,
					LARGEUR_GRID);
		}

		// Draw the Seeds of all the cells if they are not empty
		// Use Graphics2D which allows us to set the pen's stroke
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(LARGEUR_LIGNE_SYMBOLE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // Graphics2D
																												// only
		for (int ligne = 0; ligne < LIGNES; ++ligne) {

			for (int col = 0; col < COLS; ++col) {
				int x1 = col * TAILLE_CASE + PADDING_CASE;
				int y1 = ligne * TAILLE_CASE + PADDING_CASE;
				if (board[ligne][col] == Marque.CROSS) {
					g2d.setColor(Color.RED);
					int x2 = (col + 1) * TAILLE_CASE - PADDING_CASE;
					int y2 = (ligne + 1) * TAILLE_CASE - PADDING_CASE;
					g2d.drawLine(x1, y1, x2, y2);
					g2d.drawLine(x2, y1, x1, y2);
				} else if (board[ligne][col] == Marque.NOUGHT) {
					g2d.setColor(Color.BLUE);
					g2d.drawOval(x1, y1, TAILLE_SYMBOLE, TAILLE_SYMBOLE);
				}
			}
		}
	}

	


}
