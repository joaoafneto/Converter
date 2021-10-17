package com.code.afdn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Automaton {
    public final Map<Integer, State> states;
    public final Map<Integer, TreeMap<String, List<Integer>>> transitions;
    public final String alphabet;

    public Automaton(HashMap<Integer, State> states, Map<Integer, TreeMap<String, List<Integer>>> transitions,
            String alphabet) {
        this.states = states;
        this.transitions = transitions;
        this.alphabet = alphabet;
    }

    @Override
    public String toString() {
        return "Automaton [\n\talphabet=" + alphabet + ", \n\tstates=" + states + ", \n\ttransitions=" + transitions
                + "\n]";
    }

    private Integer getInitialState() {
        return this.states.values().stream().filter(x -> x.isInitial()).findFirst().orElse(null).id;
    };

    public boolean run(String sentence) {
        Set<Integer> currentStates = new TreeSet<Integer>();

        currentStates.add(getInitialState());

        for (Character character : sentence.toCharArray()) {
            Set<Integer> nextStates = new TreeSet<Integer>();

            for (Integer state : currentStates) {
                if (transitions.containsKey(state)) {
                    if (transitions.get(state).containsKey(character.toString())) {
                        nextStates.addAll(transitions.get(state).get(character.toString()));
                    }
                }
            }

            if (nextStates.isEmpty())
                return false;

            currentStates = nextStates;
        }

        for (Integer stateId : currentStates) {
            if (states.get(stateId).isFinal())
                return true;
        }

        return false;
    }

    private List<State> findPossibleNextStates(Integer stateId, Character transitionCharacter) {
        if (!transitions.containsKey(stateId)) {
            return null;
        }

        List<Integer> possibleStates = transitions.get(stateId).get(transitionCharacter.toString());

        if (possibleStates != null) {
            List<State> possibleNextStates = this.states.values().stream()
                    .filter(currentState -> possibleStates.contains(currentState.id)).toList();
            return possibleNextStates;
        }

        return null;
    }

    private String getStateNames(List<State> stateList) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < stateList.size(); i++) {
            if (i == stateList.size() - 1) {
                builder.append(stateList.get(i).name);
            } else {
                builder.append(stateList.get(i).name + ",");
            }
        }

        return builder.toString();
    }

    private StateType isFinalState(List<State> stateList) {
        boolean isFinal = stateList.stream().anyMatch(state -> state.isFinal());

        if (isFinal) {
            return StateType.Final;
        }

        return StateType.Normal;
    }

    public Integer compareInnerState(HashMap<Integer, State> newStates, List<State> stateList) {
        for (int i = 0; i < newStates.entrySet().size(); i++) {
            Map.Entry<Integer, State> newState = new ArrayList<>(newStates.entrySet()).get(i);

            List<State> innerStates = newState.getValue().innerStates;

            if (innerStates.containsAll(stateList) && stateList.containsAll(innerStates)) {
                return newState.getValue().id;
            }
        }

        return -1;
    }

    public Automaton convert() {
        HashMap<Integer, State> newStates = new HashMap<Integer, State>();
        Map<Integer, TreeMap<String, List<Integer>>> newTransitions = new TreeMap<Integer, TreeMap<String, List<Integer>>>();

        Integer currentId = 0;

        State initialState = states.get(getInitialState());
        newStates.put(currentId,
                new State(currentId, initialState.name, StateType.Initial, Arrays.asList(initialState)));

        for (int i = 0; i < newStates.entrySet().size(); i++) {
            Map.Entry<Integer, State> newState = new ArrayList<>(newStates.entrySet()).get(i);

            for (Character character : alphabet.replaceAll(",", "").toCharArray()) {
                List<State> possibleNextStates = new ArrayList<State>();

                for (State innerState : newState.getValue().innerStates) {
                    List<State> possibleStatesList = findPossibleNextStates(innerState.id, character);

                    if (possibleStatesList != null)
                        possibleNextStates.addAll(possibleStatesList);
                }

                if (possibleNextStates.size() > 0) {
                    Integer idIfAlreadyExists = compareInnerState(newStates, possibleNextStates);

                    if (idIfAlreadyExists >= 0) {
                        if (!newTransitions.containsKey(newState.getKey()))
                            newTransitions.put(newState.getKey(), new TreeMap<String, List<Integer>>());

                        if (!newTransitions.get(newState.getKey()).containsKey(character.toString()))
                            newTransitions.get(newState.getKey()).put(character.toString(), new ArrayList<Integer>());

                        newTransitions.get(newState.getKey()).get(character.toString()).add(idIfAlreadyExists);
                    } else {
                        State stateToAdd = new State(++currentId, getStateNames(possibleNextStates),
                                isFinalState(possibleNextStates), possibleNextStates);

                        newStates.put(stateToAdd.id, stateToAdd);

                        if (!newTransitions.containsKey(newState.getKey()))
                            newTransitions.put(newState.getKey(), new TreeMap<String, List<Integer>>());

                        if (!newTransitions.get(newState.getKey()).containsKey(character.toString()))
                            newTransitions.get(newState.getKey()).put(character.toString(), new ArrayList<Integer>());

                        newTransitions.get(newState.getKey()).get(character.toString()).add(stateToAdd.id);
                    }
                }
            }
        }

        return new Automaton(newStates, newTransitions, alphabet);
    }
}
