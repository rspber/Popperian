package org.jpss.cai.simplest;

import org.jpss.cai.libs.PState;
import org.jpss.cai.libs.State;
import org.jpss.cai.libs.ubup3.EasyLearnAndPredict;

public class countTo9NeuralNetworkExample
{
	private final static int secondNeuralNetworkLayerSize = 10;	// 1000 neurons on second layer.
	private final static int internalStateSize = 1;	//  the internal state is composed by 1 byte.
	private final static int stateSize = 2;	//  the current and next states are composed by 1 byte.

//SIMPLEST NEURAL NETWORK EXAMPLE:
//=====================================
// In this example, the NN will learn how to count from 0 to 9 and restart.
	public static void main( String[] args )
	{
		final EasyLearnAndPredict FNeural = new EasyLearnAndPredict(
			internalStateSize, stateSize,
			false,
			secondNeuralNetworkLayerSize,
			40,	//search size
			false,	//use cache
			false, false
		);
		final State aInternalState = new State(internalStateSize);
		State aCurrentState = new State(stateSize);
		final PState aPredictedState = new PState();

// INCLUDE YOUR CODE HERE: some code here that updates the internal and current states.
		int error_cnt = 0;
		System.out.println("Starting...");
		for( int I = 1; I <= 10000; I++ ) {
			// predicts the next state from aInternalState, aCurrentState into aPredictedState
			FNeural.Predict(aInternalState, aCurrentState, aPredictedState);
			// INCLUDE YOUR CODE HERE: some code here that updates the next state.
			State.deref(aCurrentState); aCurrentState = aCurrentState.setAction( (byte)((aCurrentState.state(0) + 1) % 10) );
			// INCLUDE YOUR CODE HERE: some code here that compares aPredictedState with new next state.
			if( !aCurrentState.eq(aPredictedState.states) ) {
				// INCLUDE YOUR CODE HERE: if predicted and next states don't match,
				error_cnt++;
			}
			// This method is responsible for training. You can use the same code for
			// training and actually predicting.
			FNeural.newStateFound(aCurrentState);
		}
		// The smaller the number of errors, the faster the NN was able to learn.
		System.out.println("Finished. Errors found:" + error_cnt);
	}

}
