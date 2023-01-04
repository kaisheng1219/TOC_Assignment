package model;

import java.util.*;
import java.util.stream.Collectors;

public class DFA extends Automaton {
    @Override
    public String getDefinition() {
        StringBuilder definition = new StringBuilder("M = {Q, Σ, δ, q₀, F}\n");
        definition.append("Q = ")
                .append(states.toString().replace("[", "{").replace("]", "}\n"));
        definition.append("Σ = ")
                .append(symbols.stream().filter(s -> s != 'Ɛ')
                        .toList().toString()
                        .replace("[", "{").replace("]", "}\n"));
        definition.append("δ: Q x Σ\uD835\uDF74 ➞ Pow(Q)\n");
        definition.append("q₀ = ").append(states.stream().filter(State::isStartState).toList().get(0)).append("\n");
        definition.append("F = ")
                .append(states.stream().filter(State::isFinalState)
                        .toList()
                        .toString()
                        .replace("[", "{").replace("]", "}"));
        return definition.toString();
    }

    // Using BFS 
    private void removeUnreachableStates() {
        // Construct tree from start state 
        State startState = states.stream().filter(State::isStartState).toList().get(0);
        // LinkedHashMap<State, HashMap<Character, State>> statesInMinDFA = new LinkedHashMap<>();
        // Stack<State> statesInMinDFA = new Stack<>();
        System.out.println("Start state: " + startState);
        // Initial Transition Table
        //            0       1
        //   Z        Z       Z
        //   A        A       Z
        //   B        A       Z
        // 
        // Result : A, Z
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
        System.out.print("Reachable states: ");
        for (State s : resultStates)
            System.out.print(s + " ");
        System.out.println();
    }

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

        HopcroftMinimizer minimizer = new HopcroftMinimizer(group1, group2, symbols);
        ArrayList<Group> groups = minimizer.getGroups();
        for (Group g : groups) {
            System.out.println(g + ": " + g.getStates());
        }
    }
}