package org.jpss.cai.libs.uabfun;

import java.util.ArrayList;
import java.util.List;

import org.jpss.cai.libs.State;
import org.jpss.cai.mining.UFRob1;
import org.jpss.cai.util.MM;

//CreateValidOperations 
public class CreateValidOperations extends RunOperation
{
	private final List<Operation> FOperations = new ArrayList<>();
	private int FBasePosition;
	private int FPredictedBytePos;
	private int FFeatureCenter2D;

	public CreateValidOperations(final CreateOperationSettings PCS,
		final State Actions,
		final State CurrentStates,
		final State NextStates
	) {
		super(PCS, Actions, CurrentStates, NextStates);
	}

	// Returns the number of current valid operations in FOperations.
	public int FOperationsCount()
	{
		return FOperations.size();
	}

	private boolean CanInclude()
	{
		final boolean r = FOperations.size() < UABFUN.csMaxOperationsArray - 1;
		if( !r ) {
			System.out.println("Warning: can not include operation.");
		}
		return r;
	}

	public void LoadCreationData(final int PredictedBytePos)
	{
/*
		if( FCS.MaxTests > csMaxTests ) {
			FCS.MaxTests = csMaxTests;
		}
*/
		FPredictedBytePos = PredictedBytePos;
		FFeatureCenter2D = FCS.Bidimensional ? FCS.CreateFeatureCenter() : 0;
	}

	private void Include(final byte OpCode, int Op1, int Op2, boolean RelOp1, boolean RelOp2, final boolean RunOnAction)
	{
		if( UABFUN.inImediatSet(OpCode) ) {
			RelOp1 = false;
			RelOp2 = false;
		}
		if( UABFUN.inFirstImediatSet(OpCode) ) {
			RelOp1 = false;
		}
		if( RelOp1 ) {
			Op1 = Op1 - FBasePosition;
		}
		if( RelOp2 ) {
			Op2 = Op2 - FBasePosition;
		}
		final Operation Oper = new Operation(OpCode, Op1, Op2, RelOp1, RelOp2, RunOnAction);

		//if the operation has the predicted state (returns true), include it.
		if( Test(Oper, FBasePosition) != 0 ) {
			//include the new operation in FOperations
			FOperations.add(Oper);
		}
	}

		// This function returns an array with all non zero elements
		private int[] getNonZeroElementsPos( final byte[] InputData )
		{
			int n = 0;
			for( int i = 0; i < InputData.length; i++ ) {
				if( InputData[i] != 0 ) {
					n++;
				}
			}
			final int[] OutputData = new int[n];
			int j = 0;
			for( int i = 0; i < InputData.length; i++ ) {
				if( InputData[i] != 0 ) {
					OutputData[j++] = i;
				}
			}
			return OutputData;
		}

//  Create
//  This function creates valid operations and includes them into FOperations.
//  Tests: field indicates if tests/conditions should be included.
//  FullEqual: indicates if all input data should be used. false means only non zero values will be used.
//  ERRORS: prediction error are used to create better operations.
//  
			private int[] LocalNonZeroPrevStates;
			private byte[] LocalPreviousStates;
			private boolean OnAction;
			private int RunOnActionFlag;

			private void IncludeEqual()
			{
				final int MJ;
				if( LocalPreviousStates.length > UABFUN.csMaxTests ) {
					MJ = FCS.MaxTests;
				}
				else {
					MJ = LocalPreviousStates.length;
				}
				for( int j = 1; j <= MJ; j++ ) {
					final int ElementPosition = MM.random(LocalPreviousStates.length);
					if( CanInclude() ) {
						Include( UABFUN.csEqual, LocalPreviousStates[ElementPosition], ElementPosition, false, false, OnAction);
					} else {
						return;
					}
				}
			}
	
			private void IncludeEqualForNonZero()
			{
				if( LocalNonZeroPrevStates.length > 0 ) {
					final int MJ;
					if( LocalNonZeroPrevStates.length > UABFUN.csMaxTests ) {
						MJ = FCS.MaxTests;
					}
					else {
						MJ = LocalNonZeroPrevStates.length + 1;
					}
					for( int j = 1; j <= MJ; j++ ) {
						final int ElementPosition = LocalNonZeroPrevStates[MM.random(LocalNonZeroPrevStates.length)];
						final byte Value = LocalPreviousStates[ElementPosition];
						if( Value == 0 ) {
							System.out.println("ERROR: IncludeEqualForNonZero created zero equal: " + ElementPosition);
						}
						if( CanInclude() && Value != 0 ) {
							Include(UABFUN.csEqual, Value, ElementPosition, false, false, OnAction);
						} else {
							return;
						}
					}
				}
			}
	
			private void IncludeBinaryTestsForNonZero()
			{
				if( LocalNonZeroPrevStates.length >= 2 ) {
					final int MI = FCS.MaxTests;
					for( int i = 0; i < MI; i++ ) {
						final int NZPos1 = MM.random(LocalNonZeroPrevStates.length);
						final int Pos1 = LocalNonZeroPrevStates[NZPos1];
						final int Pos2;
						if( FCS.Bidimensional ) {
							Pos2 = FCS.GetRandom2DPos(Pos1);
						} else {
							final int NZPos2 = MM.random(LocalNonZeroPrevStates.length);
							Pos2 = LocalNonZeroPrevStates[NZPos2];
						}
						final int Val1 = LocalPreviousStates[Pos1];
						final int Val2 = LocalPreviousStates[Pos2];
						if( Val1 == 0 || Val2 == 0 ) {
							System.out.println("ERROR: IncludeBinaryTestsForNonZero created zero equal: " + Val1 + " " + Val2);
						}
						if( CanInclude() && Pos1 != Pos2 ) {
							Include(UABFUN.ValidBinaryCode(Val1, Val2), Pos1, Pos2, true, true, OnAction);
						}
					}
				}
			}
	
			private void IncludeBinaryTests()
			{
				if( LocalPreviousStates.length >= 2 ) {
					final int MI = FCS.MaxTests;
					for( int i = 0; i < MI; i++ ) {
						final int Pos1, Pos2;
						if( FCS.Bidimensional ) {
							Pos1 = FCS.GetRandom2DPos(FBasePosition);
							Pos2 = FCS.GetRandom2DPos(FBasePosition);
						} else {
							Pos1 = MM.random(LocalPreviousStates.length);
							Pos2 = MM.random(LocalPreviousStates.length);
						}
						final int Val1 = LocalPreviousStates[Pos1];
						final int Val2 = LocalPreviousStates[Pos2];
						if( CanInclude() && (Pos1 != Pos2) ) {
							Include(UABFUN.ValidBinaryCode(Val1, Val2), Pos1, Pos2, true, true, OnAction);
						}
					}
				}
			}
	
			private void IncludeBinaryOperationsForNonZero()
			{
				if( LocalNonZeroPrevStates.length >= 2 ) {
					final int i = MM.random(LocalNonZeroPrevStates.length);
					final int j = MM.random(LocalNonZeroPrevStates.length);
					final int k = MM.random(UABFUN.csBinaryOperations.length);
					if( CanInclude() && i != j ) {
						Include(UABFUN.csBinaryOperations[k], LocalNonZeroPrevStates[i], LocalNonZeroPrevStates[j], false, false, OnAction);
					}
				}
			}
	
			private void IncludeBinaryOperations()
			{
				if( LocalPreviousStates.length >= 2 ) {
					final int i = MM.random(LocalPreviousStates.length);
					final int j = MM.random(LocalPreviousStates.length);
					final int k = MM.random(UABFUN.csBinaryOperations.length);
					if( CanInclude() && i != j ) {
						Include(UABFUN.csBinaryOperations[k], i, j, false, false, OnAction);
					}
				}
			}
	
			private void PSet()
			{
				if( CanInclude() ) {
					Include(UABFUN.csSet, NextStates.state(FPredictedBytePos), 0, false, false, false);
				}
			}
	
			public void CreateOperations( final boolean Tests, final boolean FullEqual/*, final byte[] ERRORS*/)
			{
				FOperations.clear();
		//		NonZeroErrors = getNonZeroElementsPos(ERRORS);
				if( !FCS.TestOnStates ) {
					RunOnActionFlag = 1;
				} else {
					if( !FCS.TestOnActions ) {
						RunOnActionFlag = 0;
		// 50% of change that operations will be based on actions or on states.
					} else {
						RunOnActionFlag = MM.random(2);
					}
				}
				LocalPreviousStates = new byte[UFRob1.csStateByteLength];
		// should we use action or previous states for inference?
				if( RunOnActionFlag > 0 ) {
					Actions.getBytes(LocalPreviousStates);
					OnAction = true;
				} else {
					CurrentStates.getBytes(LocalPreviousStates);
					OnAction = false;
				}
				LocalNonZeroPrevStates = getNonZeroElementsPos(LocalPreviousStates);
				FBasePosition = FPredictedBytePos;
				if( Tests ) {
					if( FCS.Bidimensional ) {
						FBasePosition = FFeatureCenter2D;
					}
					if( FullEqual || FCS.Bidimensional ) {
						if( FCS.AddEqualTest ) {
							IncludeEqual();
						}
						if( FCS.AddBinaryTest ) {
							IncludeBinaryTests();
						}
					} else {
						if( FCS.AddEqualTest ) {
							IncludeEqualForNonZero();
						}
						if( FCS.AddBinaryTest ) {
							IncludeBinaryTestsForNonZero();
						}
					}
					if( FCS.AddTrueTest ) {
						Include(UABFUN.csTrue, 0, 0, false, false, false);
					}
				} else {
					if( FCS.AddSetOp ) {
						PSet();
					}
					if( FCS.AddBinaryOp ) {
						if( FullEqual ) {
							IncludeBinaryOperations();
						} else {
							IncludeBinaryOperationsForNonZero();
						}
					}
					if( FCS.AddIncOp ) {
						Include(UABFUN.csInc, 0, 0, false, false, false);
					}
					if( FCS.AddDecOp ) {
						Include(UABFUN.csDec, 0, 0, false, false, false);
					}
					if( FCS.AddInjOp ) {
						Include(UABFUN.csInj, 0, 0, false, false, false);
					}
					if( FCS.AddNotOp ) {
						Include(UABFUN.csNot, 0, 0, false, false, false);
					}
				}
			}

	// This function returns an already created random valid operation.
	public Operation GetRandomOper()
	{
		final int N = FOperations.size();
		int ResultingPosition = N == 1 ? 0 : MM.random(N);
		Operation r = FOperations.get(ResultingPosition);
		if( N > 1 ) {
//			int MAX = 100;
			int MAX = N > 100 ? 100 : N;
			while( MAX > 0 && r.OpCode == 0 ) {
				MAX--;
				ResultingPosition = MM.random(N);
				r = FOperations.get(ResultingPosition);
			}
		}
//
////this code has been commented as it creates too many warnings.
//else {
//if( r.OpCode == 0 ) {
//  System.out.println("WARNING: Operand Code is zero." + FOperations.size());
//}
//}
//
//		FOperations.get(ResultingPosition).OpCode = 0;
		FOperations.set(ResultingPosition, Operation.Null);
		return r;
//if MAX = 100 then
//writeln("ERROR: max on getrandom max.", FOperations.size());
	}

	// returns the Image Feature Center
	public int GetFeatureCenter2D()
	{
		return this.FFeatureCenter2D;
	}

}
