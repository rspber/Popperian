/*
  This is one of the most interesting classes in the entire project.
  This class is responsible for processing operations. Operations can run tests on
  Actions array or on Current States array and create a new state on Next States array.
*/
package org.jpss.cai.libs.uabfun;

import org.jpss.cai.libs.State;
import org.jpss.cai.util.MM;

public class RunOperation {

	public final CreateOperationSettings FCS;

	protected final State Actions, CurrentStates, NextStates;

	public RunOperation(final CreateOperationSettings FCS, final State Actions, final State CurrentStates, final State NextStates )
	{
		this.FCS = FCS;
		this.Actions = Actions;
		this.CurrentStates = CurrentStates;
		this.NextStates = NextStates;
	}

	// creates a random binary test from action array
	public Operation CreateActionRandomBinaryTest()
	{
		final int Pos1, Pos2;

		if( FCS.Bidimensional ) {
			Pos1 = FCS.GetRandom2DPos(0);
			Pos2 = FCS.GetRandom2DPos(0);
		} else {
			Pos1 = MM.random(Actions.length());
			Pos2 = MM.random(Actions.length());
		}
		final int Val1 = MM.random(255);
		final int Val2 = MM.random(255);
		return new Operation(UABFUN.ValidBinaryCode(Val1, Val2), Pos1, Pos2, true, true, true);
	}

	private int LocalTestTests(final Tests tests)
	{
		int PermissibleErrors = 0;

		if( tests.N() > 0 ) {
			PermissibleErrors = tests.N() - tests.TestThreshold;
			for( final Operation Oper : tests.T ) {
				if( PermissibleErrors < 0 ) {
					break;
				}
				if( 0 == Test(Oper, tests.TestBasePosition) ) {
					PermissibleErrors--;
				}
			}
		}
		if( PermissibleErrors >= 0 && tests.N() > 0 ) {
			return tests.N() - PermissibleErrors;
		} else {
			return 0;
		}
	}

	private int ConvoluteTests(final Tests tests)
	{
		int result = 0;
		int TopX = FCS.ImageSizeX - FCS.FeatureSize;
		int TopY = FCS.ImageSizeY - FCS.FeatureSize;
		for( int x = FCS.FeatureSize; x < TopX; x++ ) {
			for( int y = FCS.FeatureSize; y < TopY; y++ ) {
				int Pos = FCS.Make2D(x, y);
				tests.TestBasePosition = Pos;
				if( LocalTestTests(tests) > 0 ) {
					result = Pos;
					break;
				}
			}
			if( result > 0 ) {
				break;
			}
		}
		return result;
	}

	public int getNextState(final Operation oper, final int BasePosition)
	{
		final byte OpCode = (byte)(oper.OpCode & 0x3f);

		final int pos1;
		if( oper.RelPos1 ) {
			pos1 = BasePosition + oper.Op1;
		} else {
			pos1 = oper.Op1;
		}

		final int pos2;
		if( oper.RelPos2 ) {
			pos2 = BasePosition + oper.Op2;
		} else {
			pos2 = oper.Op2;
		}

		final int Op1;
		if( pos1 >= Actions.length() || pos1 < 0 ) {
			if( !UABFUN.inImediatSet(OpCode) ) {
				return 0;
			}
			Op1 = 0;
		}
		else {
			Op1 = oper.RunOnAction ? Actions.state(pos1) : CurrentStates.state(pos1);
		}

		final int Op2;
		if( pos2 >= Actions.length() || pos2 < 0 ) {
			return 0;
		}
		else {
			Op2 = oper.RunOnAction ? Actions.state(pos2) : CurrentStates.state(pos2);
		}

		switch( OpCode ) {
		case UABFUN.csNop:		return 0;
		case UABFUN.csEqual:	return oper.Op1 == Op2 ? 1 : 0;
		case UABFUN.csEqualM:	return Op1 == Op2 ? 1 : 0;
		case UABFUN.csDifer:	return Op1 != Op2 ? 1 : 0;
		case UABFUN.csGreater:	return Op1 > Op2 ? 1 : 0;
		case UABFUN.csLesser:	return Op1 < Op2 ? 1 : 0;
		case UABFUN.csTrue:		return 1;
		case UABFUN.csSet:		return oper.Op1;
		case UABFUN.csInc:		return CurrentStates.state(BasePosition) + 1;
		case UABFUN.csDec:		return CurrentStates.state(BasePosition) - 1;
		case UABFUN.csAdd:		return Op1 + Op2;
		case UABFUN.csSub:		return Op1 - Op2;
		case UABFUN.csMul:		return Op1 * Op2;
		case UABFUN.csDiv:		return Op2 != 0 ? Op1 / Op2 : 0;
		case UABFUN.csMod:		return Op2 != 0 ? Op1 % Op2 : 0;
		case UABFUN.csAnd:		return Op1 & Op2;
		case UABFUN.csOr:		return Op1 | Op2;
		case UABFUN.csXor:		return Op1 ^ Op2;
		case UABFUN.csInj:		return CurrentStates.state(BasePosition);
		case UABFUN.csNot:		return ~CurrentStates.state(BasePosition);
		default:
			System.out.println("ERROR: invalid operation code:" + oper.OpCode);
			return 0;
		}
	}

	public byte Test(final Operation oper, final int BasePosition)
	{
		final int NextState = getNextState( oper, BasePosition );
		final byte OpCode = (byte)(oper.OpCode & 0x3f);
		if( UABFUN.inTestOperationSet(OpCode) ) {
			return (byte)NextState;
		} else {
			final byte st = NextStates.state(BasePosition);
			return st == NextState ? (byte)1 : 0;
		}
	}

	public int TestTests(final Tests tests)
	{
		if( FCS.Bidimensional ) {
			return ConvoluteTests(tests);
		} else {
			return LocalTestTests(tests);
		}
	}

}
