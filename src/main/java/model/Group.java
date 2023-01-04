package model;

import java.util.*;

public class Group implements Cloneable {
    private static int id;
    private final String name;
    private final ArrayList<State> states;

    public Group() {
        this.states = new ArrayList<>();
        id++;
        this.name = "G" + id;
    }

    public Group(String name) {
        this.name = name;
        this.states = new ArrayList<>();
        id++;
    }

    public Group(String name, ArrayList<State> states) {
        this.name = name;
        this.states = states;
        id++;
    }

    public void addState(State s) {
        states.add(s);
    }

    public State removeState(State s) {
        return states.remove(states.indexOf(s));
    }

    public ArrayList<State> getStates() { return this.states; }

    @Override
    public String toString() {
        return states.size() == 1 ? states.get(0).toString() : this.name; 
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
