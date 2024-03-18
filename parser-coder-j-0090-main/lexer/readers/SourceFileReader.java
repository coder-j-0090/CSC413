package lexer.readers;

public class SourceFileReader
{
    private SourceFileReader srf;
    private int gc;
    private int lineNum;

    public AbsolutePath SourceFileReader()
    {

    }
    public SourceFileReader read()
    {
        return this.srf;
    }

    public int getColumn()
    {
        return this.gc;
    }
    public int getLineNumber()
    {
        return this.lineNum;
    }
}
