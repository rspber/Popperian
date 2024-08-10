package org.jpss.cai.libs.ufway44;

import org.jpss.cai.libs.PState;
import org.jpss.cai.libs.State;
import org.jpss.cai.util.MM;

/*
  This class has 2 main goals:
  * This class can be used as a "plan builder" done by methods "Run" and "Multiple Run".
  * This class can be used as a "plan optimizer" done by methods "Optimize From" and "MultipleOptimizeFrom".
  The plan is stored in the FPlan property.
*/
public abstract class Plan
{
	public abstract boolean PredictNextState(final /*var*/ PState pCurrent, final byte targetState);
	public abstract boolean EasyPredictNextState(final /*var*/ PState pCurrent, final byte targetState);

	private final int FNumberActions;

// true means that the object is a plan that leads to success.
	public boolean Found;
// the plan.
	public final ActionStateList FPlan;

	public final int id;

	public Plan(final int id, final int PNumberActions)
	{
		this.id = id;
		this.FNumberActions = PNumberActions;
		this.FPlan = new ActionStateList();
		this.V2 = new ActionStateList();
	}

	// if state ST is a state in the plan, it returns the index of the next step in the plan.
	public int GetNextStepIndex(final State CurrentState)
	{
		if( Found ) {
			final int r = FPlan.fastIndexOf(CurrentState);
			if( r != FPlan.numStates() - 1 && r != -1 ) {
				return r + 1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	// Invalidate the plan (Means the plan doesn't lead to success).
	public void Invalidate()
	{
		Found = false;
	}

	/*
	  Gera Plano
	  TVS: onde vai ser montado o plano
	  Estado: Estado Inicial do Planejamento
	  NCicles: Numero de ciclos do planejamento (profundidade mĂˇxima)
	  PPred: Funcao de predicao. Retorna true quando acha satisfacao
	  NumberActions: numero de acoes possiveis. O intervalo de acoes
	  eh 0.. NumberActions-1
	*/

		private final PState tmp3 = new PState();

		private byte ChooseRandomAction(final PState tmp)
		{
			for( int i = 0; i < FNumberActions; i++ ) {
				final byte Action = (byte)MM.random(FNumberActions);
				tmp3.states = tmp.states;
				PredictNextState(/*var*/ tmp3, Action);
				if( FPlan.fastIndexOf(tmp3.states) == -1 ) {
					return Action;
				}
			}
			return (byte)MM.random(FNumberActions);
		}
	
		/*
		  This function tries to find a solution in just 1 step.
		  Returns -1 if can't find solution in 1 step.
		  In the case a solution is found, returns the action id.
		*/
		private final PState tmp4 = new PState();

		private byte ChooseActionIn1Step(final /*var*/PState tmp)
		{
			for( int i = 0; i < FNumberActions; i++ ) {
				final byte Action = (byte)i;
				tmp4.states = tmp.states;
				if( PredictNextState(/*var*/ tmp4, Action) ) {
					tmp.states = tmp4.states;
					return Action;
				}
			}
			return -1;
		}

	private final PState tmp2 = new PState();

	private double BuildPlan(final State StartState, final int planSize)
	{
		State.deref(tmp2.states);
		tmp2.states = StartState.clone();	// ride states

		final boolean Prefered = MM.random(2) > 0;
		final byte PreferedAct = (byte)MM.random(FNumberActions);
		for( int Cicles = 1; Cicles <= planSize; Cicles++ ) {
			byte Action = ChooseActionIn1Step(/*var*/tmp2);
			if( Action != -1 ) {
				//Has found satisfaction in 1 step?
				FPlan.include(tmp2.states, Action);
				return Cicles;
			} else {
				//has not found satisfaction in 1 step.
				if( Prefered && MM.random(2) > 0 ) {
					Action = PreferedAct;
				} else {
					Action = ChooseRandomAction(tmp2);
				}
				final boolean PlanFound = PredictNextState(/*var*/ tmp2, Action);
				FPlan.include(tmp2.states, Action);
				if( PlanFound ) {
					return Cicles;
				}
			}
		}
		return 0;
	} 

	// This function builds a plan. It returns true if a solution (plan) is found.
	// Deep is the maximum number of steps that a plan can have.
	private boolean Run(final State StartState, final int deep)
	{
		Found = false;
		FPlan.clear();
		FPlan.include(StartState, (byte)0);
		final double r = BuildPlan(StartState, deep);
		if( r != 0 ) {
			Found = true;
			FPlan.removeAllCicles();
		}
		return Found;
	}

	// This function runs the "Run" method the "Number" of tries.
	public boolean MultipleRun(final State StartState, final int deep, final int Number)
	{
		int i = 0;
		while( i < Number && !Run(StartState, deep) ) {
			i++;
		}
		return Found;
	}

	// -----------------------------------------------------------------------------------------------------------

	//Auxiliar Plan used in optimization.
	private final ActionStateList V2;

	private final PState tmp1 = new PState();

	private double TryToBuildSubPath(final State FinishState, final State StartState, final int NCicles)
	{
		State.deref(tmp1.states);
		tmp1.states = StartState.clone();	// ride states

		final boolean Prefered = MM.random(2) > 0;
		final byte PreferedAct = (byte)MM.random(FNumberActions);
		for( int Cicles = 1; Cicles <= NCicles; Cicles++ ) {

			final byte Action;
			if( Prefered && MM.random(2) > 0 ) {
				Action = PreferedAct;	// 50% is this action.
			} else {
				Action = (byte)MM.random(FNumberActions);	// 50% is a random action.
			}
			//produces a new state based on a random action.
			EasyPredictNextState(/*var*/ tmp1, Action);

			V2.include(tmp1.states, Action);
			if( tmp1.states.eq(FinishState) ) {
				return Cicles;
			}
		}
		return 0;
	}

	// Plan optimization methods.
	public int OptimizeFrom(final int ST, final int deep)
	{
		if( ST > FPlan.numStates() - 3 ) {
			return 0;
		}
		int StartPos = MM.random(FPlan.numStates() - ST) + ST;
		int FinishPos = MM.random(FPlan.numStates() - ST) + ST;
		if( FinishPos < StartPos ) {
			// LExchange
			int AUX = StartPos;
			StartPos = FinishPos;
			FinishPos = AUX;
		}
		if( Math.abs(StartPos - FinishPos) < 2 ) {
			return 0;
		}
		V2.clear();
		final State StartState = FPlan.state(StartPos);
		V2.include(StartState, FPlan.action(StartPos));
		final State FinishState = FPlan.state(FinishPos);
		// creates a new subway/subpath
		TryToBuildSubPath(FinishState, StartState, deep);
		V2.removeAllCicles();
		// for each state in the new sub path
		for( int v2pos = 1; v2pos < V2.numStates(); v2pos++ ) {
			if( (StartPos + v2pos < FPlan.numStates() - 1) && (FPlan.fastIndexOf(V2.state(v2pos)) != -1) ) {
				// is it a state in the existing plan?
				// for each of the following states in the current plan FPlan.
				for( int FPlanPos = StartPos + v2pos; FPlanPos < FPlan.numStates(); FPlanPos++ ) {
					// Does the state in V2 exist in FPlan?
					// Has shorter path been found?
					if( V2.state(v2pos).eq(FPlan.state(FPlanPos)) ) {
						//Copy Shortes Segment from V2 to FPlan
						for( int k = 1; k <= v2pos; k++ ) {
							FPlan.setState(StartPos + k, V2.state(k));
							FPlan.setAction(StartPos + k, V2.action(k));
						}
						V2.clearStates__();
						FPlan.removeSubList(StartPos + v2pos + 1, FPlanPos);
						return FPlan.numStates();
					}
				}
			}
		}
		V2.clearStates__();
		return 0;
	}

	// Plan optimization methods.
	public void MultipleOptimizeFrom(final int ST, final int deep, final int Number)
	{
		int i = 0;
		while( OptimizeFrom(ST, deep) == 0 && i < Number ) {
			i++;
		}
	}

}
