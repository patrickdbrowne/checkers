package Checkers;

public class Cell {

	private int x;
	private int y;
	private CheckersPiece piece;
	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setPiece(CheckersPiece p) {
		this.piece = p;
		if (p != null) {
			p.setPosition(this);
		}
	}

	
	public CheckersPiece getPiece() {
		return this.piece;
	}
	
	public void draw(App app) {
		// draws an individual checkers piece onto the screen. The collection of checker pieces are in board[][]
		if (this.piece != null) 
			this.piece.draw(app);
	}
}