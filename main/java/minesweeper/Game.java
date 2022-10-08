package minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Game {

	private int height, width;
	private Tile[][] board;
	private int numberOfBombs;
	private boolean gameWon = false;
	private boolean gameOver = false;
	public static final int NUMBER_OF_BOMBS = 55;
	
	/* Constructor from lecture. */
	public Game(int width, int height) {
		this.height = height;
		this.width = width;
		this.board = new Tile[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				board[y][x] = new Tile(x, y);
			}
		}
	}
	
	/*
	 * Clone-constructor made ONLY for test-purposes (FileManager)
	 */
	public Game(Game game) {
		this.height = game.getHeight();
		this.width = game.getWidth();
		this.board = game.getBoard();
	}
	
	/*
	 * Checks if coordinates is a tile on the board.
	 * Method from lecture.
	 */
	public boolean isTile(int x, int y) {
		return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
	}
	
	/* 
	 * Gets tile for (x, y) position input argument.
	 * Method from lecture.
	 */
	public Tile getTile(int x, int y) {
		if (!isTile(x, y)) {
			throw new IllegalArgumentException("Coordinates out of bounds");
		}
		return this.board[y][x];
	}
	
	/*
	 * Toggles state of isFlagged on tile with position x, y.
	 */
	public void toggleIsFlagged(int x, int y) {
		if (!getTile(x, y).getIsFlagged()) {
			getTile(x, y).setIsFlagged(true);
		}else if (getTile(x, y).getIsFlagged()) {
			getTile(x, y).setIsFlagged(false);
		}		
	}

	/*
	 * Generates bombs on random locations within the boards limits. If the board
	 * location already has a bomb, or the tile on said location is open
	 * (this prevents losing on first click) a new location will be generated for the
	 * bomb.
	 */
	public void generateBombs() {
		this.numberOfBombs = 0;
		Random rnd = new Random();

		while (this.numberOfBombs < NUMBER_OF_BOMBS) {
			int x = rnd.nextInt(getWidth() - 1);
			int y = rnd.nextInt(getHeight() - 1);

			while (getTile(x, y).isBomb() || getTile(x, y).getIsOpen()) {
				x = rnd.nextInt(getWidth() - 1);
				y = rnd.nextInt(getHeight() - 1);
			}
			board[y][x].setBomb();
			this.numberOfBombs++;
		}
	}
	
	/*
	 * The bombCount field of a tile is incremented if there is a bomb in proximity 
	 * to the  tile. This method uses the updateBombCount()-method as a helper. The
	 * coordinates around the bombs that are checked are: 
	 * (-1, 1); (-1, 0); (-1, -1)
	 * (0, 1);  (BOMB);  (0, -1) 
	 * (1, 1);  (1, 0);  (1, -1)
	 */
	public void bombCount() {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				updateBombCount(x, y);
			}
		}
	}

	/* 
	 * Helper for bombCount(). This method does the actual counting, incrementing the
	 * bombCount field on tile positioned at (x, y).
	 */
	private void updateBombCount(int x, int y) {

		if (!board[y][x].isBomb()) {
			return; 
		}
		for (int row = y-1; row <= y+1; row++) {
			for (int col = x-1; col <= x+1; col++) {
				if (!isTile(col, row) || board[row][col].isBomb()) {
					continue;
				}else {
					board[row][col].incBombCount();
				}
			}
		}
	}

	/*
	 * Opens a tile. If the tile is already open, nothing happens. If the
	 * tile is a bomb, gameOver == true, and the game is lost. If the tile does not
	 * contain a bomb, the tiles around will be checked for bombs. If the tile
	 * next to the original tile does not contain a bomb and has a bombCount equal to
	 * zero, it will be opened too. 
	 */
	public void openTile(Tile tile) {		
		if (tile.getIsOpen() == true) {
			return;
		}
		if (tile.getIsOpen() == false) {
			tile.setIsOpen(true);
			tile.setIsFlagged(false);
		}
		if (tile.isBomb()) {
			gameOver = true;
			revealAllBombs();
			return;
		}
		if (tile.isEmpty()) {			
			for (int row = tile.getY()-1; row <= tile.getY()+1; row++) {
				for (int col = tile.getX()-1; col <= tile.getX()+1; col++) {
					if (!isTile(col, row) || getTile(col, row).isBomb()) {
						continue;
					}
					if (!board[row][col].getIsOpen() && tile.getBombCount() == 0 && !tile.isBomb()) {
						openTile(board[row][col]);
					}
				}
			}
		}
	}
	
	/*
	 * Method for opening up the first tiles when starting the game. It is only used
	 * once per game, on the first click.
	 */
	public void firstOpen(Tile tile) {
		for (int row = tile.getY()-1; row <= tile.getY()+1; row++) {
			for (int col = tile.getX()-1; col <= tile.getX()+1; col++) {
				if (!isTile(col, row) || getTile(col, row).isBomb()) {
					continue;
				}
				if (!board[row][col].getIsOpen() && !board[row][col].isBomb()) {
					openTile(board[row][col]);
				}
			}
		}
	}
	
	/*
	 * Reveals all tiles containing a bomb if game is lost. Used in openTile() if the
	 * tile that is opened is a bomb.
	 */
	private void revealAllBombs() {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (getTile(x, y).isBomb()) {
					getTile(x, y).setIsOpen(true);
				}
			}
		}
	}	
	
	/* 
	 * Checks if game is won. Game is won if all bombs are marked with a flag 
	 * (isFlagged) or if every tile on the board except the bombs are opened.
	 */
	public void checkGameWon() {
		List<Tile> amountFlagged = new ArrayList<>();
		List<Tile> amountOpened = new ArrayList<>();
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (board[y][x].getIsOpen() && board[y][x].getType() != '@') {
					amountOpened.add(board[y][x]);
				}
				if (board[y][x].getType() == '@' && board[y][x].getIsFlagged()) {
					amountFlagged.add(board[y][x]);
				}
			}
		}
		if (amountFlagged.size() == numberOfBombs && numberOfBombs != 0) {
			gameWon = true;
		}
		if ((getWidth() * getHeight()) - numberOfBombs == amountOpened.size()) {
			gameWon = true;
		}
	}
	
	@Override
	public String toString() {
		String string = "";
		string += String.valueOf(width);
		string += String.valueOf(height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				string += board[y][x].toString();
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				string += String.valueOf(board[y][x].getIsOpen());
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				string += String.valueOf(board[y][x].getIsFlagged());
			}
		}
		
		return string;
	}
		
	
	/* To avoid the bombCount() method running twice when loading a saved game,
	 * giving a wrong bomb count.
	 */
	public void setNumberOfBombsToOriginal() {
		this.numberOfBombs = NUMBER_OF_BOMBS; 
	}
	
	/* Getters for the Game class. */
	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}
	
	public int getNumberOfBombs() {
		return this.numberOfBombs;
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	public boolean isGameWon() {
		return gameWon;
	}
	
	public Tile[][] getBoard() {
		return board;
	}
	
	/*
	 * Setter made ONLY for test-purposes: testing the setNumberOfBombsToOriginal()-
	 * method (GameTest)
	 */
	public void setNumberOfBombs(int numberOfBombs) {
		this.numberOfBombs = numberOfBombs;
	}
}
