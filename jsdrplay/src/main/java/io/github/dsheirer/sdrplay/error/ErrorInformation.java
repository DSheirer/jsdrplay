package io.github.dsheirer.sdrplay.error;

import io.github.dsheirer.sdrplay.api.sdrplay_api_ErrorInfoT;
import jdk.incubator.foreign.MemoryAccess;
import jdk.incubator.foreign.MemorySegment;

/**
 * Error Information structure (sdrplay_api_ErrorInfoT)
 */
public class ErrorInformation
{
    private static final int FILE_SIZE = 256;
    private static final int FUNCTION_SIZE = 256;
    private static final int MESSAGE_SIZE = 1024;

    private String mFile;
    private String mFunction;
    private int mLine;
    private String mMessage;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public ErrorInformation(MemorySegment memorySegment)
    {
        mFile = getString(sdrplay_api_ErrorInfoT.file$slice(memorySegment), FILE_SIZE);
        mFunction = getString(sdrplay_api_ErrorInfoT.function$slice(memorySegment), FUNCTION_SIZE);
        mLine = sdrplay_api_ErrorInfoT.line$get(memorySegment);
        mMessage = getString(sdrplay_api_ErrorInfoT.message$slice(memorySegment), MESSAGE_SIZE);
    }

    private static String getString(MemorySegment memorySegment, int size)
    {
        byte[] bytes = new byte[size];

        for(int x = 0; x < size; x++)
        {
            bytes[x] = MemoryAccess.getByteAtOffset(memorySegment, x);
        }

        return new String(bytes).trim();
    }

    public String getFile()
    {
        return mFile;
    }

    public String getFunction()
    {
        return mFunction;
    }

    public int getLine()
    {
        return mLine;
    }

    public String getMessage()
    {
        return mMessage;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Error Information:").append("\n");
        sb.append("\t    File: ").append(getFile()).append("\n");
        sb.append("\tFunction: ").append(getFunction()).append("\n");
        sb.append("\t    Line: ").append(getLine()).append("\n");
        sb.append("\t Message: ").append(getMessage()).append("\n");
        return sb.toString();
    }
}
