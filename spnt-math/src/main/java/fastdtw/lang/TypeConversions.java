// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TypeConversions.java

package fastdtw.lang;


public class TypeConversions
{

    public TypeConversions()
    {
    }

    public static byte[] doubleToByteArray(double number)
    {
        long longNum = Double.doubleToLongBits(number);
        return (new byte[] {
            (byte)(int)(longNum >>> 56 & 255L), (byte)(int)(longNum >>> 48 & 255L), (byte)(int)(longNum >>> 40 & 255L), (byte)(int)(longNum >>> 32 & 255L), (byte)(int)(longNum >>> 24 & 255L), (byte)(int)(longNum >>> 16 & 255L), (byte)(int)(longNum >>> 8 & 255L), (byte)(int)(longNum >>> 0 & 255L)
        });
    }

    public static byte[] doubleArrayToByteArray(double numbers[])
    {
        int doubleSize = 8;
        byte byteArray[] = new byte[numbers.length * 8];
        for(int x = 0; x < numbers.length; x++)
            System.arraycopy(doubleToByteArray(numbers[x]), 0, byteArray, x * 8, 8);

        return byteArray;
    }
}
