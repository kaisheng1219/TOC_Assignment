package model;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Automaton {
    protected ArrayList<State> states = new ArrayList<>();
    protected ArrayList<Character> symbols = new ArrayList<>();

    public void addState(State s) {
        states.add(s);
    }
    public void addSymbol(Character c) {
        symbols.add(c);
    }

    public ArrayList<State> getStates() {
        Collections.sort(this.states);
        return this.states;
    }
    public ArrayList<Character> getSymbols() {
        Collections.sort(symbols);
        return this.symbols;
    }
    public abstract String getDefinition();
}