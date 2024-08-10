package org.jpss.cai.libs.ufway44;

import java.util.Arrays;

import org.jpss.cai.libs.State;
import org.jpss.cai.util.MM;

/*
  An agent has a composition of plans. These plans can Mix together so the composite
  plan is better than the individual plans. As an example, plan A can be better in its
  first half while plan B is better in its second half. But the composition A+B makes
  a better plan than each individual plan.
*/
public abstract class CompositePlan
{
	// List of plans.
	public final Plan[] Plans;

	// Most recently used plan (plan from where the last action has been choosen).
	public Plan LastUsedPlan;

	public int plansFound() {
		int n = 0;
		for( final Plan P : Plans ) {
			if( P.Found ) {
				n++;
			}
		}
		return n;
	}

	protected abstract Plan[] PredictedPlans();

	public CompositePlan()
	{
		this.Plans = PredictedPlans();
		LastUsedPlan = null;
		// Create Plans moved to MiningAgent
	}

	// returns the index of the worst plan.
	protected Plan ChooseWorst()
	{
		Plan WorstPlan = Plans[0];
		double worst = -1000000;
		for( final Plan P : Plans ) {
			// evaluates a  plan.
			// the bigger the number, the worse is.
			final double next = !P.Found ? 100000 : 1 - P.FPlan.numStates();
			if( (next > worst) || ((next == worst) && (MM.random(2) == 0)) ) {
				worst = next;
				WorstPlan = P;
			}
		}
		return WorstPlan;
	}

	// ToAct
	public boolean ChooseBestPlan(final State ST, final /*var*/ ToAct result )
	{
		result.LastAct = false;

		Plan BestPlan = ChooseBestPlanBasedOnNextStep(ST, result );

		for( int i = 0; i < Plans.length; i++ ) {
//			final int AcI = BP.GetNextIndex(ST);
			final int AcI = result.index[BestPlan.id];
			if( AcI != -1 ) {
				final ActionStateList BestFPlan = BestPlan.FPlan;
				result.Action = BestFPlan.action(AcI);
				result.BestPlan = BestPlan;
				result.LastAct = (BestFPlan.numStates() - 1 == AcI);
				State.deref(result.NextState); result.NextState = BestFPlan.state(AcI).clone();
				return true;
			}
			BestPlan = Plans[ (BestPlan.id + 1) % Plans.length ];
		}
		return false;
	}

	private Plan ChooseBestPlanBasedOnNextStep(final State ST, final /*var*/ ToAct result )
	{
		// choose the best plan to be used using the evaluation based on the next step of each plan.
		Plan BestPlan = Plans[0];
		{
			double best = 1000000;
			for( int i = 0; i < Plans.length; ++i ) {
				final Plan P = Plans[i]; 
				// evaluates how good a plan is based on the next step of the plan.
				final int AcI = P.GetNextStepIndex(ST);
				result.index[i] = AcI;
				final double curr = AcI == -1 ? 100000 : P.FPlan.numStates() - AcI;
				if( (curr < best) || ((curr == best) && (MM.random(2) == 0)) ) {
					best = curr;
					BestPlan = P;
				}
			}
		}
		return BestPlan;
	}

//unite plans.
//plan x is added plan y - unites plans
	public void CollapsePlans(final Plan xP, final Plan yP)
	{
		if( yP.Found ) {
			final ActionStateList xPlan = xP.FPlan;
			final ActionStateList yPlan = yP.FPlan;
			if( xPlan.numStates() + yPlan.numStates() < Ufway44.MaxStates ) {
				for( int i = 0; i < yPlan.numStates(); i++ ) {
					xPlan.append(yPlan.state(i), yPlan.action(i));
				}
			}
			xPlan.removeAllCicles();
		}
	}

	public void MultipleOptimizeLastUsedPlan(final State ActState, final int deep, final int Number)
	{
		if( LastUsedPlan != null ) {
			final int AcI = LastUsedPlan.GetNextStepIndex(ActState);
			if( AcI != -1 ) {
				LastUsedPlan.MultipleOptimizeFrom(AcI + 1, deep, Number);
			} else {
				LastUsedPlan.MultipleOptimizeFrom(0, deep, Number);
			}
		}
	}

	public void MultipleOptimize(final State ActState, final int deep, final int Number)
	{
		Arrays.stream(Plans).forEach( P -> {
			if( P.Found ) {
				if( P != LastUsedPlan ) {
					P.MultipleOptimizeFrom(0, deep, Number);
				} else {
					final int AcI = P.GetNextStepIndex(ActState);
					if( AcI != -1 ) {
						P.MultipleOptimizeFrom(AcI + 1, deep, Number);
					}
				}
			}
		});
	}

	public int Optimize(final State ActState, final int deep)
	{
		MultipleOptimize(ActState, deep, 1);
		return 0;
	}

}
