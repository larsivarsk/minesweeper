package minesweeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class FileManager implements IFile {
	
	/*
	 * getUserFolderPath() makes path in the local user home of the computer running
	 * the application. ensureUserFolder() creates the path of getUserFolderPath() if
	 * it does not exist. getFullPath() simply returns the Path object from 
	 * getUserFolderPath() + name of the file. Methods getUserFolderPath(), 
	 * getFullPath() and ensureUserFolder() from TodoList example.
	 */
	private static Path getUserFolderPath() {
        return Path.of(System.getProperty("user.home"), "tdt4100", "minesweeper");
    }
	
	public static Path getFullPath(String filename) {
	        return getUserFolderPath().resolve(filename + ".txt");
	}
	 
	private boolean ensureUserFolder() {
	        try {
	            Files.createDirectories(getUserFolderPath());
	            return true;
	        } catch (IOException ioe) {
	        	System.out.println("Something went wrong...");
	            return false;
	        }
	}
	
	/*
	 * Uses the ensureUserFolder() method to create the wanted destination of saved
	 * file if the destination does not exist. Writes height and width of the game, 
	 * type of every Tile (bomb or empty), if Tile is open and if Tile is flagged to
	 * a text file.
	 */
	@Override
	public void writeGameToFile(String filename, Game game) throws FileNotFoundException {
		ensureUserFolder();
		try (PrintWriter printWriter = new PrintWriter(getFullPath(filename).toString())) {
			printWriter.println(game.getWidth());
			printWriter.println(game.getHeight());	
			
			//Bomb or empty
			for (int y = 0; y < game.getHeight(); y++) {
				for (int x = 0; x < game.getWidth(); x++) {
					printWriter.print(game.getTile(x, y).getType());
				}
			}
			printWriter.println();
			//Open or not
			for (int y = 0; y < game.getHeight(); y++) {
				for (int x = 0; x < game.getWidth(); x++) {
					printWriter.println(game.getTile(x, y).getIsOpen());
				}
			}
			printWriter.println();
			//Flagged or not
			for (int y = 0; y < game.getHeight(); y++) {
				for (int x = 0; x < game.getWidth(); x++) {
					printWriter.println(game.getTile(x, y).getIsFlagged());
				}
			}
		}
	}
	
	/*
	 * This method locates the file with given filename in the path the file is saved
	 * to. The Scanner reads the text file, which is the saved state of the game,
	 * and instantiates a new Game object with the information from the text file.
	 * The method then returns the game.
	 */
	@Override
	public Game readGameFromFile(String filename) throws FileNotFoundException {
		try (Scanner scanner = new Scanner(new File(getFullPath(filename).toString()))) {
			int width = scanner.nextInt();
			int height = scanner.nextInt();
			Game game = new Game(width, height);	
			
			scanner.nextLine();
			
			String board = scanner.next();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					char type = board.charAt(y*width + x);
					game.getTile(x, y).setType(type);
				}
			}
			scanner.nextLine();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					boolean open = scanner.nextBoolean();
					game.getTile(x, y).setIsOpen(open);
				}
			}
			scanner.nextLine();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					boolean flagged = scanner.nextBoolean();
					game.getTile(x, y).setIsFlagged(flagged);
				}
			}
			game.bombCount();
			game.setNumberOfBombsToOriginal();
			return game;
		}
	}
	
}
