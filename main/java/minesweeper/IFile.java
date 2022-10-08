package minesweeper;

import java.io.FileNotFoundException;

public interface IFile {
	
	void writeGameToFile(String filename, Game game) throws FileNotFoundException;
	
	Game readGameFromFile(String filename) throws FileNotFoundException;
	
	}