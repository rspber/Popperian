package org.jpss.cai.libs.uabfun;

import org.jpss.cai.util.MM;

//
//A->B FUNCTIONS pascal Unit by Joao Paulo Schwarz Schuler
//This unit contains/is able to:
//* Contains all neurons (tests and operations) to be used by the prediction system.
//* Can create neurons of the type "operations".
//* Can create tests (conditions) and test lists.
//* Can run neurons/operations and produce the next state.
//* Verify tests.
//
public class UABFUN
{
// available operations. Some operations are logic/test operations such as <,> and <>.
// Other operations are math operations such as +,- and *.
	public final static byte csNop = 0;	// no operation
	public final static byte csEqual = 1;	// NextState[Base] := (Op1 = State[Op2]);
	public final static byte csEqualM = 2;	// NextState[Base] := (State[Op1] = State[Op2]);
	public final static byte csDifer = 3;	// NextState[Base] := (State[Op1] <> State[Op2]);
	public final static byte csGreater = 4;	// NextState[Base] := (State[Op1] > State[Op2]);
	public final static byte csLesser = 5;	// NextState[Base] := (State[Op1] < State[Op2]);
	public final static byte csTrue = 6;	// NextState[Base] := true;
	public final static byte csSet = 7;	// NextState[Base] := Op1;
	public final static byte csInc = 8;	// NextState[Base] := State[Base] + 1;
	public final static byte csDec = 9;	// NextState[Base] := State[Base] - 1;
	public final static byte csAdd = 10;	// NextState[Base] := State[Op1] +   State[Op2];
	public final static byte csSub = 11;	// NextState[Base] := State[Op1] -   State[Op2];
	public final static byte csMul = 12;	// NextState[Base] := State[Op1] *   State[Op2];
	public final static byte csDiv = 13;	// NextState[Base] := State[Op1] div State[Op2];
	public final static byte csMod = 14;	// NextState[Base] := State[Op1] mod State[Op2];
	public final static byte csAnd = 15;	// NextState[Base] := State[Op1] and State[Op2];
	public final static byte csOr = 16;	// NextState[Base] := State[Op1] or  State[Op2];
	public final static byte csXor = 17;	// NextState[Base] := State[Op1] xor State[Op2];
	public final static byte csInj = 18;	// NextState[Base] := State[Op1];
	public final static byte csNot = 19;	// NextState[BASE] := not(PreviousState[BASE])

	// An Operation type contains: an operation, 2 operands and boolean operand modifiers.

// number of available operations
	public final static int csMaxOperations = 20;
// this array maps OpCode into its string representation
	public final static String[/*0 .. csMaxOperations - 1*/] csStrOp = new String[] {
	//  0      1    2    3     4    5    6    7       8      9      10   11   12
		"nop", "=", "=", "<>", ">", "<", "V", " := ", "inc", "dec", "+", "-", "*",
	//  13     14     15     16    17     18     19
		"div", "mod", "and", "or", "xor", "inj", "not"};
// this type represents sets of operations
	// [ValueSetOf] TOperationSet = set of 0 .. csMaxOperations - 1
	public static boolean inTestOperationSet( final byte i)
	{
		switch(i) {
		case csEqual:
		case csEqualM:
		case csDifer:
		case csGreater:
		case csLesser:
		case csTrue:
			return true;
		default:
			return false;
		}
	}
	public static boolean inImediatSet( final byte i)
	{
		switch(i) {
		case csEqual:
		case csSet:
			return true;
		default:
			return false;
		}
	}
	public static boolean inFirstImediatSet( final byte i)
	{
		return i == csEqual;
	}
	public static boolean inStateOperationSet( final byte i)
	{
		switch(i) {
		case csInc:
		case csDec:
		case csAdd:
		case csSub:
		case csMul:
		case csDiv:
		case csMod:
		case csAnd:
		case csXor:
		case csOr :
		case csNot:
		case csInj:
			return true;
		default:
			return false;
		}
	}
	public static boolean inBinaryOperationSet( final byte i)
	{
		switch(i) {
		case csAdd:
		case csSub:
		case csMul:
		case csDiv:
		case csMod:
		case csAnd:
		case csXor:
		case csOr:
		case csEqual:
		case csEqualM:
		case csDifer:
		case csGreater:
		case csLesser:
			return true;
		default:
			return false;
		}
	}
	public static boolean inNoArgSet( final byte i)
	{
		switch(i) {
		case csInc:
		case csDec:
		case csNot:
		case csInj:
			return true;
		default:
			return false;
		}
	}
// maximum number of tests on a given rule (tests implies into operation).
	public final static int csMaxTests = 30;

	public static CreateOperationSettings csCreateOpDefault = new CreateOperationSettings(
		/*AddSetOp*/true,
		/*AddBinaryOp*/true,
		/*AddBinaryTest*/true,
		/*AddEqualTest*/true,
		/*AddTrueTest*/true,
		/*AddIncOp*/true,
		/*AddDecOp*/true,
		/*AddInjOp*/true,
		/*AddNotOp*/true,
		/*TestOnActions*/true,
		/*TestOnStates*/true,
		/*Bidimensional*/false,
		/*PartialTestEval*/false,
		/*MinTests*/1,
		/*MaxTests*/csMaxTests,
		/*FeatureSize*/0,
		/*ImageSizeX*/0,
		/*ImageSizeY*/0,
		/*MinSampleForPrediction*/0
	);
	public static CreateOperationSettings csCreateOpImageProcessing = new CreateOperationSettings(
		/*AddSetOp*/true,
		/*AddBinaryOp*/false,
		/*AddBinaryTest*/true,
		/*AddEqualTest*/false,
		/*AddTrueTest*/false,
		/*AddIncOp*/false,
		/*AddDecOp*/false,
		/*AddInjOp*/false,
		/*AddNotOp*/false,
		/*TestOnActions*/true,
		/*TestOnStates*/false,
		/*Bidimensional*/true,
		/*PartialTestEval*/true,
		/*MinTests*/1,
		/*MaxTests*/csMaxTests,
		/*FeatureSize*/2,
		/*ImageSizeX*/32,
		/*ImageSizeY*/32,
		/*MinSampleForPrediction*/10
	);
	public static CreateOperationSettings csCreateOpSimplest = new CreateOperationSettings(
		/*AddSetOp*/true,
		/*AddBinaryOp*/false,
		/*AddBinaryTest*/false,
		/*AddEqualTest*/true,
		/*AddTrueTest*/false,
		/*AddIncOp*/false,
		/*AddDecOp*/false,
		/*AddInjOp*/false,
		/*AddNotOp*/false,
		/*TestOnActions*/true,
		/*TestOnStates*/false,
		/*Bidimensional*/false,
		/*PartialTestEval*/false,
		/*MinTests*/1,
		/*MaxTests*/csMaxTests,
		/*FeatureSize*/0,
		/*ImageSizeX*/0,
		/*ImageSizeY*/0,
		/*MinSampleForPrediction*/0
	);

	public final static int csMaxOperationsArray = 1500;
	public final static byte[/*1*/] csUnitaryTests = new byte[] {csEqual};
	public final static byte[/*4*/] csBinaryTests = new byte[] {csEqualM, csDifer, csGreater, csLesser};
	public final static byte[/*8*/] csBinaryOperations = new byte[] {csAdd, csSub, csMul, csDiv, csMod, csAnd, csXor, csOr};

	// "RelativeOperandPosition" Modifier Examples
	// As an example, if RelativeOperandPosition1 is false, then we have
	// NextState[Base] := State[Op1] + State[Op2];

	// If RelativeOperandPosition1 is true, then we have
	// NextState[Base] := State[BASE + Op1] + State[Op2];

	// If RunOnAction is true and RelativeOperandPosition1 is false, then we have:
	// NextState[Base] := State[Op1] + Action[Op2];

	// "RunOnAction" modifies first operator in Unary operations and
	// modifies second operator in binary operations.

	public static byte ValidBinaryCode(final int Val1, final int Val2)
	{
		return Val1 > Val2 ? csGreater : Val1 < Val2 ? csLesser : csEqual;
	}

	public static int GetRandom2DDist(final int FeatureSize)
	{
		return MM.random(FeatureSize * 2 + 1) - FeatureSize;
	}

}
