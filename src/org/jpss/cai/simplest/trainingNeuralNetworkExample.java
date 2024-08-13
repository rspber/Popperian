package org.jpss.cai.simplest;

import org.jpss.cai.libs.PState;
import org.jpss.cai.libs.State;
import org.jpss.cai.libs.ubup3.EasyLearnAndPredict;

public class trainingNeuralNetworkExample
{
	private static final int secondNeuralNetworkLayerSize = 1000;	// 1000 neurons on second layer.
	private static final int internalStateSize = 5;	//  the internal state is composed by 5 bytes.
	private static final int stateSize = 10;	//  the current and next states are composed by 10 bytes.

//INTRODUCTORY NEURAL NETWORK EXAMPLE:
//=====================================
	public static void main(String[] args)
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
		PState aPredictedState = new PState();

		// INCLUDE YOUR CODE HERE: some code here that updates the internal and current states.
		int error_cnt = 0;
		for( int I = 1; I <= 10000; I++ ) {
			// predicts the next state from aInternalState, aCurrentState into aPredictedState
			FNeural.Predict(aInternalState, aCurrentState, aPredictedState);
			// INCLUDE YOUR CODE HERE: some code here that updates the next state.
			aCurrentState = aCurrentState.setAction((byte)((aCurrentState.state(0) + 5) % 10));
			// INCLUDE YOUR CODE HERE: some code here that compares aPredictedState with new next state.
			if( !aCurrentState.eq(aPredictedState.states) ) {
				// INCLUDE YOUR CODE HERE: if predicted and next states don't match,
				error_cnt++;
			}
			// INCLUDE YOUR CODE HERE: if predicted and current states don't match, then inc(error_cnt);
			// This method is responsible for training. You can use the same code for training and actually predicting.
			FNeural.newStateFound(aCurrentState);
		}
		System.out.println(error_cnt);
	}

}
