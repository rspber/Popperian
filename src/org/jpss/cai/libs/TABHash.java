package org.jpss.cai.libs;

import java.util.Arrays;

// part of UAB

public class TABHash
{
	static final int NOfBits = 8000 - 1;

	
	private final byte[] bits;

	// creates memory structure with PLength number of bits.
	public TABHash()
	{
		bits = new byte[(NOfBits / 8) + 1];
//		clear();
	}

	public void copyFrom(final TABHash t)
	{
		System.arraycopy(t.bits, 0, bits, 0, bits.length);
	}

	// Clears (all positions are now zeroes).
	public void clear()
	{
		Arrays.fill(bits, (byte)0);
	}

	// Adds 1 to a position calculated from the array of bytes.
	public void include(final State st)
	{
		UBIT.BAWrite(bits, st.TABHashKey(), 1);
	}

	// Checks if the position corresponding to the array of byte is 1. Returns true if it's 1.
	public boolean test(final State st)
	{
		return UBIT.BATest(bits, st.TABHashKey());
	}

}

