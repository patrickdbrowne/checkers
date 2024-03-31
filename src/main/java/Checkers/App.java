package Checkers;

//import org.reflections.Reflections;
//import org.reflections.scanners.Scanners;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.awt.Font;
import java.io.*;
import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 48;
    public static int x_remove;
    public static int y_remove;
    public static final int SIDEBAR = 0;
    public static final int BOARD_WIDTH = 8;
    public static final int[] BLACK_RGB = {181, 136, 99};
    public static final int[] WHITE_RGB = {240, 217, 181};
    public static final float[][][] coloursRGB = new float[][][] {
        //default - white & black
        {
                {WHITE_RGB[0], WHITE_RGB[1], WHITE_RGB[2]},
                {BLACK_RGB[0], BLACK_RGB[1], BLACK_RGB[2]}
        },
        //green
        {
                {105, 138, 76}, //when on white cell
                {105, 138, 76} //when on black cell
        },
        //blue
        {
                {196,224,232},
                {170,210,221}
        }
	};

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE;

    public static final int FPS = 60;

	/* --------------------------------------- */
	// DATA STORAGE
	/* --------------------------------------- */
	private Cell[][] board;
	private CheckersPiece currentSelected;
	private HashSet<Cell> selectedCells;
	private HashMap<Character, HashSet<CheckersPiece>> piecesInPlay = new HashMap<>();
	private char currentPlayer = 'w';

    /* --------------------------------------- */
    /* --------------------------------------- */

    public App() {
        
    }

    /**
     * Initialise the setting of the window size.
    */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

	@Override
    public void setup() {
        frameRate(FPS);

		//Set up the data structures used for storing data in the game
		this.board = new Cell[BOARD_WIDTH][BOARD_WIDTH];

        // Hashsets are sets (arrays with no duplicates & unordered)
		HashSet<CheckersPiece> w = new HashSet<>();
        HashSet<CheckersPiece> b = new HashSet<>();
        piecesInPlay.put('w', w);
        piecesInPlay.put('b', b);

        for (int i = 0; i < board.length; i++) {
            for (int i2 = 0; i2 < board[i].length; i2++) {
                board[i][i2] = new Cell(i2,i);

                if ((i2+i) % 2 == 1) {
                    if (i < 3) {
                        //white piece
                        board[i][i2].setPiece(new CheckersPiece('w'));
                        w.add(board[i][i2].getPiece());
                    } else if (i >= 5) {
                        //black piece
                        board[i][i2].setPiece(new CheckersPiece('b'));
                        b.add(board[i][i2].getPiece());
                    }
                }
            }
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
    */
	@Override
    public void keyPressed(){

    }
    
    /**
     * Receive key released signal from the keyboard.
    */
	@Override
    public void keyReleased(){

    }

    @Override
    public void mousePressed(MouseEvent e) {
        //Check if the user clicked on a piece which is theirs - make sure only whoever's current turn it is, can click on pieces
		int x = e.getX();
		int y = e.getY();
        // int prev_x = currentSelected.getPosition().getX();
        // int prev_y = currentSelected.getPosition().getY();

		if (x < 0 || x >= App.WIDTH || y < 0 || y >= App.HEIGHT) return;
		
		Cell clicked = board[y/App.CELLSIZE][x/App.CELLSIZE];
        // System.out.println(board);
        // System.out.println(Arrays.deepToString(board));

        // for (int row=0; row<board.length; row++) {
        //     for (int col=0; col<board[0].length; col++) {
        //         System.out.print(board[row][col].getPiece());
        //     }
        //     System.out.println();
        // }

			//valid piece to click
			if (clicked.getPiece() == currentSelected) {
				// turns highlighted to clear background
                currentSelected = null;
			} 
            // System.out.println(currentSelected, clicked.getPiece());
            else {

                if (currentSelected == null && clicked.getPiece() != null && clicked.getPiece().getColour() == currentPlayer && clicked.getPiece().getExists() == true) {
                    currentSelected = clicked.getPiece();

                } else if (currentSelected != null && (clicked.getPiece() == null || clicked.getPiece().getExists() == false)) {
                    // Get the current position of the selected piece
                    int attempted_x = clicked.getX();
                    int attempted_y = clicked.getY();


                    boolean valid_move = false;

                    Set<Cell> avail = currentSelected.getAvailableMoves(board);
                    valid_move = getValidMove(avail, attempted_x, attempted_y);

                    if (valid_move) {
                        // If its a legal move
                        int prev_x = currentSelected.getPosition().getX();
                        int prev_y = currentSelected.getPosition().getY();
                        char colour = currentSelected.getColour();

                        // Move the piece to the new cell
                        clicked.setPiece(currentSelected);
                        board[prev_y][prev_x].setPiece(null); // Clear the piece from the previous cell

                        // Update the position of the piece in the CheckersPiece object, if needed
                        currentSelected.setPosition(clicked);

                        if (currentSelected.getColour() == 'b' && currentSelected.getPosition().getY() == 0) {
                            currentSelected.promote();
                        } if (currentSelected.getColour() == 'w' && currentSelected.getPosition().getY() == 7) {
                            currentSelected.promote();
                        }
                        // Reset currentSelected as the move is complete
                        currentSelected = null;

                        // currentSelected.getPosition().setPiece(null);
                        if (currentPlayer == 'w') {
                            currentPlayer = 'b';
                        } else if (currentPlayer == 'b') {
                            currentPlayer = 'w';
                        }
                        // currentSelected.setPosition(clicked);

                        if (prev_x - attempted_x == 2 || attempted_x - prev_x == 2) {
                            // indicates a jump

                            if (attempted_x > prev_x) {
                                // moves right
                                x_remove = attempted_x - 1;

                            }
                            if (attempted_x < prev_x) {
                                // moves left
                                x_remove = attempted_x + 1;
                            }
                            if (attempted_y > prev_y) {
                                // moves down
                                y_remove = attempted_y - 1;

                            }
                            if (attempted_y < prev_y) {
                                // moves up
                                y_remove = attempted_y + 1;
                            }
                            
                            // // System.out.println(piecesInPlay.get('w').size());
                            // CheckersPiece to_capture = board[y_remove][x_remove].getPiece();

                            // if (to_capture.getColour() != currentSelected.getColour()) {
                            //     // nothing happens if jumps over own piece
                            //     if (to_capture.getColour() == 'b') {
                            //         piecesInPlay.get('b').remove(board[y_remove][x_remove].getPiece());
                            //     } else {
                            //         piecesInPlay.get('w').remove(board[y_remove][x_remove].getPiece());
                            //     }
                            

                            // if you jump over someone else, it captures
                            if (board[y_remove][x_remove].getPiece().getColour() != colour) {
                                if (board[y_remove][x_remove].getPiece().getColour() == 'w') {
                                    piecesInPlay.get('w').remove(board[y_remove][x_remove].getPiece());
                                } else if (board[y_remove][x_remove].getPiece().getColour() == 'b') {
                                    piecesInPlay.get('b').remove(board[y_remove][x_remove].getPiece());
                                }
                                board[y_remove][x_remove].getPiece().capture();
                            }

                            // }
                            // System.out.println(piecesInPlay.get('w').size());
                            
                            // ...remove piece from cell specified

                        //                             //white piece
                        // board[i][i2].setPiece(new CheckersPiece('w'));
                        // w.add(board[i][i2].getPiece());
                        }
                    }
                } else {
                    currentSelected = null;
                }
                // turns clear background to highlighted bg
			}
			
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    /**
     * Draw all elements in the game by current frame. 
    */
	@Override
    public void draw() {
        this.noStroke();
		//white background
        background(WHITE_RGB[0], WHITE_RGB[1], WHITE_RGB[2]);
		//draw the board
		for (int i = 0; i < board.length; i++) {
            for (int i2 = 0; i2 < board[i].length; i2++) {
				//iterates through every cell every time before drawing
				if (currentSelected != null && board[i][i2].getPiece() == currentSelected) {
                    // when a checkers piece is selected, the background goes green

                    // highlight green if normal piece
                    this.setFill(1, (i2+i) % 2);
                    this.rect(i2*App.CELLSIZE, i*App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);

                    // currentSelected is a CheckersPiece instance. If you select on a checkers piece based on the 
                    // turn, it will return the object. If empty space is selected, or same piece is reselected, returns null
                    // TODO: draw available moves for piece to go to when selected
                    

                    for (Cell space : currentSelected.getAvailableMoves(board)) {
                        int sx = space.getX();
                        int sy = space.getY();

                        // divide by APP.cellsize?
                        // change (i2+i) %2
                        
                        this.setFill(2, 0);
                        this.rect(sx*App.CELLSIZE, sy*App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                    }
				} 
                else if ((i2+i) % 2 == 1) {
					// Draws the background of the board - fills in 'black' squares of the board
                    this.fill(BLACK_RGB[0], BLACK_RGB[1], BLACK_RGB[2]);
                    this.rect(i2*App.CELLSIZE, i*App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                    
                    if (currentSelected != null) {
                        for (Cell space : currentSelected.getAvailableMoves(board)) {
                            int sx = space.getX();
                            int sy = space.getY();

                            // divide by APP.cellsize?
                            // change (i2+i) %2
                            
                            this.setFill(2, 0);
                            this.rect(sx*App.CELLSIZE, sy*App.CELLSIZE, App.CELLSIZE, App.CELLSIZE);
                        }
                    }

				} 
                
				board[i][i2].draw(this); //draws every CIRCULAR CHECKERS PIECE (not the board itself). Each piece is in board 2D array
			}
		}
        // board[i][i2] is a Cell instance, and board contains every checkers object
        
		//check if any player has no more pieces. The winner is the player who still has pieces remaining
		if (piecesInPlay.get('w').size() == 0 || piecesInPlay.get('b').size() == 0) {
			fill(255);
			stroke(0);
			strokeWeight(5.0f);
			rect(App.WIDTH*0.19f, App.HEIGHT*0.33f, App.CELLSIZE*3.1f, App.CELLSIZE*0.92f);
			fill(200,0,200);
			textSize(24.0f*(CELLSIZE/48.0f));
			if (piecesInPlay.get('w').size() == 0) {
				text("Black wins!", App.WIDTH*0.2f, App.HEIGHT*0.4f);
			} else if (piecesInPlay.get('b').size() == 0) {
				text("White wins!", App.WIDTH*0.2f, App.HEIGHT*0.4f);
			}
		}
    }

    /**
     * Set fill colour for cell background
     * @param colourCode The colour to set
     * @param blackOrWhite Depending on if 0 (white) or 1 (black) then the cell may have different shades
     */
	public void setFill(int colourCode, int blackOrWhite) {
		this.fill(coloursRGB[colourCode][blackOrWhite][0], coloursRGB[colourCode][blackOrWhite][1], coloursRGB[colourCode][blackOrWhite][2]);
	}

    public static boolean getValidMove(Set<Cell> valid_moves, int x_coord, int y_coord) {
        // Returns if the position is valid

        // System.out.println(x_coord);
        // System.out.println(y_coord);

        for (Cell cell : valid_moves) {

            if (cell.getX() == x_coord && cell.getY() == y_coord) {
                return true;
            } 
        }

        return false;
    }
    public static void main(String[] args) {
        PApplet.main("Checkers.App");
    }
}
