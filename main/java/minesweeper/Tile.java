package minesweeper;


public class Tile {

	private char type;
	private int x, y;
	private int bombCount;
	private boolean isFlagged;
	private boolean isOpen;
	
	public Tile(int x, int y) {
		if (x < 0 || y < 0) {
			throw new IllegalArgumentException("Tile can not have negative coordinate input");
		}
		this.x = x;
		this.y = y;
		this.type = 'e';
		this.bombCount = 0;
		isFlagged = false;
		isOpen = false;
		
	}

	public void setBomb() {
		this.type = '@';
	}
	
	public void setType(char type) {
		if (type != '@' && type != 'e') {
			throw new IllegalArgumentException("Type can only be '@' or 'e'");
		}
		this.type = type;
	}
	
	public void setIsFlagged(boolean isFlagged) {
		this.isFlagged = isFlagged;
	}
	
	public void setIsOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
	public boolean isEmpty() {
		return type == 'e';
	}
	
	public boolean isBomb() {
		return type == '@';
	}
	
	public boolean getIsOpen() {
		return isOpen;
	}
	
	public boolean getIsFlagged( ) {
		return isFlagged;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public char getType() {
		return type;
	}
	
	public int getBombCount() {
		return bombCount;
	}
	
	public void incBombCount() {
		if (bombCount >= 8) {
			throw new IllegalStateException("bombCount can not exceed maximum number of surrounding tiles.");
		}
		this.bombCount += 1;
	}
	
	@Override
    public String toString() {
        switch (type) {
            case 'e':
                return "e";
            case '@':
                return "@";
            default:
                return " ";
        }
    }
	
}
