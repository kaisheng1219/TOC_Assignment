package model;

import java.util.*;

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
        if (!this.states.isEmpty() && this.states.get(this.states.size()-1).equals(new State("Z"))) {
            State nullState = this.states.stream().filter(x -> x.toString().equals("Z")).findFirst().orElseThrow();
            this.states.remove(nullState);
            this.states.add(0, nullState);
        }
        return this.states;
    }
    public ArrayList<Character> getSymbols() {
        Collections.sort(symbols);
        return this.symbols;
    }
}