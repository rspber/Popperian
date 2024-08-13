package org.jpss.cai.libs.ubup3;

import java.util.ArrayList;
import java.util.List;

import org.jpss.cai.libs.PState;
import org.jpss.cai.libs.State;
import org.jpss.cai.libs.uabfun.CreateOperationSettings;
import org.jpss.cai.libs.uabfun.CreateValidOperations;
import org.jpss.cai.libs.uabfun.Operation;
import org.jpss.cai.libs.uabfun.RunOperation;
import org.jpss.cai.util.MM;

/*
  part of UBUP3

  This class is one of the most important classes in all project.
  This class is capable of predicting the next state of an array of bytes.
*/
public class StatePrediction {

	public final CreateOperationSettings FCS;
	private final boolean fGeneralize;
	private final boolean fUseBelief;

	//These are arrays of NEURONS.
	protected final List<NeuronGroup> FNN;
	protected final int NumOfNeurons;

	// MAX NEURONS ON SECOND (OPERATION) NEURAL NETWORK LAYER
	//	private int FMaxOperationNeuronCount;

	// true = creates operation/neurons for all entries - 0 or non 0.
	private final boolean fZerosIncluded;
	private final int fNumSearch;
	private int fCycle;
	private boolean fFastPrediction;

	private final int[] SelectedIndexes;	// best prediction indexes
	private int NumberOfSelectedIndexes;	// number of best prediction indexes
	private final int NumMinSelectedIndexes;	// minimun usage number of best selected indexes

	public State fPredictedState;

	/*
	  This function creates the neural network that will later be used for prediction.
	  * The "number of searches" means the number of random attempts will be made in a given point in time.
	  * "number of searches" increases CPU time while "relation table size" increases memory.
	  * "zeros included" makes the system to use "zero" as an imput parameter. It makes the prediction
	    slower but more capable.
	*/
	public StatePrediction(
		final boolean pIncludeZeros,	// false = creates operation/neurons for non zero entries only.
		final int pNumOfNeurons,	// number of combinatorial NEURONS. If you don't know how many to crete, give 200.
		final int pNumOfSearches,	// the higher the number, more computations are used on each step. If you don't know what number to use, give 40.
		final CreateOperationSettings FCS,
		final boolean FGeneralize,
		final boolean FUseBelief
	) {
		super();
		fZerosIncluded = pIncludeZeros;
		SelectedIndexes = new int[pNumOfNeurons];
		FNN = new ArrayList<NeuronGroup>();
		this.FCS = FCS;
		for( int i = 0; i < pNumOfNeurons; ++i ) {
			FNN.add(new NeuronGroup());
		}
		NumOfNeurons = pNumOfNeurons;
		NumberOfSelectedIndexes = 0;
		fNumSearch = pNumOfSearches;
		this.fGeneralize = FGeneralize;
		this.fUseBelief = FUseBelief;
		NumMinSelectedIndexes = 10;
		fFastPrediction = false;
	}

	public NeuronGroup neuron(final int pos)
	{
		return FNN.get(pos);
	}

	public void setNeuron(final int pos, final NeuronGroup ng)
	{
		FNN.set(pos, ng);
	}

	// This function clears all memory from past learning and all stored relations.
	// This function causes a "reset" in the learning.
	public void ClearAll()
	{
		fCycle = 0;
		for( NeuronGroup ng : FNN ) {
			ng.RemoveOperations();
		}
	}

	// Same as ClearCountingAtPos but for all relations.
	public void RemoveAllNeurons()
	{
		for( final NeuronGroup ng : FNN ) {
			ng.RemoveOperations();
		}
	}

	// This function updates statistics based on the current state / actions and found states.
	// This function can be seen as a function responsible for learning.
	public void UpdatePredictionStats(final State pActions, final State pCurrentStates, final State pFoundStates) 
	{
		RunOperation ABF = new RunOperation(FCS, pActions, pCurrentStates, pFoundStates);
		FNN.stream().forEach( ng -> {
			if( ABF.TestTests(ng.TestNeuronLayer) > 0 ) {
				ng.incNeuronPredictionCnt(ABF.Test(ng.OperationNeuronLayer, ng.PredictionPos) != 0 );
			}}
		);
	}

	// This function returns the probability to win of a given neuron from position pos.
	private double ProbToWin(final int neuronPos)
	{
		final NeuronGroup ng = neuron(neuronPos);
		return ng.predictionProbabilityAtWin();
	}

	// CreateNewNeuronsOnError.
		// This function returns the worst neuron index.
		//Searches for the worst neuron;
		//  1 - empty neuronal position,
		//  2 - smallest number of victories
		// GetWorstNeuronIndex
		private NeuronGroup GetWorstNeuron(final int searchedNeuronsCnt)
		{
			int posWorst = MM.random(NumOfNeurons );
			double worst = neuron(posWorst).GetNeuronGroupScore();
			for( int i = 1; i <= searchedNeuronsCnt; i++ ) {
				final int neuronPos = MM.random(NumOfNeurons );
				final double actual = neuron(neuronPos).GetNeuronGroupScore();
				if( actual < worst ) {
					worst = actual;
					posWorst = neuronPos;
				}
			}
			return neuron(posWorst);
		}

	// CreateNewNeuronsOnError.
		// GetBestNeuronIndex
		private NeuronGroup GetBestNeuron(final int Num, final CreateValidOperations ABF)
		{
			for( int i = 1; i <= Num; i++ ) {
				final int neuronPos = MM.random(NumOfNeurons );
				final NeuronGroup ng = neuron(neuronPos);
				// EvalNeuronGroup
				if( ABF.TestTests(ng.TestNeuronLayer) > 0 ) {
					if( ABF.Test(ng.OperationNeuronLayer, ng.PredictionPos) != 0 ) {
						if( ng.Filled() && ng.GetD() > 0.8 ) {
							return ng;
						}
					}
				}
			}
			return null;
		}

	// This function creates new relations given a prediction error.
	public void CreateNewNeuronsOnError(final State PActions, final State PCurrentStates, final State PNextStates)
	{
		final NeuronGroup worst = GetWorstNeuron(fNumSearch);
		worst.RemoveOperations();
//		 ABGetDif(Pred, Pred, PNextStates);
//		  PredictedBytePos := ABGetNext1(pred, random(Length(pred)));
		final int PredictedBytePos = fPredictedState.randomGetNext1(PNextStates);

		final CreateValidOperations NewOp = new CreateValidOperations(FCS, PActions, PCurrentStates, PNextStates);
		NewOp.LoadCreationData(PredictedBytePos);

		// select a predicted byte that was wrongly predicted.
		worst.PredictionPos = PredictedBytePos;
		worst.TestNeuronLayer.testBasePosition = FCS.Bidimensional ? NewOp.GetFeatureCenter2D() : PredictedBytePos;
		if( MM.random(100) < 5 && fGeneralize ) {
			final NeuronGroup best = GetBestNeuron(fNumSearch, NewOp);
			if( best != null && best != worst ) {
				// should copy the neuron ?
				worst.copyFrom(best);
				worst.TestNeuronLayer.randomDeleteOperation();
				return;
			}
		}

		NewOp.CreateOperations(false, fZerosIncluded/*, pred*/);	//no tests
		//prediction errors
		if( NewOp.operationsCount() == 0 ) {
			System.out.println(" ERROR: relation creation has failed.");
		} else {
			worst.RemoveOperations();
			worst.OperationNeuronLayer = NewOp.GetRandomOper();
			final int NumV = FCS.MinTests + MM.random(FCS.MaxTests - FCS.MinTests) + 1;
			NewOp.CreateOperations(true, fZerosIncluded/*, pred*/);	//with tests
			//prediction errors
			for( int IV = 0; IV < NumV; IV++ ) {
				final Operation Oper = NewOp.GetRandomOper();
				if( Oper.OpCode == 0 ) {
					break;
				}
				worst.TestNeuronLayer.addTest(Oper);
			}
			final int N = worst.TestNeuronLayer.N();
			worst.TestNeuronLayer.testThreshold = FCS.PartialTestEval ? MM.random(N) + 1 : N;
		}
	}

	// clear victory countings
	public void ClearNeuronVictories()
	{
		for( final NeuronGroup ng : FNN ) {
			ng.Vitories = 0;
		}
	}

	public void deleteRarelyUsedNeuros(final int minPredictions)
	{
		for( final NeuronGroup ng : FNN ) {
			if( ng.CorrectNeuronPredictionCnt < minPredictions ) {
				ng.RemoveOperations();
			}
		}
	}

	public void deleteNeverWinningNeurons(final int minVictories)
	{
		for( final NeuronGroup ng : FNN ) {
			if( ng.Vitories < minVictories ) {
				ng.RemoveOperations();
			}
		}
	}
/*
	public void deleteDuplicateNeurons()
	{
		final Set<String> NeuronList = new HashSet<>();
		for( final NeuronGroup ng : FNN ) {
			if( ng.Filled() ) {
				final String NeuronStr = ng.GetUniqueString();
				if( NeuronList.contains(NeuronStr) ) {
					ng.RemoveOperations();
				} else {
					NeuronList.add(NeuronStr);
				}
			}
		}
	}
*/
	public int getVictoriousNeuronsCnt()
	{
		int victoriousNeuronsCnt = 0;

		for( final NeuronGroup ng : FNN ) {
			if( ng.Vitories > 0 ) {
				victoriousNeuronsCnt++;
			}
		}
		return victoriousNeuronsCnt;
	}

	// This function might be one of the most important in all code.
	// Based on Actions and currents states, next states are predicted. For each
	// predicted state in the output array (PNextStates), the probability is given
	// by pRelationProbability. The relation index used when predicting is stored in
	// pVictoryIndex.
	public void Prediction(final State pActions, final State pCurrentState, final /*var*/ PState pNextStates,
		final /*var*/ double[] pRelationProbability,
		final /*var*/ int[] pVictoryIndex) // index of victorious neuron 
	{
		State states = pCurrentState;	// LOOK
		final RunOperation ABF = new RunOperation(FCS, pActions, pCurrentState, pNextStates.states);
		for( int i = 0; i < pRelationProbability.length; i++ ) {
			pRelationProbability[i] = 0;
			pVictoryIndex[i] = -1;
		}
		final int MaxIndexP1 = fFastPrediction ? NumberOfSelectedIndexes : NumOfNeurons;
		for( int j = 0; j < MaxIndexP1; j++ ) {
			final int i = fFastPrediction ? SelectedIndexes[j] : j;
			final NeuronGroup ng = neuron(i);
			final double Probability = ng.predictionProbability1(fUseBelief);
			final int PredictionPos = ng.PredictionPos;
			if( Probability > pRelationProbability[PredictionPos] && ng.CorrectNeuronPredictionCnt > FCS.MinSampleForPrediction ) {
				if( ABF.TestTests(ng.TestNeuronLayer ) > 0 ) {
					final byte NextState = (byte)ABF.getNextState(ng.OperationNeuronLayer, PredictionPos);
					states = states.setState(PredictionPos, NextState);
					pRelationProbability[PredictionPos] = Probability;
					pVictoryIndex[PredictionPos] = i;
				}
			}
		}
		pNextStates.states = states;
	}

	// This function is similar to Prediction but doesn't touch "next states" array.
	// This function calculates the probability of each next state but doesn't calculate the state.
	private void PredictionProbability(final State pActions, final State pCurrentState, final PState pNextStates,
		// efeitos a serem medidos
		// output
		final /*var*/ double[] pRelationProbability,	//probabilidades	//probabilities
		final /*var*/ int[] pVictoryIndex)	//posicao do vitorioso	//victory position
	{
		final RunOperation ABF = new RunOperation(FCS, pActions, pCurrentState, pNextStates.states);
		for( int i = 0; i < pRelationProbability.length; i++ ) {
			pRelationProbability[i] = 0;
			pVictoryIndex[i] = -1;
		}
		for( int i = 0; i < NumOfNeurons; i++ ) {
			final NeuronGroup ng = neuron(i);
			final int PredictionPosition = ng.PredictionPos;
			if( ng.Filled() ) {
				if( ABF.Test(ng.OperationNeuronLayer, PredictionPosition) != 0 ) {
					if( ABF.TestTests(ng.TestNeuronLayer) > 0 ) {
						final double Probability = ng.predictionProbability2(fUseBelief);
						if( Probability > pRelationProbability[PredictionPosition] ) {
							pRelationProbability[PredictionPosition] = Probability;
							pVictoryIndex[PredictionPosition] = i;
						}
					}
				}
			}
		}
	}

	// This function returns all relation indexes with a minimun number of victories (selections)
	// and a minimum probability MinF.
	private void SelectBestIndexes(final int MinimumNumberOfVictories, final double MinF)
	{
		fFastPrediction = true;
		NumberOfSelectedIndexes = 0;

		for( int i = 0; i < NumOfNeurons; i++ ) {
			final NeuronGroup ng = neuron(i);
			final double Probability = ng.predictionProbability2(fUseBelief);
			if( MinimumNumberOfVictories <= ng.Vitories && MinF <= Probability ) {
				SelectedIndexes[NumberOfSelectedIndexes] = i;
				NumberOfSelectedIndexes++;
			}
		}
		fFastPrediction = fFastPrediction && (NumberOfSelectedIndexes >= NumMinSelectedIndexes);
	}

	private void ResumeSlowPrediction()
	{
		fFastPrediction = false;
	}

	// Defrag all neurons from start to end. Returns the first empty space.
	public int Defrag()
	{
		int result = 0;
		for( int i = 0; i < NumOfNeurons; i++ ) {
			final NeuronGroup ng = neuron(i);
			if( ng.Filled() ) {
				if( i > result ) {
					neuron(result).copyFrom(ng);
//TODO
					ng.TestNeuronLayer.removeOperations();
				}
				result++;
			}
		}
		return result;
	}

	// update victory countings based on the found state and the predicted state.
	// winner's position
	//positions of victorious neurons 
	// found state is compared to the predicted state.
	public void UpdateNeuronVictories(final State pFoundStates, final int[] pVictoryIndex)
	{
		fCycle++;
		for( int i = 0; i < pFoundStates.length(); i++ ) {
			int IVict = pVictoryIndex[i];
			if( IVict != -1 ) {
				if( pFoundStates.state(i) == fPredictedState.state(i) ) {
					neuron(IVict).victory(fCycle);
				} else {
					neuron(IVict).defeat();
				}
			}
		}
	}

}
