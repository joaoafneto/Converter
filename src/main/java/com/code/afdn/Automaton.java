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

    public boolean execute(String sentence) {
        Set<Integer> currentStates = new TreeSet<Integer>();

        currentStates.add(getInitialState());

        for (Character character : sentence.toCharArray()) {
            Set<Integer> states = new TreeSet<Integer>();

            for (Integer state : currentStates) {
                if (transitions.containsKey(state)) {
                    if (transitions.get(state).containsKey(character.toString())) {
                        states.addAll(transitions.get(state).get(character.toString()));
                    }
                }
            }

            if (states.isEmpty())
                return false;

            currentStates = states;
        }

        for (Integer stateId : currentStates) {
            if (states.get(stateId).isFinal())
                return true;
        }

        return false;
    }

    private List<State> findPossibleNextStates(Integer stateId, Character character) {
        if (!transitions.containsKey(stateId)) {
            return null;
        }

        List<Integer> states = transitions.get(stateId).get(character.toString());

        if (states != null) {
            List<State> nextStates = this.states.values().stream()
                    .filter(currentState -> states.contains(currentState.id)).toList();
            return nextStates;
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

        return StateType.Default;
    }

    public Integer compareState(HashMap<Integer, State> newStates, List<State> stateList) {
        for (int i = 0; i < newStates.entrySet().size(); i++) {
            Map.Entry<Integer, State> newState = new ArrayList<>(newStates.entrySet()).get(i);
            List<State> innerStates = newState.getValue().states;

            if (innerStates.containsAll(stateList) && stateList.containsAll(innerStates)) {
                return newState.getValue().id;
            }
        }

        return -1;
    }

    public Automaton convert() {
        HashMap<Integer, State> newStates = new HashMap<Integer, State>();
        Map<Integer, TreeMap<String, List<Integer>>> transitions = new TreeMap<Integer, TreeMap<String, List<Integer>>>();
        Integer currentId = 0;
        State initialState = states.get(getInitialState());

        newStates.put(currentId,
                new State(currentId, initialState.name, StateType.Initial, Arrays.asList(initialState)));

        for (int i = 0; i < newStates.entrySet().size(); i++) {
            Map.Entry<Integer, State> state = new ArrayList<>(newStates.entrySet()).get(i);

            for (Character character : alphabet.replaceAll(",", "").toCharArray()) {
                List<State> nextStates = new ArrayList<State>();

                currentId = getId(newStates, transitions, currentId, state, character, nextStates);
            }
        }

        return new Automaton(newStates, transitions, alphabet);
    }

    private Integer getId(HashMap<Integer, State> newStates,
            Map<Integer, TreeMap<String, List<Integer>>> transitions, Integer currentId,
            Map.Entry<Integer, State> state, Character character, List<State> nextStates) {
        for (State innerState : state.getValue().states) {
            List<State> possibleStatesList = findPossibleNextStates(innerState.id, character);

            if (possibleStatesList != null)
                nextStates.addAll(possibleStatesList);
        }

        if (nextStates.size() > 0) {
            Integer idExists = compareState(newStates, nextStates);

            if (idExists >= 0) {
                findId(transitions, state, character);

                transitions.get(state.getKey()).get(character.toString()).add(idExists);
            } 
            else {
                State stateToAdd = new State(++currentId, getStateNames(nextStates),
                        isFinalState(nextStates), nextStates);

                newStates.put(stateToAdd.id, stateToAdd);

                findId(transitions, state, character);

                transitions.get(state.getKey()).get(character.toString()).add(stateToAdd.id);
            }
        }

        return currentId;
    }

    private void findId(Map<Integer, TreeMap<String, List<Integer>>> transitions, Map.Entry<Integer, State> state,
            Character character) {
        if (!transitions.containsKey(state.getKey()))
            transitions.put(state.getKey(), new TreeMap<String, List<Integer>>());

        if (!transitions.get(state.getKey()).containsKey(character.toString()))
            transitions.get(state.getKey()).put(character.toString(), new ArrayList<Integer>());
    }
}