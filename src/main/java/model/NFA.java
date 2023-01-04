package model;

import util.GrammarConverter;

import java.util.*;

public class NFA extends Automaton {
    public void removeEpsilonTransition() {
        for (State s : states) {
            for (Character c : symbols) {
                if (c == 'Ɛ') continue; // Ɛ = epsilon
                ArrayList<State> statesToBeTransition = getEClosureOfState(s);
                ArrayList<State> statesToBeEclosure = getStatesToEClosure(statesToBeTransition, c);
                ArrayList<State> statesAfterEclosure = getEClosureOfStates(statesToBeEclosure);

                if (statesAfterEclosure != null) 
                    for (State k : statesAfterEclosure)
                        s.addTransition(c, k);

            }
            s.removeTransition('Ɛ');
        }
    }

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
        definition.append("q₀ = ").append(states.stream().parallel().filter(State::isStartState).toList().get(0)).append("\n");
        definition.append("F = ")
                  .append(states.stream().parallel().filter(State::isFinalState)
                                         .toList()
                                         .toString()
                                         .replace("[", "{").replace("]", "}"));
        return definition.toString();
    }

    public void toUnminimizedDFA() {
        resolvePowerset();
    }

    private void resolvePowerset () {
        Map<Set<State>, State> mergedStateNameMapper = new HashMap<>();
        State nullState = new State("Z");
        mergedStateNameMapper.put(new HashSet<>(List.of(nullState)), nullState);

        int size = this.states.size();
        for (int i = 1; i < (1 << size); i++) {
            Set<State> subset = new LinkedHashSet<>();
            for (int j = 0; j < size; j++)
                if ((i & (1 << j)) >= 1)
                    subset.add(this.states.get(j));
            mergedStateNameMapper.put(subset, convertMergedStateToState(subset));
        }
        addState(nullState);
        renameNfaStatesToDfa(mergedStateNameMapper);
    }

    private State convertMergedStateToState(Set<State> mergedState) {
        StringBuilder finalStateName = new StringBuilder();
        mergedState.forEach(finalStateName::append);
        return this.states.stream()
                .filter(x -> x.toString().equals(finalStateName.toString()))
                .findFirst().orElseGet(() -> {
                    State newState = new State(GrammarConverter.getAvailableStateNames().remove(0));
                    addState(newState);
                    symbols.forEach(symbol -> {
                        Set<State> transitions = new HashSet<>();
                        mergedState.forEach(state -> {
                            if (state.getTransition(symbol) != null)
                                transitions.addAll(state.getTransition(symbol));
                            if (state.isFinalState())
                                newState.setAsFinalState();
                        });
                        newState.getTransitions().put(symbol, transitions);
                    });
                    return newState;
                });
    }

    private void renameNfaStatesToDfa(Map<Set<State>, State> mergedStateNameMapper) {
        states.forEach(state -> {
            symbols.forEach(symbol -> {
                Set<State> transitions = state.getTransition(symbol);
                state.removeTransition(symbol);
                if (transitions == null || transitions.isEmpty()) {
                    State nullState = states.stream().filter(x -> x.toString().equals("Z")).findFirst().orElseThrow();
                    state.addTransition(symbol, nullState);
                } else
                    state.addTransition(symbol, mergedStateNameMapper.get(transitions));
            });
        });
    }

    public void toDFA(DFA dfa) {
        dfa.getStates().addAll(this.states);
        dfa.getSymbols().addAll(this.symbols);
    }

    // Step 1 Get the state's epsilon closure
    private ArrayList<State> getEClosureOfState(State state) {
        ArrayList<State> closureStates = new ArrayList<>();
        closureStates.add(state);
        if (state.getTransitions().containsKey('Ɛ'))
            for (State s : state.getTransition('Ɛ'))
                closureStates.addAll(getEClosureOfState(s));
        
        return closureStates;
    }

    // Step 2 Test each of the closure states with the transition 
    private ArrayList<State> getStatesToEClosure(ArrayList<State> states, Character c) {
        ArrayList<State> statesToEClosure = new ArrayList<>();
        for (State s : states) {
            Set<State> tempResult = s.getTransition(c);
            if (tempResult != null)
                statesToEClosure.addAll(tempResult);
        }
        if (statesToEClosure.isEmpty())
            statesToEClosure.add(null);
        
        return statesToEClosure;
    }

    // Step 3 Pass in the states for epsilon closure
    private ArrayList<State> getEClosureOfStates(ArrayList<State> state) {
        if (state.contains(null)) return null;
        ArrayList<State> result = new ArrayList<>();
        for (State s : state)
            if (!result.contains(s))
                result.addAll(getEClosureOfState(s));

        return result;
    }
}
