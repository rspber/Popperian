package org.jpss.cai.libs.ufway44;

import java.util.ArrayList;
import java.util.List;

import org.jpss.cai.libs.State;
import org.jpss.cai.libs.TABHash;

// part of Ufway44

//
//This class contains a list of states. FKeyCache is used to speed up by keeping a
//kind of hash of all included states. An ordered list of actions and states is
//actually a PLAN.
//
public class ActionStateList
{
	private final TABHash keyCache; // 1 in a given position might mean that the entry is there.
	private final List<State> states;	// list of stored states.
	private final byte[] actions;	// list of action on state.

//	private final int FStateLength;
	
//	TVisitedStatesCopy
	public void receivePlan( final ActionStateList P )
	{
		keyCache.clear();
		//copy hash table.
		keyCache.copyFrom(P.keyCache);
		P.keyCache.clear();

		System.arraycopy(P.actions, 0, actions, 0, P.states.size());

		for( final State state : states ) {
			state.deref();
		}
		states.clear();
		states.addAll(P.states);
		P.states.clear();
	}

	// Returns the number of states of the plan.
	public int numStates()	//number of stored states
	{
		return states.size();
	}

	// Returns action of position i.
	public byte action( final int i )
	{
		return actions[i];
	}

	//set action at position i.
	public void setAction( final int i, final byte action )
	{
		actions[i] = action;
	}

	public State state( final int i )
	{
		return states.get(i);
	}

	public void setState( final int i, final State st )
	{
		states.get(i).deref();
		states.set(i, st.clone());
	}

	public ActionStateList( final int StateLength )
	{
		keyCache = new TABHash();
		actions = new byte[Ufway44.MaxStates];
		states = new ArrayList<>();
//		FStateLength = StateLength;
	}

	// Clear all entries
	void clearStates__()
	{
		for( final State state : states ) {
			state.deref();
		}
		states.clear();
	}

	// Clear all entries
	public void clear()
	{
		clearStates__();
		keyCache.clear();
	}

	// Removes the first state and action.
	public void removeFirst()
	{
		keyCache.clear();
		if( states.size() > 0 ) {
			final State st = states.remove(0);
			st.deref();
			System.arraycopy(actions, 1, actions, 0, states.size()-1);
			for( final State state : states ) {
				keyCache.include(state);
			}
		}
	}

	// includes a state and action in the end of the plan.
	public void include(final State ST, final byte Action)
	{
		keyCache.include(ST);
		if( states.size() < Ufway44.MaxStates ) {
			actions[states.size()] = Action;
			states.add( ST.clone() );
		} else {
			throw new RuntimeException("TVisitedStates: Limit states exceesed: " + states.size() + " ");
		}
		if( states.size() >= Ufway44.MaxStates - 5 ) {
			removeCicles();
		}
	}

	// includes a state and action in the end of the plan.
	public void shiftAddState(final State ST, final byte Action)
	{
		if( states.size() == Ufway44.MaxStates ) {
			removeFirst();
		}
		include( ST, Action );
	}

	// This function returns -1 when the parameter doesn't exist; else returns position ...
	// The same as Exists but faster. Depends on valid FKeyCache.
	// FastExists
	public int fastIndexOf(final State ST)
	{
		if( keyCache.test(ST) ) {
			return indexOf(ST);
		} else {
			return -1;
		}
	}

	// This function returns -1 when the parameter doesn't exist; else returns position ...
	// Exists
	private int indexOf(final State ST)
	{
		for( int i = states.size(); --i >= 0; ) {
			if( ST.eq( states.get(i) )) {
				return i;
			}
		}
		return -1;
	}

	// Recreates the FKeyCache list.
	public void reDoHash()
	{
		keyCache.clear();
		for( final State bot : states ) {
			keyCache.include(bot);
		}
	}

	// Removes all entries from InitPost to FinishPos
	public void removeSubList(final int InitPos, int FinishPos)
	{
		FinishPos++;
		if( FinishPos <= states.size() ) {
			System.arraycopy(actions, FinishPos, actions, InitPos, states.size()-FinishPos);
			int difer = FinishPos - InitPos;
			while( --difer >= 0 ) {
				final State st = states.remove(InitPos);
				st.deref();
			}
			reDoHash();
		}
	}

	// Removes some duplicate states.
	public boolean removeCicles()
	{
//System.out.println("Removing"); 
		for( int j = states.size(); --j >= 1; ) {
			final State bj = states.get(j);
			for( int i = 0; i < j; i++ ) {
				final State bi = states.get(i);
				if( bj.eq(bi) ) {
					removeSubList(i + 1, j);
//System.out.println("removed:" + ListStates.size() + "  "); 
					return true;
				}
			}
		}
		return false;
	}

	// Removes all duplicate states.
	public void removeAllCicles()
	{
		while( removeCicles() ) {
		}
	}

}
