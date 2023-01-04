package model;

import java.util.*;
public class HopcroftMinimizer {
	private ArrayList<Character> symbols;
	private ArrayList<Group> groups; // Store accepting and non-accepting sets
	private ArrayList<Group> current; // Store latest groups

	public HopcroftMinimizer(Group nonFinals, Group finals, ArrayList<Character> symbols) {
		this.current = new ArrayList<>();
		this.groups = new ArrayList<>(Arrays.asList(nonFinals, finals));
		this.symbols = symbols;
		this.minimize();
	}

	public ArrayList<Group> getGroups() { return this.groups; }

	public void minimize() {
		ArrayList<Group> finalResult = new ArrayList<>(groups);

		while (!current.equals(finalResult)) {
			System.out.println("current: " + current);
			current = new ArrayList<>(finalResult);
			System.out.println("groups: " + groups);
			finalResult = new ArrayList<>();
			for (Group g : current) {
				finalResult.addAll(stabilize(g));
				// groups.addAll(stabilize(g));
				System.out.println("States in " + g.toString() + ": " + g.getStates());
			}
			groups = new ArrayList<>(finalResult);
		}
	}

	private ArrayList<Group> stabilize(Group group) {
		HashSet<ArrayList<Group>> outputs = new HashSet<>();
		ArrayList<Group> results = new ArrayList<>();
		results.add(group);

		for (State s : group.getStates()) {
			ArrayList<Group> tempGroup = new ArrayList<>();
			for (Character c : symbols) {
				State tempTransition = s.getTransition(c).stream().findFirst().get();
				for (Group g : this.groups) {
					if (g.getStates().contains(tempTransition)) {
						tempGroup.add(g);
					}
				}
			}
			System.out.println("tempGroup: " + tempGroup);
			if (outputs.isEmpty())
				outputs.add(tempGroup);
			else if (!outputs.contains(tempGroup)) {
				System.out.println("tempGroup: " + tempGroup + " is different than " + outputs);
				if (results.size() == 1)
					results.add(new Group());
				results.get(1).addState(s);
			}
		}
		if (results.size() > 1) {
			System.out.println("States in " + results.get(1) + ": " + results.get(1).getStates());
			group.getStates().removeAll(results.get(1).getStates());
		}
		System.out.println("Result: " + results);
		return results;
	}

	// private ArrayList<Set<State>> split(Set<State> s) {
	// 	ArrayList<Set<State>> temp = new ArrayList<>();
	// 	for (Character c : symbols) {
	// 		for (State state : s) {
	// 			State result = state.getTransition(c).get(0);
				
	// 		}
	// 	}
	// 	return s;
	// }

	// public Set<Set<Integer>> hopcroftMinimization() {
	// 	Set<Set<Integer>> P = new HashSet<>();
	// 	Set<Set<Integer>> W = new HashSet<>();
	// 	P.add(new HashSet<Integer>(accepting));
	// 	P.add(setSubtraction(states, accepting));
	// 	W.addAll(P);
	   
	// 	while (!W.isEmpty()) {
	// 	  System.out.println(W.toString());
	// 	  Object[] wArr = W.toArray();
	// 	  Object[] pArr = P.toArray();
	   
	// 	  for (Object A : wArr) {
	// 		W.remove(A);
	// 		System.out.println(W.toString());
	// 		for (char input : validInputs) {
	// 		  Set<Integer> X = findX((Set)A, input);
	   
	// 		  for (Object Y : pArr) {
	// 			Set<Integer> xAndY = intersection(X, (Set)Y);
	// 			Set<Integer> yNotX = setSubtraction((Set)Y, X);
	// 			if (!xAndY.isEmpty() && !yNotX.isEmpty()) {
	// 			  P.remove(Y);
	// 			  P.add(xAndY);
	// 			  P.add(yNotX);
	// 			  if (W.contains(Y)) {
	// 				W.remove(Y);
	// 				W.add(xAndY);
	// 				W.add(yNotX);
	// 			  }
	// 			} else {
	// 			  if (xAndY.size() <= yNotX.size()) {
	// 				W.add(xAndY);
	// 			  } else {
	// 				W.add(yNotX);
	// 			  }
	// 			}
	// 		  }
	// 		}
			
	// 	  }
	// 	}
	   
	// 	return P;
	//   }
	// public Set<Integer> intersection(Set<Integer> A, Set<Integer> B) {
    //     Set<Integer> ret = new HashSet<>();
    //     ret.addAll(A);
    //     ret.retainAll(B);
    //     return ret;
    // }
    
    // public Set<Integer> setSubtraction(Set<Integer> A, Set<Integer> B) {
    //     Set<Integer> ret = new HashSet<>();
    //     ret.addAll(A);
    //     ret.removeAll(B);
    //     return ret;
    // }
    
    // public Set<Integer> findX(Set<Integer> currA, char currC) {
    //     Set<Integer> X = new HashSet<>();
    //     for (int state : currA)
	// 		if (map.doesInputLeadToState(currC, state)) 
	// 			X.add(state);
    //     return X;
    // }
}