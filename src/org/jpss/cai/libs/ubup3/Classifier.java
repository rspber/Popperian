package org.jpss.cai.libs.ubup3;

import java.util.ArrayList;
import java.util.List;

import org.jpss.cai.libs.State;
import org.jpss.cai.libs.uabfun.CreateOperationSettings;
import org.jpss.cai.libs.uabfun.Operation;
import org.jpss.cai.libs.uabfun.RunOperation;
import org.jpss.cai.libs.uabfun.Tests;
import org.jpss.cai.libs.uabfun.UABFUN;
import org.jpss.cai.util.MM;

// part of UBUP3

class TLabeledState
{
//	private final int FLabel;
	final RunOperation FTester;

	TLabeledState( final int FLabel, final RunOperation FTester )
	{
//		this.FLabel = FLabel;
		this.FTester = FTester;
	}

}

//TClassifier 
public class Classifier extends StatePrediction
{

	private final List<TLabeledState> FStates;
	private int FNumClasses;
	private double FRandomProbability;

	public Classifier( boolean pZerosIncluded, int pNumOfNeurons, int pNumOfSearches, final CreateOperationSettings FCS, final boolean FGeneralize, final boolean FUseBelief )
	{
		super( pZerosIncluded, pNumOfNeurons, pNumOfSearches, FCS, FGeneralize, FUseBelief);
		FStates = new ArrayList<>();
	}

	public void AddClassifier(final int NumClasses, final int NumStates)
	{
		FNumClasses = NumClasses;
		FRandomProbability = 1.0 / FNumClasses;
		FStates.clear();
	}

	public void AddState(final int pLabel, final State pState)
	{
		FStates.add( new TLabeledState( pLabel, new RunOperation(FCS, pState, new State(1), new State(1) )));
	}

	private int EvolveNeuronGroupAtPos(final int neuronPos)
	{
		int result = -1;
		NeuronGroup NG = neuron(neuronPos);
		double BaseScore = NG.GetF();
		if( NG.CorrectNeuronPredictionCnt < FCS.MinSampleForPrediction ) {
			BaseScore = 0;
		}
//Write(' Start:', NG.CorrectNeuronPredictionCnt,'x',BaseScore:6:4, ' Size:', NG.TestNeuronLayerOps());
		for( int MutationCount = 1; MutationCount <= 10; MutationCount++ ) {
			final NeuronGroup Mutaded = this.MutateNeuronGroup(NG);
			final double NewScore = this.NeuronGroupFitness(Mutaded);
			if( NewScore > BaseScore && Mutaded.CorrectNeuronPredictionCnt > FCS.MinSampleForPrediction ) {
				setNeuron(neuronPos, Mutaded);
				NG = Mutaded;
				result = MutationCount;
//WriteLn(' Better: ', Mutaded.CorrectNeuronPredictionCnt,'x',
//NewScore:6:4, ' Exit@: ', MutationCount);
				BaseScore = NewScore;
				if( MutationCount > 0 ) {
					return result;
				}
			}
		}
//WriteLn(' Not Found!!!');
		return result;
	}

	private double NeuronGroupFitness(final NeuronGroup NG)
	{
		int SuccessCnt = 0;
		int TotalCnt = 0;
		if( FStates.size() > 0 ) {
			for( int StateCount = 0; StateCount <= 1000; StateCount++ ) {
				final int StatePos = MM.random( FStates.size() );
				if( FStates.get(StatePos).FTester.TestTests(NG.TestNeuronLayer) > 0 ) {
					TotalCnt++;
					if( FStates.get(StatePos).FTester.Test(NG.OperationNeuronLayer, NG.PredictionPos) > 0 ) {
						SuccessCnt++;
					}
				}
				if( SuccessCnt > 100 ) {
					break;
				}
			}
		}
		if( TotalCnt > FCS.MinSampleForPrediction ) { //(FNextFreePos/FMaxOperationNeuronCount)
			NG.CorrectNeuronPredictionCnt = SuccessCnt;
			NG.WrongNeuronPredictionCnt = TotalCnt - SuccessCnt;
			NG.Vitories = 0;
			NG.CorrectPredictionAtWin = 0;
			NG.WrongPredictionAtWin = 0;
			return (double)SuccessCnt / TotalCnt;
		}
		return 0;
	}

	private NeuronGroup MutateNeuronGroup( final NeuronGroup NG )
	{
		byte MutationType = (byte)MM.random( 3);
//0: delete
//1: add
//2: modify (both)
		final Tests tests = NG.TestNeuronLayer;
		if( tests.N() == 0 ) {
			MutationType = 1;
		}
		if( (tests.N() < FCS.MinTests || tests.N() == 1) && MutationType == 0 ) {
			MutationType = 2;
		}
		if( tests.N() == UABFUN.csMaxTests && MutationType == 1 ) {
			MutationType = 0;
		}
		if( MutationType == 0 ) {
			tests.RandomDeleteOperation();
		} else {
			if( MutationType == 1 ) {
				tests.AddTest(FStates.get(0).FTester.CreateActionRandomBinaryTest());
			} else {
				tests.RandomDeleteOperation();
				tests.AddTest(FStates.get(0).FTester.CreateActionRandomBinaryTest());
			}
		}
		final int N = tests.N();
		tests.TestThreshold = N > 10 ? N - (int)MM.random(N / 10.0) : N;
		return NG;
	}

	public void CreateRandomNeuronGroup(final int neuronPos, final int pClass)
	{
		final NeuronGroup ng = neuron(neuronPos);
		ng.RemoveOperations();
		ng.TestNeuronLayer.AddTest(FStates.get(0).FTester.CreateActionRandomBinaryTest());
		ng.TestNeuronLayer.TestThreshold = ng.TestNeuronLayer.N();
		ng.OperationNeuronLayer = new Operation(UABFUN.csSet, pClass, 0, false, false, false);
	}

	public byte PredictClass( final State PActions )
	{
		final RunOperation ABF = new RunOperation( FCS, PActions, new State(1), new State(1) );

		final double[] PossibleStates = new double[FNumClasses];
		for( int i = 0; i < PossibleStates.length; i++ ) {
			PossibleStates[i] = 0;
		}
		for( final NeuronGroup ng : FNN ) {
			final double Probability = ng.GetF();
			final int PredictionPosition = 0;
			if( ng.Filled() && Probability > 0.1 && ng.CorrectNeuronPredictionCnt > 10 ) {
				if( ABF.TestTests( ng.TestNeuronLayer ) > 0 ) {
					final byte NextState = (byte)ABF.getNextState( ng.OperationNeuronLayer, PredictionPosition);
					PossibleStates[NextState] = (Probability - FRandomProbability) + PossibleStates[NextState];
				}	
			}
		}	
		double Best = 0;
		for( int i = 0; i < PossibleStates.length && i <= 9; i++ ) {
//Write(' ',PossibleStates[i]:6:4 );
			if( PossibleStates[i] > Best ) {
				Best = PossibleStates[i];
				return (byte)i;
			}
		}
//WriteLn(' Best:', Result);
		return 0;
	} 

}
