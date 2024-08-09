package org.jpss.cai.libs.ubup3;

import org.jpss.cai.libs.uabfun.Operation;
import org.jpss.cai.libs.uabfun.Tests;

// part of UBUP3

//NeuronGroup 
public class NeuronGroup extends NeuronGroupBase {

	// FIRST NEURAL NETWORK LAYER: testing layer - test are <, >, =, ...
	final Tests TestNeuronLayer;

	// SECOND NEURAL NETWORK LAYER: A+B, A-B, A AND B, ...
	Operation OperationNeuronLayer;

 	public NeuronGroup()
	{
		TestNeuronLayer = new Tests();
		OperationNeuronLayer = Operation.Null;
	}

	// Returns the neuron group score. Better neurons survive.
	public double GetNeuronGroupScore()
	{
		final double r;

		if( TestNeuronLayer.N() == 0 ) {
			r = -100;
		} else {
			r = Vitories;
		}
//r := GetF();
		return r;
	}

	public void RemoveOperations()
	{
		TestNeuronLayer.removeOperations();
		OperationNeuronLayer = Operation.Null;
		Clear();
	}

	public void copyFrom(final NeuronGroup t)
	{
		Clear();
		TestNeuronLayer.copyFrom(t.TestNeuronLayer);
		OperationNeuronLayer = t.OperationNeuronLayer;
		PredictionPos = t.PredictionPos;
	}

	// returns true if there is a valid group
	public boolean Filled()
	{
		return this.TestNeuronLayer.N() > 0;
	}

	public double predictionProbability1(final boolean FUseBelief)
	{
		final int TotalCount = WrongNeuronPredictionCnt + CorrectNeuronPredictionCnt;
		if( TotalCount > 0 && Filled() ) {
			if( FUseBelief ) {
				return (double)(CorrectNeuronPredictionCnt + 1) / (TotalCount + 2);
			} else {
				return (double)CorrectNeuronPredictionCnt / TotalCount;	// best method
			}
		} else {
			return 0;
		}
	}

	public double predictionProbability2(final boolean FUseBelief)
	{
		final int TotalCount = WrongNeuronPredictionCnt + CorrectNeuronPredictionCnt;
		if( TotalCount > 0 ) {
			if( FUseBelief ) {
				return (double)(CorrectNeuronPredictionCnt + 1) / (TotalCount + 2);
			} else {
				return (double)CorrectNeuronPredictionCnt / TotalCount;	// best method
			}
		} else {
			return 0;
		}
	}

	public double predictionProbabilityAtWin()
	{
		int TotalCount = WrongPredictionAtWin + CorrectPredictionAtWin;
		if( TotalCount > 0 ) {
			return (double)CorrectPredictionAtWin / TotalCount;
		} else {
			TotalCount = WrongNeuronPredictionCnt + CorrectNeuronPredictionCnt;
			if( TotalCount > 0 ) {
				return ((double)CorrectNeuronPredictionCnt / TotalCount) / 100;
			}
			else {
				return 0;
			}
		}
	}

}
