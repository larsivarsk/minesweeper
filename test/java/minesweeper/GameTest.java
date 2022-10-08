package minesweeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameTest {
	
	private Game game;
	
	@BeforeEach
	private void setup() {
		game = new Game(20, 15);
	}
	
	private static void checkInvalidGetTile(int x, int y) {
		assertThrows(IllegalArgumentException.class, () -> {
			new Game(20, 15).getTile(x, y);
		});
	}
	
	private static void checkOpenTile(Tile tile, Game game) {
		if (tile.getBombCount() > 0) {
			assertTrue(tile.getIsOpen());
			assertFalse(tile.getIsFlagged());
			return;
		}
		if (tile.getBombCount() == 0) {
			for (int row = tile.getY()-1; row <= tile.getY()+1; row++) {
				for (int col = tile.getX()-1; col <= tile.getX()+1; col++) {
					if (!game.isTile(col, row)) {
						continue;
					}
					if (game.getTile(col, row).isBomb()) {
						assertFalse(game.getTile(row, col).getIsOpen());
						continue;
					}
					assertTrue(game.getTile(col, row).getIsOpen());
					assertFalse(game.getTile(col, row).getIsFlagged());
				}
			}
		}
	}
	
	@Test
	public void testConstructor() {
		assertEquals(game.getHeight(), 15);
		assertEquals(game.getWidth(), 20);
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				assertFalse(game.getTile(x, y).getIsOpen());
				assertFalse(game.getTile(x, y).getIsFlagged());
				assertEquals(game.getTile(x, y).getBombCount(), 0);
				assertEquals(game.getTile(x, y).getType(), 'e');
			}
		}
		assertFalse(game.isGameOver());
		assertFalse(game.isGameWon());
	}
	
	@Test
	public void testIsTile() {
		assertFalse(game.isTile(0, -1));
		assertFalse(game.isTile(-1, 0));
		assertFalse(game.isTile(-1, -1));
		assertFalse(game.isTile(15, 20));
		assertFalse(game.isTile(21, 10));
		assertFalse(game.isTile(20, 15));
		assertTrue(game.isTile(0, 0));
		assertTrue(game.isTile(19, 14));
		assertTrue(game.isTile(6, 8));
		assertTrue(game.isTile(18, 3));
		assertTrue(game.isTile(12, 10));
		assertTrue(game.isTile(2, 5));
	}
	
	@Test
	public void testGetTile() {
		Tile tile = new Tile(0, 0);
		assertEquals(game.getTile(10, 7).getX(), 10);
		assertEquals(game.getTile(10, 7).getY(), 7);
		assertEquals(game.getTile(0, 0).getX(), 0);
		assertEquals(game.getTile(0, 0).getY(), 0);
		assertEquals(game.getTile(5, 14).getX(), 5);
		assertEquals(game.getTile(5, 14).getY(), 14);
		assertEquals(game.getTile(11, 3).getClass(), tile.getClass());
		assertEquals(game.getTile(4, 6).getClass(), tile.getClass());
		assertEquals(game.getTile(19, 2).getClass(), tile.getClass());
		assertTrue(game.getTile(15, 5) instanceof Tile);
		assertTrue(game.getTile(7, 1) instanceof Tile);
		assertTrue(game.getTile(9, 8) instanceof Tile);
		checkInvalidGetTile(20, 15);
		checkInvalidGetTile(19, 15);
		checkInvalidGetTile(20, 14);
		checkInvalidGetTile(-1, 0);
		checkInvalidGetTile(0, -1);
		checkInvalidGetTile(-1, -1);
	}
	
	@Test
	@DisplayName("Tester at tilstanden isFlagged endres ved hvert kall på metoden")
	public void testToggleIsFlagged() {
		assertFalse(game.getTile(0, 0).getIsFlagged());
		game.toggleIsFlagged(0, 0);
		assertTrue(game.getTile(0, 0).getIsFlagged());
		game.toggleIsFlagged(0, 0);
		assertFalse(game.getTile(0, 0).getIsFlagged());
	}
	
	@Test
	@DisplayName("Tester at det genereres riktig antall bomber på forskjellige plasser over hele brettet")
	public void testGenerateBombs() {
		game.generateBombs();
		int bombCount = 0;
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				if (game.getTile(x, y).isBomb()) {
					bombCount++;
				}
			}
		}
		assertEquals(Game.NUMBER_OF_BOMBS, bombCount);
	}
	
	@Test
	@DisplayName("Tester at alle Tile-objekter har riktig bombCount, at bombCount ikke er større enn 8, at den er 0 på bomber og >0 rundt bomber")
	public void testBombCount() {
		game.generateBombs();
		game.bombCount();
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				if (game.getTile(x, y).isBomb()) {
					assertEquals(game.getTile(x, y).getBombCount(), 0);
					for (int row = game.getTile(x, y).getY()-1; row <= game.getTile(x, y).getY()+1; row++) {
						for (int col = game.getTile(x, y).getX()-1; col <= game.getTile(x, y).getX()+1; col++) {
							if (!game.isTile(col, row) || game.getBoard()[row][col].isBomb()) {
								continue;
							}else
								assertTrue(game.getTile(col, row).getBombCount() > 0);
						}
					}
				}
				if (game.getTile(x, y).isEmpty()) {
					List<Tile> testTiles = new ArrayList<>();
					for (int row = game.getTile(x, y).getY()-1; row <= game.getTile(x, y).getY()+1; row++) {
						for (int col = game.getTile(x, y).getX()-1; col <= game.getTile(x, y).getX()+1; col++) {
							if (!game.isTile(col, row)) {
								continue;
							}
							if (game.getTile(col, row).isBomb()) {
								testTiles.add(game.getTile(col, row));
							}
						}
					}
					assertFalse(game.getTile(x, y).getBombCount() > 8);
					assertEquals(game.getTile(x, y).getBombCount(), testTiles.size());
				}
			}
		}
	}
	
	@Test
	@DisplayName("Tester at en Tile åpnes riktig og at den rekursive logikken stemmer (så godt det lar seg gjøre)")
	public void testOpenTile() {
		//Initial necessities for testing the method 
		game.generateBombs();
		game.bombCount();
		Tile bombTile;
		Random rnd = new Random(987654321L);
		List<Tile> testTiles = new ArrayList<>();
		List<Tile> bombTiles = new ArrayList<>();
		while (testTiles.size() <= 100) {
			int x = rnd.nextInt(game.getWidth() - 1);
			int y = rnd.nextInt(game.getHeight() - 1);

			while (testTiles.contains(game.getBoard()[y][x])) {
				x = rnd.nextInt(game.getWidth() - 1);
				y = rnd.nextInt(game.getHeight() - 1);
			}
			testTiles.add(game.getBoard()[y][x]);
		}
		
		for (Tile tile : testTiles) {
			//Save tiles containing bombs for testing further down
			if (tile.isBomb()) {
				bombTiles.add(tile);
			}
			//Tests opening of empty tiles
			if (tile.isEmpty()) {
				game.openTile(tile);
				checkOpenTile(tile, game);
			}
		}
		//Tests opening tile containing bomb
		bombTile = bombTiles.get(rnd.nextInt(bombTiles.size()-1));
		game.openTile(bombTile);
		assertTrue(bombTile.getIsOpen());
		assertTrue(game.isGameOver());
		assertFalse(game.isGameWon());
		assertFalse(bombTile.getIsFlagged());
		//Tests revealAllBombs() method which is a helper method in openTile()
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				if (game.getTile(x, y).isBomb()) {
					assertTrue(game.getTile(x, y).getIsOpen());
					assertFalse(game.getTile(x, y).getIsFlagged());
				}
			}
		}
	}
	
	@Test
	@DisplayName("Tester firstOpen() som egentlig er et spesialtilfelle av openTile()")
	public void testFirstOpen() {
		game.generateBombs();
		game.bombCount();
		Random rnd = new Random(987654321L);
		int x = rnd.nextInt(game.getWidth() - 1);
		int y = rnd.nextInt(game.getHeight() - 1);
		while (game.getTile(x, y).isBomb()) {
			x = rnd.nextInt(game.getWidth() - 1);
			y = rnd.nextInt(game.getHeight() - 1);
		}
		game.firstOpen(game.getTile(x, y));
		assertTrue(game.getTile(x, y).getIsOpen());
		assertFalse(game.getTile(x, y).getIsFlagged());
		for (int row = game.getTile(x, y).getY()-1; row <= game.getTile(x, y).getY()+1; row++) {
			for (int col = game.getTile(x, y).getX()-1; col <= game.getTile(x, y).getX()+1; col++) {
				if (!game.isTile(col, row)) {
					continue;
				}
				if (game.getTile(col, row).isEmpty()) {
					assertTrue(game.getTile(col, row).getIsOpen());
					assertFalse(game.getTile(col, row).getIsFlagged());
				}
			}
		}
	}
	
	@Test
	@DisplayName("Tester at spillet er vunnet når alle bomber er merket, eller når alt utenom bombene er åpnet")
	public void testCheckGameWon() {
		game.generateBombs();
		game.bombCount();
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				if (game.getTile(x, y).isEmpty()) {
					game.openTile(game.getTile(x, y));
				}
			}
		}
		assertFalse(game.isGameWon());
		game.checkGameWon();
		assertTrue(game.isGameWon());
		
		Game game2 = new Game(20, 15);
		game2.generateBombs();
		game2.bombCount();
		for (int y = 0; y < game2.getHeight(); y++) {
			for (int x = 0; x < game2.getWidth(); x++) {
				if (game2.getTile(x, y).isBomb()) {
					game2.getTile(x, y).setIsFlagged(true);
				}
			}
		}
		assertFalse(game2.isGameWon());
		game2.checkGameWon();
		assertTrue(game2.isGameWon());
	}
	
	@Test
	public void testSetNumberOfBombsToOriginal() {
		assertEquals(game.getNumberOfBombs(), 0);
		game.generateBombs();
		assertEquals(game.getNumberOfBombs(), Game.NUMBER_OF_BOMBS);
		game.setNumberOfBombs(0);
		assertEquals(game.getNumberOfBombs(), 0);
		game.setNumberOfBombsToOriginal();
		assertEquals(game.getNumberOfBombs(), Game.NUMBER_OF_BOMBS);
		game.setNumberOfBombs(100);
		assertEquals(game.getNumberOfBombs(), 100);
		game.setNumberOfBombsToOriginal();
		assertEquals(game.getNumberOfBombs(), Game.NUMBER_OF_BOMBS);
		game.setNumberOfBombs(57);
		assertEquals(game.getNumberOfBombs(), 57);
		game.setNumberOfBombsToOriginal();
		assertEquals(game.getNumberOfBombs(), Game.NUMBER_OF_BOMBS);
	}
}
