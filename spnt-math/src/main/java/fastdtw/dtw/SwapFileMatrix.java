// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SwapFileMatrix.java

package fastdtw.dtw;

import java.io.*;
import java.util.Random;

import fastdtw.lang.TypeConversions;

// Referenced classes of package dtw:
//            CostMatrix, SearchWindow

class SwapFileMatrix
    implements CostMatrix
{

    SwapFileMatrix(SearchWindow searchWindow)
    {
        window = searchWindow;
        if(window.maxI() > 0)
        {
            currCol = new double[(window.maxJforI(1) - window.minJforI(1)) + 1];
            currColIndex = 1;
            minLastRow = window.minJforI(currColIndex - 1);
        } else
        {
            currColIndex = 0;
        }
        minCurrRow = window.minJforI(currColIndex);
        lastCol = new double[(window.maxJforI(0) - window.minJforI(0)) + 1];
        swapFile = new File("swap" + RAND_GEN.nextLong());
        isSwapFileFreed = false;
        colOffsets = new long[window.maxI() + 1];
        try
        {
            cellValuesFile = new RandomAccessFile(swapFile, "rw");
        }
        catch(FileNotFoundException e)
        {
            throw new InternalError("ERROR:  Unable to create swap file: " + swapFile);
        }
    }

    public void put(int col, int row, double value)
    {
        if(row < window.minJforI(col) || row > window.maxJforI(col))
            throw new InternalError("CostMatrix is filled in a cell (col=" + col + ", row=" + row + ") that is not in the " + "search window");
        if(col == currColIndex)
            currCol[row - minCurrRow] = value;
        else
        if(col == currColIndex - 1)
            lastCol[row - minLastRow] = value;
        else
        if(col == currColIndex + 1)
        {
            try
            {
                if(isSwapFileFreed)
                    throw new InternalError("The SwapFileMatrix has been freeded by the freeMem() method");
                cellValuesFile.seek(cellValuesFile.length());
                colOffsets[currColIndex - 1] = cellValuesFile.getFilePointer();
                cellValuesFile.write(TypeConversions.doubleArrayToByteArray(lastCol));
            }
            catch(IOException e)
            {
                throw new InternalError("Unable to fill the CostMatrix in the Swap file (IOException)");
            }
            lastCol = currCol;
            minLastRow = minCurrRow;
            minCurrRow = window.minJforI(col);
            currColIndex++;
            currCol = new double[(window.maxJforI(col) - window.minJforI(col)) + 1];
            currCol[row - minCurrRow] = value;
        } else
        {
            throw new InternalError("A SwapFileMatrix can only fill in 2 adjacentcolumns at a time");
        }
    }

    public double get(int col, int row)
    {
//        if(row < window.minJforI(col) || row > window.maxJforI(col))
//            return (1.0D / 0.0D);
//        if(col == currColIndex)
//            return currCol[row - minCurrRow];
//        if(col == currColIndex - 1)
//            return lastCol[row - minLastRow];
//        if(isSwapFileFreed)
//            throw new InternalError("The SwapFileMatrix has been freeded by the freeMem() method");
//        cellValuesFile.seek(colOffsets[col] + (long)(8 * (row - window.minJforI(col))));
//        return cellValuesFile.readDouble();
//        IOException e;
//        e;
//        if(col > currColIndex)
//            throw new InternalError("The requested value is in the search window but has not been entered into the matrix: (col=" + col + "row=" + row + ").");
//        else
//            throw new InternalError("Unable to read CostMatrix in the Swap file (IOException)");
    	return 0;
    }

    protected void finalize()
        throws Throwable
    {
//        if(!isSwapFileFreed)
//            cellValuesFile.close();
//        swapFile.delete();
//        super.finalize();
//        break MISSING_BLOCK_LABEL_96;
//        Exception e;
//        e;
//        System.err.println("unable to close swap file '" + swapFile.getPath() + "' during finialization");
//        swapFile.delete();
//        super.finalize();
//        break MISSING_BLOCK_LABEL_96;
//        Exception exception;
//        exception;
//        swapFile.delete();
//        super.finalize();
//        throw exception;
    }

    public int size()
    {
        return window.size();
    }

    public void freeMem()
    {
        try
        {
            cellValuesFile.close();
        }
        catch(IOException e)
        {
            System.err.println("unable to close swap file '" + swapFile.getPath() + "'");
        }
        finally
        {
            if(!swapFile.delete())
                System.err.println("unable to delete swap file '" + swapFile.getPath() + "'");
        }
    }

    private static final double OUT_OF_WINDOW_VALUE = (1.0D / 0.0D);
    private static final Random RAND_GEN = new Random();
    private final SearchWindow window;
    private double lastCol[];
    private double currCol[];
    private int currColIndex;
    private int minLastRow;
    private int minCurrRow;
    private final File swapFile;
    private final RandomAccessFile cellValuesFile;
    private boolean isSwapFileFreed;
    private final long colOffsets[];

}
