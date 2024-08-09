package org.jpss.cai.libs.ubup3;

import org.jpss.cai.libs.PState;
import org.jpss.cai.libs.State;
import org.jpss.cai.libs.uabfun.UABFUN;
import org.jpss.cai.libs.ucashst2.CacheMem;

/*
  part of UBUP3

  This class is an AMAZING class. This class is capable of learning and predicting
  next states with easy to use methods. Use this class on your projects
*/
public class EasyLearnAndPredict extends Classifier {

	private State fActions, fCurrentState;
	private final double[] fRelationProbability;
	private final int[] fVictoryIndex;
	// array with indexes of neurons that predicted each output state

	private boolean fCached;
	private final boolean fUseCache;
	private final CacheMem fCache;

	// Defines size of neural network, size of input, size of output and flags.
	public EasyLearnAndPredict(
		final int actionByteLen,	// action array size in bytes
		final int stateByteLen,		// state array size in bytes
		final boolean includeZeros,	// false = creates operation/neurons for non zero entries only.
		final int numOfNeurons,		// number of combinatorial NEURONS. If you don't know how many to crete, give 200.
		final int numOfSearches,	// the higher the number, more computations are used on each step. If you don't know what number to use, give 40.
		final boolean useCache,		// replies the same prediction for the same given state. Use false if you aren't sure.
		final boolean generalize,
		final boolean useBelief
	) {
		super(includeZeros, numOfNeurons, numOfSearches, UABFUN.csCreateOpDefault, generalize, useBelief);
		fActions = new State();
		fCurrentState = new State();
		fPredictedState = new State();
		fRelationProbability = new double[stateByteLen];
		fVictoryIndex = new int[stateByteLen];
		if( useCache ) {
			fCache = new CacheMem(actionByteLen, stateByteLen);
		} else {
			fCache = new CacheMem(1, 1);
		}
		fUseCache = useCache;
	}

	// THIS METHOD WILL PREDICT THE NEXT STATE GIVEN AN ARRAY OF ACTIONS AND STATES.
	// You can understand ACTIONS as a kind of "current state".
	// Returned value "predicted states" contains the neural network prediction.
	public void Predict(final State actions, final State currentState, final /*var*/PState predictedState)
	{
		State.deref(fActions); fActions = actions.clone();
		State.deref(fCurrentState); fCurrentState = currentState.clone();
		fCached = false;
		if( fUseCache ) {
			final State state = fCache.Read(fActions);
			if( state != null ) {
				if( fActions.eq(fCurrentState) ) {
					State.deref(predictedState.states); predictedState.states = state.clone();
					fCached = true;
				}
			}
		}
		if( !fCached ) {
			Prediction(fActions, fCurrentState, predictedState, /*var*/fRelationProbability, /*var*/fVictoryIndex);
		}
		State.deref(fPredictedState); fPredictedState = predictedState.states.clone();
	}


	// Call this method to train the neural network so it can learn from the "found state".
	// Call this method and when the state of your environment changes so the neural
	// network can learn how the state changes from time to time.
	public int newStateFound(final State stateFound)
	{
		final int predictionError = fPredictedState.countDif(stateFound );
		// Do we have a cached prediction and was the prediction correct?
		if( !fCached || predictionError != 0 ) {
			 // was the prediction wrong?, then forgets the cache and recalculate
			UpdatePredictionStats(fActions, fCurrentState, stateFound );
			if( !fCached ) {
				UpdateNeuronVictories(stateFound/*current*/, fVictoryIndex );
			}
			// compares predicted state with found state.
			if( fPredictedState.countDif(stateFound) != 0 ) {
				for( int j = 1; j <= 1; j++ ) {
					CreateNewNeuronsOnError(fActions, fCurrentState, stateFound );
				}
			}
		}
		if( fUseCache ) {
			fCache.Include(fActions, stateFound);
		}
		return predictionError;
	}
/*
	// returns m/n = frequency
	private double GetF(final int posPredictedState)
	{
		final int IV = FVictoryIndex[posPredictedState];
		return IV >= 0 ? getNeuron(IV).GetF() : 0;
	}

	// returns (m+1)/(n+2) = confidence.
	private double GetD(final int posPredictedState)
	{
		final int IV = FVictoryIndex[posPredictedState];
		return IV >= 0 ? getNeuron(IV).GetD() : 0;
	}
*/

}
