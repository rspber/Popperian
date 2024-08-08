package org.jpss.cai.libs.uabfun;

//Operation 
public class Operation
{
	public final byte OpCode;	//Operand Code
	final short Op1;	//Operand 1
	final short Op2;	//Operand 2
	final boolean RelPos1;	// RelativeOperandPosition1   Operand position is relative
	final boolean RelPos2;	// RelativeOperandPosition2   Operand position is relative
	final boolean RunOnAction;

	public final static Operation Null = new Operation((byte)0, 0, 0, false, false, false);

	public Operation(final byte OpCode, final int Op1, final int Op2, final boolean RelPos1, final boolean RelPos2, final boolean RunOnAction)
	{
		this.OpCode = OpCode;
		this.Op1 = (short)Op1;
		this.Op2 = (short)Op2;
		this.RelPos1 = RelPos1;
		this.RelPos2 = RelPos2;
		this.RunOnAction = RunOnAction;
	}

}
