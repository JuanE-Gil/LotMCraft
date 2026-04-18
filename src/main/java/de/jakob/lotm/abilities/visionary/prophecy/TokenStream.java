package de.jakob.lotm.abilities.visionary.prophecy;

import javax.annotation.Nullable;

public class TokenStream {
    private final String[] tokens;
    private int index = 0;

    public TokenStream(String input) {
        this.tokens = input.trim().split("\\s+");
    }

    public @Nullable String peek() {
        return index < tokens.length ? tokens[index] : null;
    }

    public @Nullable String next() {
        return index < tokens.length ? tokens[index++] : null;
    }

    public boolean match(String expected) {
        if (expected.equalsIgnoreCase(peek())) {
            index++;
            return true;
        }
        return false;
    }

    public boolean isEmpty(){
        return index >= tokens.length;
    }

}
