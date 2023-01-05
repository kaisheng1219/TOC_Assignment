package model;

import java.util.*;
public class HopcroftMinimizer {
	private final ArrayList<Character> symbols;
	private ArrayList<Group> groups; // Store accepting and non-accepting sets
	private ArrayList<Group> current; // Store latest groups

	private final ArrayList<State> groupedStates;

	public HopcroftMinimizer(Group nonFinals, Group finals, ArrayList<Character> symbols) {
		this.current = new ArrayList<>();
		this.groups = new ArrayList<>(Arrays.asList(nonFinals, finals));
		this.symbols = symbols;
		this.groupedStates = new ArrayList<>();
//		this.minimize();
	}

	public ArrayList<State> minimize() {
		ArrayList<Group> finalGroups = new ArrayList<>(groups);

		while (!current.equals(finalGroups)) {
			System.out.println("current: " + current);
			current = new ArrayList<>(finalGroups);
			System.out.println("groups: " + groups);
			finalGroups = new ArrayList<>();
			for (Group g : current)
				finalGroups.addAll(stabilize(g));
			groups = new ArrayList<>(finalGroups);
		}
		convertGroupsIntoStates(finalGroups);
		return groupedStates;
	}

	private ArrayList<Group> stabilize(Group group) {
		HashSet<ArrayList<Group>> outputs = new HashSet<>();
		ArrayList<Group> results = new ArrayList<>();
		results.add(group);

		for (State s : group.getStates()) {
			ArrayList<Group> tempGroup = new ArrayList<>();
			for (Character c : symbols) {
				State toState = s.getTransition(c).stream().findFirst().orElseThrow();
				for (Group g : this.groups)
					if (g.getStates().contains(toState))
						tempGroup.add(g);
			}
			if (outputs.isEmpty())
				outputs.add(tempGroup);
			else if (!outputs.contains(tempGroup)) {
				if (results.size() == 1)
					results.add(new Group());
				results.get(1).addState(s);
			}
		}
		if (results.size() > 1)
			group.getStates().removeAll(results.get(1).getStates()); // remove non-stable states from prev group
		return results;
	}

	private void convertGroupsIntoStates(ArrayList<Group> finalGroups) {
		groups.forEach(group -> {
			State groupedState = new State(group.toString());
			if (group.getStates().stream().anyMatch(State::isStartState)) groupedState.setAsStartState();
			if (group.getStates().stream().anyMatch(State::isFinalState)) groupedState.setAsFinalState();
			groupedStates.add(groupedState);
		});

		for (int i = 0; i < finalGroups.size(); i++) {
			State currentGroupState = groups.get(i).getStates().get(0);
			for (Character symbol : symbols) {
				State toState = currentGroupState.getTransition(symbol).iterator().next();
				Group groupToStateBelongsTo = groups.stream().
						filter(grp -> grp.getStates().contains(toState)).findFirst().orElseThrow();
				State equivalentState = groupedStates.stream()
						.filter(gp -> gp.toString().equals(groupToStateBelongsTo.toString())).findFirst().orElseThrow();
				groupedStates.get(i).addTransition(symbol, equivalentState);
			}
		}
	}
}