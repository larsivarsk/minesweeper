package minesweeper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;


public class FileManagerTest {
	
	private Game game1;
	private FileManager fm;
	
	
	private void setUpBoard() {
		game1 = new Game(20, 15);
		game1.generateBombs();
		game1.bombCount();
		Random rnd = new Random(987654321L);
		int counter = 0;
		while (counter <= 100) {
			int x = rnd.nextInt(game1.getWidth() - 1);
			int y = rnd.nextInt(game1.getHeight() - 1);

			if (game1.getTile(x, y).isEmpty() && !game1.getTile(x, y).getIsOpen()) {
				game1.openTile(game1.getTile(x, y));
				counter++;
			}else if (game1.getTile(x, y).isBomb() && !game1.getTile(x, y).getIsOpen()) {
				game1.getTile(x, y).setIsFlagged(true);
				counter++;
			}
		}	
	}
	
	@BeforeEach 
	private void setup() {
		setUpBoard();
		fm = new FileManager();
	}
	
	@Test
	@DisplayName("Tester lagring av spill til fil")
	public void testWriteGameToFile() throws IOException {
		Game game2 = new Game(game1);
		try {
			fm.writeGameToFile("test-game1", game1);
		}catch (FileNotFoundException e) {
			fail("Could not save game");
		}
		
		try {
			fm.writeGameToFile("test-game2", game2);
		}catch (FileNotFoundException e) {
			fail("Could not save game");
		}
		
		byte[] game1Bytes = null;
		byte[] game2Bytes = null;
		try {
			game1Bytes = Files.readAllBytes(FileManager.getFullPath("test-game1"));
		}catch (IOException e) {
			fail("Could not load game");
		}
		try {
			game2Bytes = Files.readAllBytes(FileManager.getFullPath("test-game2"));
		}catch (IOException e) {
			fail("Could not load game");
		}		
		assertNotNull(game1Bytes);
		assertNotNull(game2Bytes);		
		assertTrue(Arrays.equals(game1Bytes, game2Bytes));
		
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		
		try {
			br1 = new BufferedReader(new FileReader(FileManager.getFullPath("test-game1").toString()));
		}catch (FileNotFoundException e) {
			fail("Something went wrong");
		}
		try {
			br2 = new BufferedReader(new FileReader(FileManager.getFullPath("test-game2").toString()));
		}catch (FileNotFoundException e) {
			fail("Something went wrong");
		}
		assertEquals(String.valueOf(game1.getWidth()), br1.readLine());
		assertEquals(String.valueOf(game1.getHeight()), br1.readLine());
		assertEquals(String.valueOf(game2.getWidth()), br2.readLine());
		assertEquals(String.valueOf(game2.getHeight()), br2.readLine());
		String board1 = "";
		String board2 = "";
		for (int y = 0; y < game1.getHeight(); y++) {
			for (int x = 0; x < game1.getWidth(); x++) {
				board1 += game1.getTile(x, y).toString();
				board2 += game2.getTile(x, y).toString();
			}
		}
		assertEquals(String.valueOf(board1), br1.readLine());
		assertEquals(String.valueOf(board2), br2.readLine());
		
		while (br1.readLine() != null && br2.readLine() != null) {
			assertEquals(br1.readLine(), br2.readLine());
		}		
	}
	
	@Test
	@DisplayName("Tester overskriving av allerede eksisterende fil")
	public void testOverWriteFile() {
		try {
			fm.writeGameToFile("test-game1", game1);
		}catch (FileNotFoundException e) {
			fail("Could not save game");
		}
		
		byte[] firstBytes = null;
		try {
			firstBytes = Files.readAllBytes(FileManager.getFullPath("test-game1"));
		}catch (IOException e) {
			fail("Could not save game");
		}
		assertNotNull(firstBytes);
		
		Game game2 = new Game(20, 15);
		Random rnd = new Random(987654321L);
		int x = rnd.nextInt(20);
		int y = rnd.nextInt(15);
		game2.firstOpen(game2.getTile(x, y));
		try {
			fm.writeGameToFile("test-game1", game2);
		}catch (FileNotFoundException e) {
			fail("Could not save game");
		}
		
		byte[] secondBytes = null;
		try {
			secondBytes = Files.readAllBytes(FileManager.getFullPath("test-game1"));
		}catch (IOException e) {
			fail("Could not save game");
		}
		assertNotNull(secondBytes);
		assertFalse(Arrays.equals(firstBytes, secondBytes));
	}

	@Test
	@DisplayName("Tester lesing av fil som ikke eksisterer")
	public void testFileNotExist() {
		assertThrows(FileNotFoundException.class, () -> 
		game1 = fm.readGameFromFile("non-existing"));
	}
	
	@Test
	@DisplayName("Spill et spill og lagre som 'filename' (default) for at testen skal fungere. Tester lesing av spillet til fil")
	public void testReadGameFromFile() {
		Game game2 = null;
		try {
			game2 = fm.readGameFromFile("filename");
		} catch (FileNotFoundException e) {
			fail("Was not able to load game");
		}
		assertEquals(game2.getWidth(), 20);
		assertEquals(game2.getHeight(), 15);	
		assertFalse(game2.isGameOver());
		assertFalse(game2.isGameWon());
		int numberOfBombs = 0;
		for (int y = 0; y < game2.getHeight(); y++) {
			for (int x = 0; x < game2.getWidth(); x++) {
				if (game2.getTile(x, y).isBomb()) {
					numberOfBombs++;
				}
			}
		}
		assertEquals(Game.NUMBER_OF_BOMBS, numberOfBombs);
	}
		

	@AfterAll
	static void deleteTestFiles() {
		File testFile1 = new File(FileManager.getFullPath("test-game1").toString());
		testFile1.delete();
		File testFile2 = new File(FileManager.getFullPath("test-game2").toString());
		testFile2.delete();
	}
}
