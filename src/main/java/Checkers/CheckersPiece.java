package Checkers;
import java.util.*;

public class CheckersPiece {

	private char colour;
	private Cell position;
	private boolean isKing = false;
	private boolean exists = true;
	
	public CheckersPiece(char c) {
		this.colour = c;
	}
	
	public char getColour() {
		return this.colour;
	}
	
	public void setPosition(Cell p) {
		this.position = p;
	}
	
	public Cell getPosition() {
		return this.position;
	}
	
	public Set<Cell> getAvailableMoves(Cell[][] board) {
		//TODO: Get available moves for this piece depending on the board layout, and whether this piece is a king or not
		//How to record if the move is a capture or not? Maybe make a new class 'Move' that stores this information, along with the captured piece?
		
		Set<Cell> availableMoves = new HashSet<>();

        int[][] directions;
		// {+ is up, + is right}
        if (isKing) {
            directions = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; // Kings can move in any diagonal direction
        } else {
			if (colour == 'b') {
				directions = new int[][]{{-1, 1}, {-1, -1}};
			} else {
				directions = new int[][]{{1, 1}, {1, -1}};
			}
        }

        for (int[] dir : directions) {
            int nextRow = position.getY() + dir[0];
            int nextCol = position.getX() + dir[1];

            // Check if the next cell is within edges of the board
            if (nextRow >= 0 && nextRow < board.length && nextCol >= 0 && nextCol < board[0].length) {
                Cell nextCell = board[nextRow][nextCol];
                
				// If the next cell is empty, add it as a move
                if (nextCell.getPiece() == null || nextCell.getPiece().getExists() == false) {
                    availableMoves.add(nextCell);
                } else if (nextCell.getPiece() != null && nextCell.getPiece().getExists() == true) {
					// when the nextCell has another piece ... (same colour too)
					//  && nextCell.getPiece().getColour() != this.getColour()


					// row and column are extended in the same direction for a capture		
                    int jumpRow = nextRow + dir[0];
                    int jumpCol = nextCol + dir[1];

					// jump must still be within the edges of the board
                    if (jumpRow >= 0 && jumpRow < board.length && jumpCol >= 0 && jumpCol < board[0].length) {
                        Cell jumpCell = board[jumpRow][jumpCol];
                        if (jumpCell.getPiece() == null || jumpCell.getPiece().getExists() == false) {
                            // Add the cell after the jump as a capture move
                            availableMoves.add(jumpCell);
                        }
                    }
                }
            }
        }

        return availableMoves;
	}
	
	public void capture() {
		//capture this piece
		this.exists = false;
	}
	public boolean getExists() {
		return this.exists;
	}
	
	public void promote() {
		//promote this piece
		this.isKing = true;
	}
	
	//draw the piece
	public void draw(App app) {
		if (this.exists) {
			app.strokeWeight(5.0f);
			if (this.isKing) {
				if (colour == 'w') {
					app.fill(255);
					app.stroke(0);
				} else if (colour == 'b') {
					app.fill(0);
					app.stroke(255);
				}
				app.ellipse(position.getX()*App.CELLSIZE + App.CELLSIZE/2, position.getY()*App.CELLSIZE + App.CELLSIZE/2, App.CELLSIZE*0.8f, App.CELLSIZE*0.8f);
				app.ellipse(position.getX()*App.CELLSIZE + App.CELLSIZE/2, position.getY()*App.CELLSIZE + App.CELLSIZE/2, App.CELLSIZE*0.4f, App.CELLSIZE*0.4f);

				
				app.noStroke();

			}
			else if (!this.isKing) {
				if (colour == 'w') {
					app.fill(255);
					app.stroke(0);
				} else if (colour == 'b') {
					app.fill(0);
					app.stroke(255);
				}
				app.ellipse(position.getX()*App.CELLSIZE + App.CELLSIZE/2, position.getY()*App.CELLSIZE + App.CELLSIZE/2, App.CELLSIZE*0.8f, App.CELLSIZE*0.8f);
				app.noStroke();
			}
		}
	}
}