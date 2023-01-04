package model;

import java.util.*;

public class State implements Comparable<State> {
    private final String name;
    private final LinkedHashMap<Character, Set<State>> transitions;
    private boolean isStartState, isFinalState;

    public State (String name) {
        this.name = name;
        this.transitions = new LinkedHashMap<>();
        this.isFinalState = false;
        this.isStartState = false;
    }

    public void setAsStartState() { this.isStartState = true; }
    public void setAsFinalState() { this.isFinalState = true; }

    public boolean isStartState() { return this.isStartState; }
    public boolean isFinalState() { return this.isFinalState; }

    public void addTransition(Character symbol, State state) {
        if (this.transitions.containsKey(symbol) && !this.transitions.get(symbol).contains(state))
            this.transitions.get(symbol).add(state);
        else
            this.transitions.put(symbol, new LinkedHashSet<>(Collections.singletonList(state)));


    }

    public Set<State> removeTransition(Character c) { return this.transitions.remove(c); }
    public Set<State> getTransition(Character symbol) { return this.transitions.get(symbol); }
    public LinkedHashMap<Character, Set<State>> getTransitions() { return this.transitions; }

    @Override
    public boolean equals(Object obj) {
        State o = (State) obj;
        return this.name.equals( o.name );
    }

    @Override
    public int compareTo(State s) {
        return this.name.compareTo(s.name);
    }
    @Override
    public String toString() { return this.name; }
}
