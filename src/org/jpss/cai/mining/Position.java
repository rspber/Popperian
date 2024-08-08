package org.jpss.cai.mining;

public class Position
{
// x and y can vary from 0 until WorldLength-1.
	double x, y;
//States can be unloaded (URobMin2.csUnloaded)and loaded (URobMin2.csLoaded).
	int Charge;

	Position( final double x, final double y, final int Charge )
	{
		this.x = x;
		this.y =y;
		this.Charge = Charge;
	}

	public void set( final Position p )
	{
		this.x = p.x;
		this.y = p.y;
		this.Charge = p.Charge;
	}

	public boolean Encounter( final Position p )
	{
		final double dx = Math.abs(x - p.x);
		final double dy = Math.abs(y - p.y);
		return (dx <= 1) && (dy <= 1);
	}

}

