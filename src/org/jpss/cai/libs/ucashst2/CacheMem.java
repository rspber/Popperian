package org.jpss.cai.libs.ucashst2;

import org.jpss.cai.libs.State;

public class CacheMem
{
// indicates that the position is filled with data.
	private final boolean[] FFilledStatePosition;
// Keys in the memory structure.
	private final State[] FKeyStates;
// Data stored in the memory structure.
	private final State[] DataA;
// number of hits in the cache memory.
	private int NHit;
// number of misses in the cache memory.
	private int NMiss;
	/*private*/ int NOver;

	// This function returns an indication of how much of the memory structure is in use.
	public double Used()
	{
		int s = 0;
		for( int i = 0; i < UCashST2.MaxStates; i++ ) {
			if( FFilledStatePosition[i] ) {
				s = s + 1;
			}
		}
		return (double)s / UCashST2.MaxStates;
	}

	// This function returns the frequency of hits.
	public double HitsOverAll()
	{
		final double T = NHit + NMiss;
		return NHit / (T + 0.01);
	}

	public CacheMem(final int StateLength, final int DataLength)
	{
		NHit = 0;
		NMiss = 0;
		NOver = 0;
		FFilledStatePosition = new boolean[UCashST2.MaxStates];
		FKeyStates = new State[UCashST2.MaxStates];
		DataA = new State[UCashST2.MaxStates];
	}
/*
	// clears the memory
	public void Clear()
	{
		for( int i = 0; i < UCashST2.MaxStates; i++ ) {
			FFilledStatePosition[i] = false;
		}
	}
*/
	// Includes data into the memory.
	public void Include(
		// input
		final State ST, //memory index
		final State DTA) //memory data
	{
		final int PIncludePOS;
		{
			final int POS = ST.TCacheMemKey();
			// is it valid entry with wrong/other index?
			if( FFilledStatePosition[POS] && !FKeyStates[POS].eq(ST) ) {
	// or
	//     not(ABCmp(DataA[POS],DTA)) ) 
				NOver++;
	// POS1 not valid/filled or POS1 has correct index?
				final int POS1 = (POS + 1) % UCashST2.MaxStates;
				if( !FFilledStatePosition[POS1] || (FFilledStatePosition[POS1] && FKeyStates[POS1].eq(ST)) ) {
					PIncludePOS = POS1;
				} else {
					PIncludePOS = POS;
				}
			} else {
				PIncludePOS = POS;
			}
		}
//		private void PInclude(final int POS)
//		{
			FFilledStatePosition[PIncludePOS] = true;
			FKeyStates[PIncludePOS] = ST;
			DataA[PIncludePOS] = DTA;
//		}
	}

	// Returns true if the state/index exists on the array.
//memory index
	public boolean ValidEntry(final State ST)
	{
		final int POS = ST.TCacheMemKey();
		if( FFilledStatePosition[POS] && ST.eq(FKeyStates[POS]) ) {
			return true;
		}
		final int POS1 = (POS + 1) % UCashST2.MaxStates;
		return FFilledStatePosition[POS1] && ST.eq(FKeyStates[POS1]);
	}

	// This function returns -1 when ST does not exist. When exists, returns the position.
	// Returns DTA when ST exists.
	public State Read(
		// input
		final State ST) //memory index
	{
		int POS = ST.TCacheMemKey();
		int n = 2;
		while( --n >= 0 ) {
			if( FFilledStatePosition[POS] && ST.eq(FKeyStates[POS]) ) {
				NHit++;
				return DataA[POS];
			}
			POS = (POS + 1) % UCashST2.MaxStates;
		}
		NMiss++;
		return null;
	}

}
