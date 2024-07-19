package com.tyron.code.java.protocol;

import com.tyron.code.project.model.TextPosition;

import java.util.Objects;

/**
 * Position in a text document expressed as zero-based line and character offset. A position is
 * between two characters like an 'insert' cursor in a editor.
 *
 * <p>See: <a href="https://github.com/Microsoft/language-server-protocol/blob/master/protocol.md#position">...</a>
 */
public class Position extends TextPosition {
    private final int line;
    private final int character;

    // For GSON
    public Position() {
        this.line = 0;
        this.character = 0;
    }

    public Position(int line, int character) {
        this.line = line;
        this.character = character;
    }

    public static Position createFromTextPosition(TextPosition pos) {
        if (pos instanceof Position) {
            return (Position) pos;
        }
        return new Position(pos.getLine(), pos.getCharacter());
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", line, character);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position other)) {
            return false;
        }

        return this.line == other.line && this.character == other.character;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, character);
    }
}