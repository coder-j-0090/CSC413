package tests.helpers.lexer;

import java.util.List;

public class TestReader {
  private int lineNumber = 1;
  private int column = -1;
  private boolean atEndOfLine = false;
  private List<String> sourceLines;

  public TestReader(List<String> sourceLines) {
    this.sourceLines = sourceLines;
  }


  public void close() {
    // no-op
  }

  public char read() {
    if (this.atEndOfLine) {
      this.atEndOfLine = false;
      this.column = -1;
      this.lineNumber++;
    }
    this.column++;

    char c = '\0';

    try {
      c = sourceLines.get(lineNumber - 1).charAt(this.column);
    } catch (IndexOutOfBoundsException exception) {
      return c;
    }

    if (c == '\n') {
      this.atEndOfLine = true;
    }

    return c;
  }

  public int getColumn() {
    return this.column;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }
}
