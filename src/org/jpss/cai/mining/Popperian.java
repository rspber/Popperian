package org.jpss.cai.mining;

import org.jpss.cai.libs.PState;
import org.jpss.cai.libs.State;
import org.jpss.cai.libs.ubup3.EasyLearnAndPredict;

public class Popperian extends MiningAgent {

	final ArtificialMiningWorld FMiningWorld;

	public Popperian(final ArtificialMiningWorld FMiningWorld, final EasyLearnAndPredict LearnAndPredict, final Forms forms)
	{
		super(LearnAndPredict, forms);
		this.FMiningWorld = FMiningWorld;
	}

	private final byte[] bb = new byte[UFRob1.csStateByteLength];

	private State prepNextStates()
	{
		final Position RUNNER = FMiningWorld.RUNNER();
		bb[0] = (byte)0;
		bb[1] = (byte)Math.round(RUNNER.x);
		bb[2] = (byte)Math.round(RUNNER.y);
		bb[3] = (byte)RUNNER.Charge;
		bb[4] = FMiningWorld.BASE().Encounter(RUNNER) ? (byte)1 : 0;
		bb[5] = FMiningWorld.EncounterMINE(RUNNER) ? (byte)1 : 0;
		bb[6] = (byte)(UFRob1.FWorldCenter + 1);	// pos base
		bb[7] = (byte)0;
		return new State(bb);
	}

	PState predictedStates = new PState();

	// Popperian
	int MoveNeuralAgent()
	{
		int r = 0;
		State localStates = prepNextStates();

		r = r + LearnAndPredict.newStateFound(localStates);

		final byte localAction = ChooseActionAndPlan(localStates);
		PreviousAction = localAction;

		localStates = localStates.cloneAndSetAction((byte)(localAction + 1));

		State.deref(predictedStates.states); predictedStates.states = new State();
		LearnAndPredict.Predict(localStates, localStates, predictedStates );

		if( localAction !=  URobMin2.csHold ) {
			FMiningWorld.move(FMiningWorld.RUNNER(), localAction );
		}
		State.deref(PreviousStates); PreviousStates = localStates.cloneAndSetAction((byte)0);

		return r;
	}

}
