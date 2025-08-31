/**
 * @author Ryan, Amine, Sanaa
 * 
 * A class for a row and column pair so that a pairs of rows and
 * columns can be passed together.
 */
public class Coordinates {
	private int r, c; // row and column of the coordinates
	/**
	 * 
	 * @param r - row of the coordinates
	 * @param c - column of the coordinates
	 */
	public Coordinates(int r, int c) {
		this.r = r;
		this.c = c;
	}
	
	/**
	 * 
	 * @return the row of the coordinates
	 */
	public int getR() { return r; }
	/**
	 * 
	 * @return the column of the coordinates
	 */
	public int getC() { return c; }
	/**
	 * 
	 * @param r - the new row
	 * sets the row of the coordinates to a new row
	 */
	public void setR(int r) { this.r = r; } 
	/**
	 * 
	 * @param c - the new column
	 * sets the column of the coordinates to a new column
	 */
	public void setC(int c) { this.c = c; }
	
	/**
	 * @return whether or not the other object is equal to this object
	 */
	public boolean equals(Object o) {
		if(!(o instanceof Coordinates)) return false;
		Coordinates other = (Coordinates)o;
		return this.r == other.r && this.c == other.c;
	}
	
	/**
	 * @return {row, col} representation of the coordinates
	 */
	public String toString() {
		return "{" + r + ", " + c + "}";
	}
}
