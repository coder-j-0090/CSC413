package lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SourceReader
{
    private BufferedReader source;
    private int lineno = 0;
    private int position;
    private boolean isPriorEndLine = true;
    private String nextLine;

    public SourceReader(String sourceFile) throws IOException
    {
        System.out.println("Source file: "+sourceFile);
        System.out.println("user.dir: " + System.getProperty("user.dir"));
        source = new BufferedReader(new FileReader(sourceFile));
    }

    void close()
    {
        try
        {
            source.close();
        } catch (Exception e) {}
    }

    public char read() throws IOException
    {
        if (isPriorEndLine)
        {
            nextLine = source.readLine();
            if (nextLine != null)
            {
                lineno++;
                position = -1;
                System.out.println(nextLine);
            }
            isPriorEndLine = false;
        }
        if (nextLine == null)
        {
            throw new IOException();
        }
        if ( nextLine.length() == 0)
        {
            isPriorEndLine = true;
            return ' ';
        }
        position++;
        if (position >= nextLine.length())
        {
            isPriorEndLine = true;
            return ' ';
        }
        return nextLine.charAt(position);
    }

    public int getPosition()
    {
        return position;
    }

    public int getLineno()
    {
        return lineno;
    }

    public void modify(int positionT, int line)
    {
        position = positionT - 1;
        lineno = line;
        isPriorEndLine = false;
    }

    public void printFile() throws IOException
    {
        source = new BufferedReader(new FileReader(String.valueOf(source)));
        String line;
        while((line = reader.readLine())!=null)
        {
            System.out.println(line);
        }
        reader.close;
    }
}
