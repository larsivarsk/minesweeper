package minesweeper;


import java.io.FileNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.image.Image ;
import javafx.scene.image.ImageView;


public class GameController {
	
	private Game game;
	private FileManager fileManager = new FileManager();
	
	@FXML Text gameWonText = new Text();
	@FXML Text gameLostText = new Text();
	
	@FXML GridPane board;
	
	@FXML TextField filename;
	
	@FXML Text fileNotFoundMessage;
	@FXML Text fileCantSaveMessage;
	
	/* Initializes game. */
	@FXML
	private void initialize() {
		game = new Game(20, 15);
		drawBoard();
	}
	
	/*
	 * Initializes new game if the player would want to restart by pushing 
	 * "New Game" button.
	 */
	@FXML
	private void handleOnNewGame() {
		initialize();
		fileNotFoundMessage.setVisible(false);
		fileCantSaveMessage.setVisible(false);
	}
	
	/*
	 * Saves game by writing game state to file and saving it in designated
	 * folder(s).
	 */
	@FXML
	private void handleOnSaveGame() {
		if (game.isGameOver() || game.isGameWon()) {
			fileCantSaveMessage.setVisible(true);
		}else {
			try {
				fileManager.writeGameToFile(getFilename(), game);
				fileNotFoundMessage.setVisible(false);
			}catch (FileNotFoundException e) {
				fileNotFoundMessage.setVisible(true);
			}
		}
	}
	
	/*
	 * Loads game if the filename input is correct/exists. Method more or less from
	 * lecture.
	 */
	@FXML
	private void handleOnLoadGame() {
		try {
			this.game = fileManager.readGameFromFile(getFilename());
			fileNotFoundMessage.setVisible(false);
		}catch (FileNotFoundException e) {
			fileNotFoundMessage.setVisible(true);
		}
		fileCantSaveMessage.setVisible(false);
		drawBoard();
	}
	
	/* 
	 * Gets the filename input from the TextField in FXML. If there is no filename
	 * entered, the default is saving the game as "filename". Method from lecture. 
	 */
	 private String getFilename() {
	    	String filename = this.filename.getText();
	    	if (filename.isEmpty()) {
	    		filename = "filename";
	    	}
	    	return filename;
	    }
	
	/*
	 * Methods for handling left- and right clicks. The first right click will 
	 * generate bombs so that the player will not loose on first click. Left click
	 * toggles marker for bomb.
	 */
	private void handleOnLeftClick(int x, int y) {
		if (game.getNumberOfBombs() == 0) {
			game.getTile(x, y).setIsOpen(true);
			game.generateBombs();
			game.bombCount();
			game.firstOpen(game.getTile(x, y));
		}
		game.openTile(game.getTile(x, y));
		game.checkGameWon();
		drawBoard();
	}	
	
	private void handleOnRightClick(int x, int y) {
		if (!game.getTile(x, y).getIsOpen()) {
			game.toggleIsFlagged(x, y);
			game.checkGameWon();
			drawBoard();
		}
	}	
	
	/* 
	 * Creates board by adding Pane objects to the GridPane board. The panes are
	 * are enabled by the setOnMouseClicked() method, and the GridPane coordinates
	 * are used to locate the panes when the mouse is clicked.
	 */
	private void createBoard() {
		board.getChildren().clear();
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				Pane pane = new Pane();
				board.add(pane, x, y);
				pane.setOnMouseClicked(e -> {
		            if (e.getButton() == MouseButton.PRIMARY && !game.isGameWon() && !game.isGameOver()) {
		            	handleOnLeftClick(GridPane.getColumnIndex(pane), GridPane.getRowIndex(pane));
		            }else if (e.getButton() == MouseButton.SECONDARY && !game.isGameWon() && !game.isGameOver()) {
		            	handleOnRightClick(GridPane.getColumnIndex(pane), GridPane.getRowIndex(pane));
		            }else {
		            	return;
		            }
		        });	
			}
		}
	}
		
	/* 
	 * The base of the method is for coloring the board and setting the 
	 * "Game Won/Over" text. It also sets the number of bombs surrounding an open 
	 * tile as text. drawBoard() uses createBoard(), to keep the text objects up to 
	 * date, especially the 'B' for flagging. Method is from lecture. Some of the 
	 * content in this method is from lecture.
	 */
	private void drawBoard() {
		createBoard();
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				board.getChildren().get(y*game.getWidth() + x).setStyle("-fx-border-color:#7b68ee; "
						+ "-fx-border-width:1px; -fx-background-color: " + getTileColor(game.getTile(x, y)) + ";");
				if (game.getTile(x, y).getIsOpen() && game.getTile(x, y).getBombCount() != 0) {
					Text bombCount = new Text();
					bombCount.setText(game.getTile(x, y).getBombCount()+"");
					bombCount.setStyle("-fx-font-size: 25px");
					bombCount.setFill(Color.BLACK);
					bombCount.setTranslateX(12.0);
					board.add(bombCount, x, y);
				}
				if (game.getTile(x, y).getIsFlagged()) {
					Text text = new Text();
					text.setText("B");
					text.setFill(Color.WHITE);
					text.setTranslateX(12.0);
					text.setStyle("-fx-font-size: 25px");
					text.setMouseTransparent(true);
					board.add(text, x, y);
					/*
					 * For flags instead of "B". Takes longer to update board. 
					 * Comment out text and uncomment image to try
					 */
//					Image image = new Image(getClass().getResource("flag.png").toString(), true);
//					ImageView iv = new ImageView(image);
//					iv.setFitWidth(30.0);
//					iv.setFitHeight(30.0);
//					iv.setTranslateX(2.0);
//					iv.setMouseTransparent(true);
//					board.add(iv, x, y);
				}
			}
		}
		if(game.isGameWon()) {
			gameWonText.setText("Congratulations!");
			gameWonText.setStyle("-fx-font: 80px Tahoma;\r\n"
					+ "    -fx-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, orangered 0%, yellow 50%);\r\n"
					+ "    -fx-stroke: black;\r\n"
					+ "    -fx-stroke-width: 2;");
			gameWonText.setTranslateX(67.0);
			gameWonText.setTranslateY(230.0);
			board.getChildren().add(gameWonText);
		}else if(game.isGameOver()) {
			displayBombs();
			gameLostText.setText("You lost!");
			gameLostText.setStyle("-fx-font: 100px Tahoma;\r\n"
					+ "    -fx-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, orangered 0%, yellow 50%);\r\n"
					+ "    -fx-stroke: black;\r\n"
					+ "    -fx-stroke-width: 2;");
			gameLostText.setTranslateX(165.0);
			gameLostText.setTranslateY(230.0);
			board.getChildren().add(gameLostText);
		}
	}
	
	/*
	 * Helper method for drawBoard(). If the game is lost, this method will set 
	 * bomb icons on every tile containing a bomb. 
	 */
	private void displayBombs() {
		for (int y = 0; y < game.getHeight(); y++) {
			for (int x = 0; x < game.getWidth(); x++) {
				if (game.getTile(x, y).isBomb()) {	
					Image image = new Image(getClass().getResource("smiley.png").toString(), true);
					ImageView iv = new ImageView(image);
					iv.setFitWidth(35.0);
					iv.setFitHeight(35.0);
					board.add(iv, x, y);
				}
			}
		}
	}
	
	/* Gives tiles color based on if they are open or not. Method from lecture. */
	private String getTileColor(Tile tile) {
	  	if (tile.getIsOpen()) {
	  		return "#b0e0e6";
	 	} 
	  	else {
			return "#000080";
	 	}
	}
	
}
