package org.jpss.cai.libs.ubup3;

// part of UBUP3

//TNeuronGroupBase 
public class NeuronGroupBase
{
	// Where in the output array this neuron group predicts a state
	int PredictionPos;

	// Number of Neuron Group Victories
	protected int Vitories;
	protected int LastVictory;

	// neuron prediction countings
	protected int WrongNeuronPredictionCnt;
	protected int CorrectNeuronPredictionCnt;

	// neuron prediction countings at win (at victory)
	int WrongPredictionAtWin;
	int CorrectPredictionAtWin;

	public void victory(final int VCount)
	{
		Vitories++;
		CorrectPredictionAtWin++;
		LastVictory = VCount;
	}

	public void defeat()
	{
		Vitories--;
		WrongPredictionAtWin++;
	}

	public void incNeuronPredictionCnt(final boolean ok)
	{
		if( ok ) {
			CorrectNeuronPredictionCnt++;
		} else {
			WrongNeuronPredictionCnt++;
		}
	}

	// returns Correct Prediction / Total = Correct Prediction Frequency
	public double GetF()
	{
		final int T = WrongNeuronPredictionCnt + CorrectNeuronPredictionCnt;
		return T > 0 ? (double)CorrectNeuronPredictionCnt / T : 0;
	}

	// returns (m+1)/(n+2)
	public double GetD()
	{
		final int T = WrongNeuronPredictionCnt + CorrectNeuronPredictionCnt;
		return (double)CorrectNeuronPredictionCnt / (T + 2);
	}
/*
	// creates a string for storage
	public String toString()
	{
		return PredictionPos + ">" + Vitories + ">" + LastVictory + ">" + WrongNeuronPredictionCnt + ">" + CorrectNeuronPredictionCnt + ">" + WrongPredictionAtWin + ">" + CorrectPredictionAtWin;
	}
*/
	public void Clear()
	{
		WrongNeuronPredictionCnt = 0;
		CorrectNeuronPredictionCnt = 0;
		WrongPredictionAtWin = 0;
		CorrectPredictionAtWin = 0;
		Vitories = 0;
		LastVictory = 0;
	}

}

