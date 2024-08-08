package org.jpss.cai.libs.ufway44;

import org.jpss.cai.libs.State;

public class ToAct {

	public State NextState;
	public byte Action;
	public boolean LastAct;
	final int[] index = new int[Ufway44.MaxPlans];
	public Plan BestPlan;

	public void clear()
	{
		State.deref(NextState); NextState = null;
		BestPlan = null;
	}
}
