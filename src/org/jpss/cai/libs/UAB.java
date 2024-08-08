package org.jpss.cai.libs;

import java.util.Arrays;

import org.jpss.cai.util.MM;

/*
	uses SysUtils, UBit
*/

// U stands for Unit. a stands for Array. b stands for Bytes.
// This unit contains "array of bytes" functions.
// made by Joao Paulo Schwarz Schuler
// jpss@schulers.com
public class UAB
{
// This class represents a int array of bits.
//   FLenght is the array lenght in bits.
//   "Include" method calculates a key (position) for a given input array and sets 1 to the bit in the array.
//   "Test" method returs true if the bit is 1 for the position of the key of the s array.

// creates a Key from the array.
	public static int ABKey(final byte[] s, final int Divisor)
	{
		int SumKey = 203;

		for( int i = 0; i < s.length; i++ ) {
			SumKey = (((SumKey * (s[i] + 11)) % Divisor) * (s[(i + 1) % s.length] + 17) + 3) % Divisor;
		}
		return Math.abs(SumKey) % Divisor;
	}

// ab Compare
	public static boolean ABCmp(final byte[] x, final byte[] y)
	{
		return Arrays.equals(x, y);
	}

// a <- b
	public static void ABCopy(final /*var*/ byte[] a, final byte[] b)
	{
		final int MinLen;

		if( a.length < b.length ) {
			MinLen = a.length;
		} else {
			MinLen = b.length;
		}
		System.arraycopy(b, 0, a, 0, MinLen);
	}

// returns the next 1 position from ST
	public static int ABGetNext1(final byte[] ab, final int ST)
	{
		boolean Found = false;
		int i = -1;
		final int L = ab.length;
		int r = 0;
		while( ++i <= L ) {
			r = (i + ST) % L;
			if( ab[r] != 0 ) {
				break;
			}
		}
		return r;
	}

// clears array (fills with zeros
	public static void ABClear(final /*var*/ byte[] ab)
	{
		for( int i = ab.length; --i >= 0; ) {
			ab[i] = 0;
		}
	}

// counts the number of diff bytes
	public static int ABCountDif(final byte[] x, final byte[] y)
	{
		int r = 0;
		for( int i = x.length; --i >= 0; ) {
			if( x[i] != y[i] ) {
				r++;
			}
		}
		return r;
	}

// returns the number of non matching bytes
	public static byte[] ABGetDif(final byte[] x, final byte[] y)
	{
		final byte[] Dif = new byte[x.length];
		for( int i = 0; i < x.length; i++ ) {
			Dif[i] = x[i] != y[i] ? (byte)1 : (byte)0;
		}
		return Dif;
	}


//	final byte[] ab = ABGetDif(x, y);
//	final int r = ABGetNext1(ab, MM.random(ab.length));

	// returns the number of non matching bytes
	// returns the next 1 position from ST
	public static int ABRandomGetNext1(final byte[] x, final byte[] y)
	{
		int i = -1;
		final int L = x.length;
		final int ST = MM.random(L);
		while( i < L ) {
			final int r = (++i + ST) % L;
			if( x[r] != y[r] ) {
				return r;
			}
		}
		return 0;
	}


}
