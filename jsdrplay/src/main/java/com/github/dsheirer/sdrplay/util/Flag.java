package com.github.dsheirer.sdrplay.util;

/**
 * Utility for integer and byte values representing true/false flags.
 */
public class Flag
{
    public static final byte TRUE = 0x01;
    public static final byte FALSE = 0x00;

    /**
     * Evaluates the boolean value false (0x00) or true
     * @param value to evaluate
     * @return true of the value is non-zero
     */
    public static boolean evaluate(byte value)
    {
        return value != FALSE;
    }

    /**
     * Evaluates the boolean value false (0) or true
     * @param value to evaluate
     * @return true of the value is non-zero
     */
    public static boolean evaluate(int value)
    {
        return value != 0;
    }

    /**
     * Returns a byte value representing the boolean value.
     * @param value to lookup
     * @return byte flag value
     */
    public static byte of(boolean value)
    {
        return value ? TRUE : FALSE;
    }
}
