package util;

import model.*;

import java.util.*;
import java.util.stream.IntStream;

public class GrammarConverter {
    private final String[] lines;
    private final NFA nfa;
    private final ArrayList<State> states;
    private static final ArrayList<String> availableStateNames = new ArrayList<>();

    public GrammarConverter(NFA nfa, String grammar) {
        this.nfa = nfa;
        this.lines = grammar.split("\\R");
        this.states = nfa.getStates();
        this.setAllPossibleStateName();
        this.produce();
    }

    public String getLongForm() {
        StringBuilder longFormSb = new StringBuilder();
        for (State state : states) {
            longFormSb.append(state).append(" ➞ ");
            LinkedHashMap<Character, Set<State>> transitions = state.getTransitions();
            if (transitions == null || transitions.isEmpty())
                longFormSb.append("Ɛ\n");
            else {// 'a' : [B, C], 'b' : [C], 'Ɛ' : [C, A] ==> aB | aC | bC | C | A
                Iterator<Map.Entry<Character, Set<State>>> iterator = transitions.entrySet().iterator();
                while (iterator.hasNext()) {
                    // Get the next entry in the map
                    Map.Entry<Character, Set<State>> entry = iterator.next();
                    ArrayList<State> toStates = new ArrayList<>(entry.getValue());
                    for (int i = 0; i < toStates.size(); i++) {
                        if (!iterator.hasNext() && i == toStates.size()-1)
                            longFormSb.append(entry.getKey()).append(toStates.get(i));
                        else
                            longFormSb.append(entry.getKey()).append(toStates.get(i)).append(" | ");
                    }
                }
                if (state.isFinalState())
                    longFormSb.append(" | Ɛ");
                longFormSb.append("\n");
            }
        }
        return longFormSb.toString();
    }

    public static ArrayList<String> getAvailableStateNames() {
        return availableStateNames;
    }
    
    private void setAllPossibleStateName() {
        IntStream.rangeClosed('A', 'Y').mapToObj(x -> (char) x).forEach(x -> availableStateNames.add(String.valueOf(x)));
        for (String line : lines) {
            String lhs = line.split("➞")[0];
            availableStateNames.remove(lhs);
        }
    }

    private void produce() {
        for (int i = 0; i < lines.length; i++) {
            String[] halfSplitted = lines[i].split("➞");
            System.out.println(Arrays.toString(halfSplitted));
            State state = getStateIfPresentElseGetNew(halfSplitted[0]);

            if (i == 0)
                state.setAsStartState();

            String[] transitions = halfSplitted[1].split("\\|");
            for (String transition : transitions)
                produceByRule(state, transition);
        }
        this.setAllPossibleStateName();
        this.setPossibleFinalStates();
    }

    private void produceByRule(State variable, String grammarRHS) {
        // 1. S -> X; X can be {alphabet, Ɛ, State}
        if (grammarRHS.length() == 1)
            rule1(variable, grammarRHS);
        // 2. S -> a...
        else
            rule2(variable, grammarRHS);
    }

    private void rule1(State variable, String grammarRHS) {
        // S -> x convert into S -> xQ  ; Note: Ɛ and State are uppercase letters
        if (!Character.isUpperCase(grammarRHS.charAt(0))) {
            State newState = new State(getNewStateName());
            // Add Transition of 'x' to new State
            variable.addTransition(grammarRHS.charAt(0), newState);
            newState.setAsFinalState();
            if (!nfa.getSymbols().contains(grammarRHS.charAt(0)))
                nfa.addSymbol(grammarRHS.charAt(0));
            nfa.addState(newState);
        }
        // S -> epsilon
        else if (grammarRHS.charAt(0) == 'Ɛ') //
            variable.setAsFinalState();
        // S -> State
        else {
            State toState = getStateIfPresentElseGetNew(grammarRHS);
            // Add Transition of 'Ɛ' to rhs State
            variable.addTransition('Ɛ', toState);
            if (!nfa.getSymbols().contains('Ɛ'))
                nfa.addSymbol('Ɛ');
        }
    }

    private void rule2(State variableLHS, String grammarRHS) {
        System.out.println(grammarRHS);
        // S -> aaa...
        if (!Character.isUpperCase(grammarRHS.charAt(grammarRHS.length()-1))) { // if not end with a variable
            for (Character c : grammarRHS.toCharArray()) {
                State newState = new State(getNewStateName());
                variableLHS.addTransition(c, newState);
                if (!nfa.getSymbols().contains(c))
                    nfa.addSymbol(c);
                variableLHS = newState;
                nfa.addState(variableLHS);
            }
        }
        // S -> aX
        else if (grammarRHS.length() == 2) {
            State toState = getStateIfPresentElseGetNew(grammarRHS.substring(1));
            variableLHS.addTransition(grammarRHS.charAt(0), toState);
            if (!nfa.getSymbols().contains(grammarRHS.charAt(0)))
                nfa.addSymbol(grammarRHS.charAt(0));
        }
        // S -> aaa...X
        else {
            rule2(variableLHS, grammarRHS.substring(0, grammarRHS.length()-2));
            rule2(states.get(states.size()-1), grammarRHS.substring(grammarRHS.length()-2));
        }
    }

    private void setPossibleFinalStates() {
        states.stream().filter(x -> x.getTransitions().isEmpty()).forEach(State::setAsFinalState);
    }
    private State getStateIfPresentElseGetNew(String stateName) {
        return states.stream()
                     .filter(x -> x.toString().equals(stateName))
                     .findFirst().orElseGet(() -> {
                         State newState = new State(stateName);
                         nfa.addState(newState);
                         return newState;
                     });
    }

    private String getNewStateName() {
        return availableStateNames.remove(0);
    }
}
