package org.jpss.cai.libs.uabfun;

import java.util.ArrayList;
import java.util.List;

import org.jpss.cai.util.MM;

//Tests represents a list of tests such as (A>B) and (C=D) and (E>F).
public class Tests
{
	public int TestBasePosition;
//Neuronal TestThreshold - minimum number of valid test to fire the operation
	public int TestThreshold;

	final List<Operation>/*[0 .. csMaxTests - 1]*/ T;
//when N is Zero, there is no operation/neuron
	public int N() {
		return T.size();
	}

	public Tests()
	{
		T  = new ArrayList<>();
	}

	public void RandomDeleteOperation()
	{
		final int k = MM.random( T.size() );
		T.remove(k);
	}
	
	public void RemoveOperations( )
	{
		T.clear();
	}

	public void copyFrom( final Tests t )
	{
		TestBasePosition = t.TestBasePosition;
		TestThreshold = t.TestThreshold;
		T.clear();
		T.addAll( t.T );
	}

// Adds a test to the end of test list
	public void AddTest(final Operation Adding)
	{
		if( T.size() < UABFUN.csMaxTests ) {
			T.add(Adding);
		}
		else {
		}
	}

}
