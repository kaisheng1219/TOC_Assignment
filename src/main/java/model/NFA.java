package model;

import util.GrammarConverter;

import java.util.*;

public class NFA extends Automaton {
    public String getDefinition() {
        return "M = {Q, Σ, δ, q₀, F}\n" + "Q = " +
                states.toString().replace("[", "{").replace("]", "}\n") +
                "Σ = " +
                symbols.stream().filter(s -> s != 'Ɛ')
                        .toList().toString()
                        .replace("[", "{").replace("]", "}\n") +
                "δ: Q x Σ\uD835\uDF74 ➞ Pow(Q)\n" +
                "q₀ = " + states.stream().parallel().filter(State::isStartState).toList().get(0) + "\n" +
                "F = " +
                states.stream().parallel().filter(State::isFinalState)
                        .toList()
                        .toString()
                        .replace("[", "{").replace("]", "}");
    }

    public void removeEpsilonTransition() {
        for (State s : states) {
            for (Character c : symbols) {
                if (c == 'Ɛ') continue; // Ɛ = epsilon
                Set<State> statesToBeTransition = getEClosureOfState(s);
                Set<State> statesToBeEclosure = getStatesToEClosure(statesToBeTransition, c);
                Set<State> statesAfterEclosure = getEClosureOfStates(statesToBeEclosure);

                if (statesAfterEclosure != null)
                    for (State k : statesAfterEclosure)
                        s.addTransition(c, k);
            }
        }
        State startState = states.stream().filter(State::isStartState).findFirst().orElseThrow();
        if (getEClosureOfState(startState).stream().anyMatch(State::isFinalState))
            startState.setAsFinalState();
        states.forEach(state -> state.removeTransition('Ɛ'));
        symbols.remove(Character.valueOf('Ɛ'));
    }

    public void toDFA(DFA dfa) {
        resolvePowerset();
        dfa.getStates().addAll(getStates());
        dfa.getSymbols().addAll(getSymbols());
    }

    private void resolvePowerset () {
        Map<Set<State>, State> mergedStateNameMapper = new HashMap<>();
        State nullState = new State("Z");
        symbols.forEach(symbol -> nullState.addTransition(symbol, nullState));
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
        StringBuilder mergedStateName = new StringBuilder();
        mergedState.forEach(mergedStateName::append);
        return this.states.stream()
                .filter(x -> x.toString().equals(mergedStateName.toString()))
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
        states.forEach(state -> symbols.forEach(symbol -> {
            Set<State> transitions = state.getTransition(symbol);
            state.removeTransition(symbol);
            if (transitions == null || transitions.isEmpty()) {
                State nullState = states.stream().filter(x -> x.toString().equals("Z")).findFirst().orElseThrow();
                state.addTransition(symbol, nullState);
            } else
                state.addTransition(symbol, mergedStateNameMapper.get(transitions));
        }));
    }

    // Step 1 Get the state's epsilon closure
    private Set<State> getEClosureOfState(State state) {
        Set<State> closureStates = new LinkedHashSet<>();
        closureStates.add(state);
        if (state.getTransitions().containsKey('Ɛ'))
            for (State s : state.getTransition('Ɛ'))
                closureStates.addAll(getEClosureOfState(s));
        return closureStates;
    }

    // Step 2 Test each of the closure states with the transition 
    private Set<State> getStatesToEClosure(Set<State> states, Character c) {
        Set<State> statesToEClosure = new LinkedHashSet<>();
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
    private Set<State> getEClosureOfStates(Set<State> state) {
        if (state.contains(null)) return null;
        Set<State> result = new LinkedHashSet<>();
        for (State s : state)
            result.addAll(getEClosureOfState(s));
        return result;
    }
}
