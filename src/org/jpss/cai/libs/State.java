package org.jpss.cai.libs;

import java.util.Arrays;

import org.jpss.cai.libs.ucashst2.UCashST2;
import org.jpss.cai.mining.UFRob1;

public class State {

//	public static final int STATE_0 = 0;
//	public static final int STATE_1 = 1;	// RUNNER position X
//	public static final int STATE_2 = 2;	// RUNNER position Y
//	public static final int STATE_3 = 3;	// RUNNER state 0 or 1
//	public static final int STATE_4 = 4;	// RUNNER is close to destination
//	public static final int STATE_5 = 5;	// RUNNER is close to origin
//	public static final int STATE_6 = 6;	// csWorldCenter + 1 = navel of world
//	public static final int STATE_7 = 7;

	private int refcnt;

	public State clone()
	{
		++refcnt;
		return this;
	}

	public void deref()
	{
		if( refcnt > 0 ) {
			refcnt--;
		}
		else {
//			throw new RuntimeException("ref less then 0 over");
		}
	}

	private final byte[] b_;

	private int TABHashKey_;
	public int TABHashKey()
	{
		if( TABHashKey_ == 0 ) {
			TABHashKey_ = UAB.ABKey(b_, TABHash.NOfBits);
		}
		return TABHashKey_;
	}

	private int TCacheMemKey_;
	public int TCacheMemKey()
	{
		if( TCacheMemKey_ == 0 ) {
			TCacheMemKey_ = UAB.ABKey(b_, UCashST2.MaxStates);
		}
		return TCacheMemKey_;
	}

	public State()
	{
		this(UFRob1.csStateByteLength);
	}

	public State(final int length)
	{
		b_ = new byte[length];
	}

	public State(final byte[] b)
	{
		b_ = new byte[b.length];
		System.arraycopy(b, 0, b_, 0, b.length);
	}

	public int length()
	{
		return b_.length;
	}

	public byte state(final int i)
	{
		return b_[i];
	}

	public void getBytes(final byte[] b)
	{
		System.arraycopy(b_, 0, b, 0, b_.length < b.length ? b_.length : b.length);
	}

	public int x()
	{
		return b_[ 1 ];
	}

	public int y()
	{
		return b_[ 2 ];
	}

	public int charge()
	{
		return b_[ 3 ];
	}

	public boolean closeToBase()
	{
		return b_[ 4 ] != 0;
	}

	public boolean closeToAgent()
	{
		return b_[ 5 ] != 0;
	}

	public State setState(final int i, final byte v)
	{
		if( refcnt == 0 ) {
			b_[i] = v;
			TABHashKey_ = 0;
			TCacheMemKey_ = 0;
			return this;
		}
		else {
			final State st = new State(b_);
			st.b_[i] = v;
			return st;
		}
	}

	public State setAction(final byte v)
	{
		return setState(0, v);
	}

	public State cloneAndSetState(final int i, final byte v)
	{
		final State st = new State(b_);
		st.b_[i] = v;
		return st;
	}

	public State cloneAndSetAction(final byte v)
	{
		return cloneAndSetState(0, v);
	}

	public boolean eq(final State st)
	{
		return st.TABHashKey() == TABHashKey() && Arrays.equals(st.b_, b_);
//		return Arrays.equals(st.b_, b_);
	}

	public String toString()
	{
		return String.format("%s%s%d:%d%s", closeToBase() ? "B:" : "", closeToAgent() ? "A:" : "", x(), y(), charge() != 0 ? "q:" : "");
//		return UAB.ABToString(b_);
	}

	public int countDif(final State st)
	{
		return UAB.ABCountDif(b_, st.b_);
	}
	
	public int randomGetNext1(final State st)
	{
		return UAB.ABRandomGetNext1(st.b_, b_);
	}

	public static void deref(final State st)
	{
		if( st != null ) {
			if( st.refcnt > 0 ) {
				st.refcnt--;
			}
			else {
			}
		}
	}
}
