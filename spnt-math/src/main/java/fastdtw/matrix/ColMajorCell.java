// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ColMajorCell.java

package fastdtw.matrix;

public class ColMajorCell {
	private final int col;
	private final int row;

	public int getCol() {
		return col;
	}

	public int getRow() {
		return row;
	}

	public ColMajorCell(int column, int row) {
		col = column;
		this.row = row;
	}

	public boolean equals(Object o) {
		return (o instanceof ColMajorCell) && ((ColMajorCell) o).col == col
				&& ((ColMajorCell) o).row == row;
	}

	public int hashCode() {
		return (1 << col) + row;
	}

	public String toString() {
		return "(" + col + "," + row + ")";
	}

}
