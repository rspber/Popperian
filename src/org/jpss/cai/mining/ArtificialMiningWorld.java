package org.jpss.cai.mining;

import org.jpss.cai.util.MM;

public final class ArtificialMiningWorld
{
// number of movements.
	int NMoves;
	private int FWorldLength;

// Allows Superposition?
	boolean Superposition;
	boolean FLinkedBorder;
	int NumUnLoad;
	int NumLoad;
	boolean FShowGrid;

	private Position[] AgentP;

	int AgentCount()
	{
		return AgentP.length;
	}

	Position Agent( final int i )
	{
		return AgentP[i];
	}

	ArtificialMiningWorld( final int FWorldLength )
	{
		this.FWorldLength = FWorldLength;
	}

	int ApplyInitialPositions( final Position[] InitPos )
	{
		int TotalCharge = 0;

		AgentP = new Position[ InitPos.length ];
		for( int i = 0; i < InitPos.length; ++i ) {
			final Position p = InitPos[i];
			AgentP[i] = new Position( 0, 0, 0);
			AgentP[i].set( p );
			TotalCharge += p.Charge;
		}
		return TotalCharge;
	}


	boolean showGrid()
	{
		return FShowGrid;
	}
/*
	public void FixPos( final Position p )
	{
		if( p.x < 0 ) {
			p.x += FWorldLength;
		}
		if( p.x >= FWorldLength ) {
			p.x -= FWorldLength;
		}
		if( p.y < 0 ) {
			p.y += FWorldLength;
		}
		if( p.y >= FWorldLength ) {
			p.y -= FWorldLength;
		}
	}

	public void MoveAgentR( final int dx, final int dy, final Position p )
	{
		p.x += dx;
		p.y += dy;
		FixPos( p );
	}

	public void DefineWorldLength()
	{
		NumUnLoad = 0;
		NumLoad = 0;
		NMoves = 0;
	}

	protected void DefineSuperposition(final boolean s)
	{
		Superposition = s;
	}

	protected void DefineLinkedBorder(final boolean LB)
	{
		FLinkedBorder = LB;
	}
*/
	Position BASE()
	{
		return AgentP[ URobMin2.csBaseIdx ];
	}

	Position RUNNER()
	{
		return AgentP[ AgentP.length-1 ];
	}

	boolean EncounterMINE( final Position RUNNER )
	{
		for( int i = URobMin2.csMiningIdx; i < URobMin2.csMaxAgents - URobMin2.csRunners; ++i ) {	// 1
			if( RUNNER.Encounter(AgentP[i]) ) {
				return true;
			}
		}
		return false;
	}

	// ---------------------------------------------------------------------------

	private void goLeft( final Position p )
	{
		double nx = p.x - 1;
		if( nx < UFRob1.csWorldMin ) {
			if( FLinkedBorder ) {
				nx = UFRob1.csWorldMax - 1;
			} else {
				nx = p.x;
			}
		}
		if( !UsedPos(nx, p.y) ) {
			p.x = nx;
		}
	}

	private void goRight( final Position p )
	{
		double nx = p.x + 1;
		if( nx >= UFRob1.csWorldMax ) {
			if( FLinkedBorder ) {
				nx = UFRob1.csWorldMin + 1;
			} else {
				nx = p.x;
			}
		}
		if( !UsedPos(nx, p.y) ) {
			p.x = nx;
		}
	}

	private void goUp( final Position p )
	{
		double ny = p.y - 1;
		if( ny < UFRob1.csWorldMin ) {
			if( FLinkedBorder ) {
				ny = UFRob1.csWorldMax - 1;
			} else {
				ny = p.y;
			}
		}
		if( !UsedPos(p.x, ny) ) {
			p.y = ny;
		}
	}

	private void goDown( final Position p )
	{
		double ny = p.y + 1;
		if( ny >= UFRob1.csWorldMax ) {
			if( FLinkedBorder ) {
				ny = UFRob1.csWorldMin + 1;
			} else {
				ny = p.y;
			}
		}
		if( !UsedPos(p.x, ny) ) {
			p.y = ny;
		}
	}

	public boolean move( final Position p, final byte Direction )
	{
		final double OX = p.x;
		final double OY = p.y;

		switch( Direction ) {
		case URobMin2.csLeft:
			goLeft(p);
			break;
		case URobMin2.csRight:
			goRight(p);
			break;
		case URobMin2.csUp:
			goUp(p);
			break;
		case URobMin2.csDown:
			goDown(p);
			break;
		case URobMin2.csHold:
			NMoves++;
			return false;
		case URobMin2.csLoad:
			Load(p);
			break;
		case URobMin2.csUnLoad:
			Unload(p);
			break;
		case URobMin2.csUpLeft:
			goUp(p);
			goLeft(p);
			break;
		case URobMin2.csUpRight:
			goUp(p);
			goRight(p);
			break;
		case URobMin2.csDownLeft:
			goDown(p);
			goLeft(p);
			break;
		case URobMin2.csDownRight:
			goDown(p);
			goRight(p);
			break;
		case URobMin2.csCenter:
			p.x = UFRob1.FWorldCenter;
			p.y = UFRob1.FWorldCenter;
			break;
		}
		NMoves++;
		return OX != p.x || OY != p.y;
	}

	public void Load( final Position p )
	{
		if( p.Charge == 0 ) {
			for( int i = URobMin2.csMiningIdx; i < URobMin2.csMaxAgents - URobMin2.csRunners; ++i ) {
				final Position a = AgentP[i];
				if( p.Encounter(a) ) {
					NumLoad++;
					p.Charge++;
					a.Charge--;
					if( a.Charge == 0 ) {
						a.x = UFRob1.csWorldMin - 10;
						a.y = UFRob1.csWorldMin - 10;
					}
					break;
				}
			}
		}
	}

	public void Unload( final Position p )
	{
		final Position a = AgentP[URobMin2.csBaseIdx];
		if( p.Charge == 1 && p.Encounter(a) ) {
			NumUnLoad++;
			p.Charge--;
			a.Charge++;
		}
	}

	public void RandomPos( final Position p )
	{
		p.x = UFRob1.csWorldMin + MM.random( FWorldLength );
		p.y = UFRob1.csWorldMin + MM.random( FWorldLength );
	}
/*
	private void RandomMove(final int P)
	{
		move( AgentP[P], (byte)MM.random( 4) );	// left, right, up, down
	}
*/
	public void RandomPos()
	{
		for( final Position p : AgentP ) {
			RandomPos( p );
		}
	}

	public boolean UsedPos(final double x, final double y)
	{
		if( !Superposition ) {
			for( final Position p : AgentP ) {
				if( p.x == x && p.y == y ) {
					return true;
				}
			}
		}
		return false;
	}
}
