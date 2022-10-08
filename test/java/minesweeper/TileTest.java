package minesweeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class TileTest {
	
	Tile tile;

	@BeforeEach
	public void setup() {
		tile = new Tile(0, 0);
	}
	
	private static void checkInvalidConstructor(int x, int y) {
		assertThrows(IllegalArgumentException.class, () -> {
			new Tile(x, y);
		});
	}
	
	@Test
	public void testConstructor() {
		assertEquals(tile.getX(), 0);
		assertEquals(tile.getY(), 0);
		assertEquals(tile.getType(), 'e');
		assertEquals(tile.getBombCount(), 0);	
		assertFalse(tile.getIsFlagged());
		assertFalse(tile.getIsOpen());
		checkInvalidConstructor(-1, 0);
		checkInvalidConstructor(0, -1);
		checkInvalidConstructor(-1, -1);
	}
	
	@Test
	public void testSetType() {
		tile.setType('@');
		assertEquals(tile.getType(), '@');
		assertTrue(tile.isBomb());
		tile.setType('e');
		assertEquals(tile.getType(), 'e');
		assertTrue(tile.isEmpty());
		assertThrows(IllegalArgumentException.class, () -> {
			tile.setType('a');
		});
	}
	
	@Test
	public void testSetBomb() {
		assertEquals(tile.getType(), 'e');
		tile.setBomb();
		assertEquals(tile.getType(), '@');
	}
	
	@Test
	@DisplayName("Tester setIsFlagged")
	public void testSetIsFlagged() {
		assertFalse(tile.getIsFlagged());
		tile.setIsFlagged(true);
		assertTrue(tile.getIsFlagged());
		tile.setIsFlagged(false);
		assertFalse(tile.getIsFlagged());
	}
	
	@Test
	@DisplayName("Tester setIsOpen")
	public void testSetIsOpen() {
		assertFalse(tile.getIsOpen());
		tile.setIsOpen(true);
		assertTrue(tile.getIsOpen());
		tile.setIsOpen(false);
		assertFalse(tile.getIsOpen());
	}
	
	@Test
	@DisplayName("Tester minetelleren som gir tiles med bomber rundt et tall")
	public void testIncBombCount() {
		assertEquals(tile.getBombCount(), 0);
		tile.incBombCount();
		assertEquals(tile.getBombCount(), 1);
		tile.incBombCount();
		tile.incBombCount();
		tile.incBombCount();
		tile.incBombCount();
		tile.incBombCount();
		assertEquals(tile.getBombCount(), 6);
		tile.incBombCount();
		tile.incBombCount();
		assertThrows(IllegalStateException.class, () -> {
			tile.incBombCount();
		});
	}
}
