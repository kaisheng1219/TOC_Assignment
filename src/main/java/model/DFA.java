package model;

import java.util.*;
import java.util.stream.Collectors;

public class DFA extends Automaton {
    // Minimize DFA
    public void minimize() {
        removeUnreachableStates();
        ArrayList<State> nonFinalStates = states.stream()
                                                .filter(x -> !x.isFinalState())
                                                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<State> finalStates = states.stream()
                                             .filter(State::isFinalState)
                                             .collect(Collectors.toCollection(ArrayList::new));

        Group group1 = new Group("G1", nonFinalStates);
        Group group2 = new Group("G2", finalStates);
        if (!group1.getStates().isEmpty() && !group2.getStates().isEmpty()) {
            HopcroftMinimizer minimizer = new HopcroftMinimizer(group1, group2, symbols);
            this.states = minimizer.minimize();
        }
    }

    public boolean isStringAcceptedOrRejected(String input) {
        boolean status = false;
        // start from start state
        State currentState = states.stream().filter(State::isStartState).findFirst().orElseThrow();
        for (int i = 0; i < input.length(); i++) {
            //get character for string, one by one, as a symbol
            char symbol = input.charAt(i);

            // Only change state if input is not epsilon
            // And check if the current state has any transition with the symbol
            if (symbol != 'Ɛ') {
                //if contains, update currentstate value
                if (!currentState.getTransition(symbol).isEmpty())
                    currentState = currentState.getTransition(symbol).iterator().next();
                else {
                    status = false;
                    break;
                }
            }

            //if is last character of string, check if current state is final state
            status = i == input.length() - 1 && currentState.isFinalState();
        }
        return status;
    }

    private void removeUnreachableStates() {
        // Construct tree from start state
        State startState = states.stream().filter(State::isStartState).toList().get(0);
        ArrayList<State> resultStates = new ArrayList<>();
        Stack<State> bfsStates = new Stack<>();
        bfsStates.add(startState);

        while (!bfsStates.isEmpty()) {
            if (!resultStates.contains(bfsStates.peek())) {
                State stateInDFA = bfsStates.pop();
                resultStates.add(stateInDFA);
                for (Character c : symbols)
                    bfsStates.addAll(stateInDFA.getTransition(c));
            } else
                bfsStates.pop();
        }
        this.states = resultStates;
    }
}