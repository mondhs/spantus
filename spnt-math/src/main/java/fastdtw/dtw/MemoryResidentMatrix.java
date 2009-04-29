// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MemoryResidentMatrix.java

package fastdtw.dtw;


// Referenced classes of package dtw:
//            CostMatrix, SearchWindow

class MemoryResidentMatrix
    implements CostMatrix
{

    MemoryResidentMatrix(SearchWindow searchWindow)
    {
        window = searchWindow;
        cellValues = new double[window.size()];
        colOffsets = new int[window.maxI() + 1];
        int currentOffset = 0;
        for(int i = window.minI(); i <= window.maxI(); i++)
        {
            colOffsets[i] = currentOffset;
            currentOffset += (window.maxJforI(i) - window.minJforI(i)) + 1;
        }

    }

    public void put(int col, int row, double value)
    {
        if(row < window.minJforI(col) || row > window.maxJforI(col))
        {
            throw new InternalError("CostMatrix is filled in a cell (col=" + col + ", row=" + row + ") that is not in the " + "search window");
        } else
        {
            cellValues[(colOffsets[col] + row) - window.minJforI(col)] = value;
            return;
        }
    }

    public double get(int col, int row)
    {
        if(row < window.minJforI(col) || row > window.maxJforI(col))
            return (1.0D / 0.0D);
        else
            return cellValues[(colOffsets[col] + row) - window.minJforI(col)];
    }

    public int size()
    {
        return cellValues.length;
    }

    private static final double OUT_OF_WINDOW_VALUE = (1.0D / 0.0D);
    private final SearchWindow window;
    private double cellValues[];
    private int colOffsets[];
}
