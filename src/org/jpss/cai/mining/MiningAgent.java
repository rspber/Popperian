package org.jpss.cai.mining;

import org.jpss.cai.libs.PState;
import org.jpss.cai.libs.State;
import org.jpss.cai.libs.ubup3.EasyLearnAndPredict;
import org.jpss.cai.libs.ufway44.ActionStateList;
import org.jpss.cai.libs.ufway44.CompositePlan;
import org.jpss.cai.libs.ufway44.Plan;
import org.jpss.cai.libs.ufway44.ToAct;
import org.jpss.cai.libs.ufway44.Ufway44;
import org.jpss.cai.util.MM;

// part of UFRob1

class MiningAgent extends CompositePlan
{
//	double PreviousX, PreviousY;
	byte PreviousAction;
	State PreviousStates = new State();

	private int PlannedActionsCnt;	// reports
	private int PlanningErrorCnt;	// reports
	private int RandomActionsCnt;	// reports

	private boolean LastPlanned;
	private final ActionStateList LastStates;
	private final ActionStateList LastPlannedStates;
	Plan LastTargetPlan;

	final EasyLearnAndPredict LearnAndPredict;
	final int csMaxPlanejamento;

	private final Forms Forms;


	public Plan PlanPredict(final int id)
	{
		return new Plan(id, URobMin2.csNumberActions) {

			private final ToAct clixo = new ToAct();

			// This function predicts the next state and returns true
			// if the ACTION is in the PATH of the target state (load/unload).
			@Override
			public boolean PredictNextState(
				// input/output
				final /*var*/ PState pCurrentState,
				// input
				final byte Action)
			{
				LastTargetPlan = null;
				if( EasyPredictNextState(/*var*/ pCurrentState, Action) ) {
					return true;
				} else {
					if( ChooseBestPlan(pCurrentState.states, clixo) ) {	// ToAct
						LastTargetPlan = clixo.BestPlan;
						clixo.clear();
						return true;
					}
				}
				return false;
			}

			// This function predicts the next state and returns true
			// if the ACTION brings to the "target" state in just one step.
			@Override
			public boolean EasyPredictNextState(
				// input/output
				final /*var*/ PState pCurrentState,
				// input
				final byte Action)
			{
				final State localState = pCurrentState.states.cloneAndSetAction((byte)(Action + 1));
				LearnAndPredict.Predict(localState, localState, /*var*/pCurrentState);
				return UFRob1.isTargetAction(localState, Action);
			}
		};
		
	}
	
	@Override
	protected Plan[] PredictedPlans()
	{
		final Plan[] Plans = new Plan[Ufway44.MaxPlans];
		for( int i = 0; i < Plans.length; i++ ) {
			Plans[i] = PlanPredict(i);
		}
		return Plans;
	}


// ---------------------------------------------------------------------------

	// Creates needed internal memory structures for learning and planning.
	public MiningAgent(final EasyLearnAndPredict LearnAndPredict, final Forms Forms)
	{
		super();
		this.csMaxPlanejamento = (int)Math.round(UFRob1.FWorldLength * 1.5);
		this.LearnAndPredict = LearnAndPredict;
		this.Forms = Forms;
		PlannedActionsCnt = 0;
		PlanningErrorCnt = 0;
		RandomActionsCnt = 0;
		LastPlanned = false;
		LastStates = new ActionStateList();
		LastPlannedStates = new ActionStateList();
	}


	void Clear()
	{
		LastStates.clear();
		LastPlannedStates.clear();
	}

	//	marks the last used plan as invalid plan.
	private void InvalidateLastUsedPlan()
	{
		if( LastUsedPlan != null ) {
			LastUsedPlan.Invalidate();
		}
	}

	private void AddPlan(final State pCurrentState, final boolean notCollapsePlans)
	{
		if( LastStates.numStates() > 0 ) {
			if( PreviousAction != URobMin2.csHold ) {	// 4
				LastStates.shiftAddState(pCurrentState, PreviousAction);
			}
			LastStates.removeAllCicles();
			final Plan P = ChooseWorst();
			P.FPlan.receivePlan(LastStates);
			P.Found = true;
			if( !notCollapsePlans ) {
				CollapsePlans(P, LastUsedPlan);
			}
		}
	}

	private boolean MakePlan(final State pCurrentState)
	{
		// chooses the worst plan and replaces by a newly built plan.
		boolean WantQuit = false;
		final Plan P = ChooseWorst();
		if( P.MultipleRun(pCurrentState, csMaxPlanejamento, 1) ) {
			if( LastTargetPlan != null ) {
				CollapsePlans(P, LastTargetPlan);
			}
			if( ChooseBestPlan(pCurrentState, toAct) ) {
				LastUsedPlan = toAct.BestPlan;
//				resultAction = toAct.Action;
				LastStates.clear();
				LastPlanned = true;
				WantQuit = true;	//exit;
			}
		} else {
			Forms.ShowPlan(P, UFRob1.FWorldLength);
		}
		return WantQuit;
	}

	// ChooseActionAndPlan
	private final ToAct toAct = new ToAct();

	// Given a current state, this function is capable of choosing an action and
	// build/optimize plans as required.
	public byte ChooseActionAndPlan(final State pCurrentState)
	{
		byte localAction = 0;
		byte resultAction = URobMin2.csHold;	// 4
		boolean WantQuit = false;
// ---
		if( !LastPlanned && (PreviousStates.charge() != pCurrentState.charge()) ) {
			AddPlan(pCurrentState, true);
		}
// ---
		final Plan tmpLastUsedPlan = LastUsedPlan;
		if( Forms.Optimization() > MM.random(100) ) {  // -------------- Optimization ?	// pass 2
			if( MM.random(2) > 0 ) {
				MultipleOptimize(pCurrentState, 5, 20);
			} else {
				Optimize(pCurrentState, 95);
			}
			if( LastPlanned ) {
				MultipleOptimizeLastUsedPlan(pCurrentState, 5, 200);
				MultipleOptimizeLastUsedPlan(pCurrentState, 95, 10);
			}
		}
		if( tmpLastUsedPlan != LastUsedPlan ) {
			System.out.println("ERROR!!!! Plan used and changed along optimization!!!");
		}
// ---
		if( !WantQuit && Forms.UseLearning() ) { // ------------------ Learning ?	// pass 1
			if( pCurrentState.closeToAgent() && pCurrentState.charge() == 0 ) {
				localAction = URobMin2.csLoad;
				resultAction = localAction;
				WantQuit = true;
			}
			if( pCurrentState.closeToBase() && pCurrentState.charge() > 0 ) {
				localAction = URobMin2.csUnLoad;
				resultAction = localAction;
				WantQuit = true;
			}
		}
// ---
		if( !WantQuit && Forms.UsePlanning() ) { // --------------------- Planning ?	// pass 2
			if( Forms.EliminateIncorrectPlan() ) { // -------------------  should eliminate incorrect plan?		// pass 2
				if( LastPlanned && !toAct.NextState.eq(pCurrentState) && !(PreviousStates.charge() != pCurrentState.charge()) ) {
					InvalidateLastUsedPlan();
					PlanningErrorCnt++;
				}
			}
			if( ChooseBestPlan(pCurrentState, toAct) ) {  // --------------- follows planning
				LastUsedPlan = toAct.BestPlan;
				localAction = toAct.Action;
				// lastAction = toAct.LastAct;
				if( !LastPlanned ) {
					AddPlan(pCurrentState, false);
				}
				if( LastPlannedStates.fastIndexOf(pCurrentState) != -1 ) { // Is this a  cycle?
					InvalidateLastUsedPlan(); // delete cycling plan.
				}
				resultAction = localAction;
				PlannedActionsCnt++;
				LastPlanned = true;
				WantQuit = true;	//exit;
			} else {
				// create plan
				// not a good outcome ???
				if( !UFRob1.isTargetAction(PreviousStates, PreviousAction) && LastPlanned && !(PreviousStates.charge() != pCurrentState.charge()) ) {
					InvalidateLastUsedPlan();
				}
				LastPlanned = false;
				if( PreviousAction != URobMin2.csHold ) {
					LastStates.shiftAddState(pCurrentState, PreviousAction);
				}
				if( Forms.CreateNewPlan() ) { // ------------------ should create new plans ???
					if( MakePlan(pCurrentState) ) {
						WantQuit = true;
						resultAction = toAct.Action;
					}
				} else {
					// do not create a new plan
				}
			}
		} else {
			// do not use planning
			LastPlanned = false;
			if( PreviousAction != URobMin2.csHold ) {	// 4
				LastStates.shiftAddState(pCurrentState, PreviousAction);
			}
		}
		if( !WantQuit && LastPlanned ) {
			System.out.println("ERROR:Last Planned1");
		}
// ---
		if( !WantQuit && (MM.random(100) < Forms.RandomBehaviour()) ) {  // -------------- RandomBehaviour ?
			boolean FPreferred = false;
			byte PreferredAction = 0;
			if( MM.random(UFRob1.FWorldLength * URobMin2.csNumberActions) == 0 ) {
				FPreferred = (MM.random(2) > 0);
				PreferredAction = (byte)MM.random(URobMin2.csNumberActions);
			}
			if( FPreferred && MM.random(2) > 0 ) {
				localAction = PreferredAction;
			} else {
				localAction = (byte)MM.random(URobMin2.csNumberActions);
			}
			resultAction = localAction;
			RandomActionsCnt++;
			WantQuit = true;
		}
// ---
		if( LastPlanned ) {
			LastPlannedStates.shiftAddState(pCurrentState, localAction);
		} else {
			LastPlannedStates.clear();
			LastUsedPlan = null;
		}

		if( UFRob1.isTargetAction(PreviousStates, PreviousAction) ) {
			if( LastPlannedStates.numStates() > 0 ) {
				LastPlannedStates.clear();
			}
		}

		return resultAction;
	}

}

