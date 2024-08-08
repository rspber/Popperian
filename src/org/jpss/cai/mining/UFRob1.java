package org.jpss.cai.mining;

import org.jpss.cai.libs.ubup3.EasyLearnAndPredict;
import org.jpss.cai.util.MM;

public class UFRob1
{
	public final static int csStateByteLength = 8;
	public final static int csWorldMin = -50;
	public final static int csWorldMax = 50;

	public final static int FWorldCenter = (csWorldMin + csWorldMax) / 2;
	public final static int FWorldLength = csWorldMax - csWorldMin;

	final Forms forms;

// Private declarations 
	private boolean FShouldStop;	// indicates that the process should stop
//	double Speed;	// multiplicador de velocidade para agentes
// Public declarations 
	final Popperian popperian;
	final ArtificialMiningWorld FMiningWorld;
	int FSuccessCount;	// Number of Successes

	private final EasyLearnAndPredict LearnAndPredict;


		// DefineInitialPositions
		private Position CreateMine( final int Charge )
		{
			final double PosBase = FWorldCenter + 1;
			final double DistMin = FWorldLength * 0.4;
			int DX = (int)MM.random( DistMin);
			int DY = (int)Math.round(DistMin) - DX;
			DX = DX * (MM.random( 2) * 2 - 1);	// GetOneOrMinusOne;
			DY = DY * (MM.random( 2) * 2 - 1);	// GetOneOrMinusOne;
			return new Position( PosBase + DX, PosBase + DY, Charge );
		}
	
	private static final int[] DISPX = new int[] {1, 1, 1, 0, -1, -1, -1, 0};
	private static final int[] DISPY = new int[] {1, 0, -1, -1, -1, 0, 1, 1};
	
	Position[] DefineInitialPositions()
	{
		final Position[] InitPos = new Position[ URobMin2.csMaxAgents ];
		InitPos[0] = new Position( FWorldCenter, FWorldCenter, 0 );	// URobMin2.csBaseIdx = 0
		int i = 0;
		while( ++i < URobMin2.csMaxAgents - URobMin2.csRunners ) {	// URobMin2.csMiningIdx = 1
			InitPos[i] = CreateMine( URobMin2.csAgentCharge );
		}
		int j = 0;
		while( i < URobMin2.csMaxAgents ) {
			InitPos[i] = new Position( FWorldCenter + DISPX[j], FWorldCenter + DISPY[j], 0 );
			++i;
			++j;
		}
		return InitPos;
	}


	public void Button1Click()
	{
//		FMiningWorld.DefineWorldLength();
//		FMiningWorld.DefineSuperposition(false);
//		FMiningWorld.DefineLinkedBorder(false);
/*
		AgentP[0].DefinePos(  0,  0);
		AgentP[1].DefinePos(-30,-40);
		AgentP[2].DefinePos(-21,-41);
		AgentP[3].DefinePos(-47,-42);
		AgentP[4].DefinePos(-36,-43);
*/
/*
		FShouldStop = false;
		while( !FShouldStop ) {
			FMiningWorld.RandomMove(MM.random( 5));
			Mostra();
		}
*/
	}

	public void Mostra()
	{
		forms.form1.MostraWorld(FMiningWorld);
		if( forms.formOpt.CBShowPlan.isSelected() ) {
			forms.formPlans.MostraPlanos(popperian.Plans, FMiningWorld);
		}
	}

	UFRob1( final Forms forms )
	{
		this.forms = forms;

		FMiningWorld = new ArtificialMiningWorld( UFRob1.FWorldLength );
//		FMiningWorld.DefineWorldLength();
//		FMiningWorld.DefineSuperposition(false);	//true
//		FMiningWorld.DefineLinkedBorder(false);

		LearnAndPredict = new EasyLearnAndPredict(
			csStateByteLength, csStateByteLength,
			false, //pFullEqual
			100, // pNumOfNeurons
			40, //pNumOfSearches
			true, //pUseCache
			true, //FGeneralize
			true //FUseBelief
		);

		popperian = new Popperian( FMiningWorld, LearnAndPredict, forms );
//		Speed = 1;
	}

	public void MMStopClick()
	{
		FShouldStop = true;
	}

	public void FormClose()
	{
		FShouldStop = true;
		forms.setChMostra(false);
	}

	private boolean ok1( final int i )
	{
		if( i % 200 == 0 ) {
			final double speed = forms.Speed();
			if( speed < 100 ) {
				double r = 100 - speed;
				final int d = (int)(r * r * 2);
				try {
					Thread.sleep(d / 100, d % 1000);
				} catch (InterruptedException e) {
				}
			}
			return true;
		}
		return false;
	}

	private void RunLearning( final Position[] InitPos )
	{
		final int Runs = 1000000;	// or at least 20 plans found
		final int TotalCharge = FMiningWorld.ApplyInitialPositions( InitPos );
		popperian.Clear();
		int i = 0;
		while( !FShouldStop && FMiningWorld.NMoves < Runs) {
			if( popperian.plansFound() > 20 ) {
				break;
			}
			i++;
			if( ok1(i) ) {
				Mostra();
			}
			popperian.MoveNeuralAgent();
			if( forms.ChMostra() ) {
				Mostra();
			}
		}
	}

	private boolean ok2( final int i )
	{
		final double speed = forms.Speed();
		if( speed < 100 ) {
			double r = 100 - speed;
			final int d = (int)(r * r * 2);
			try {
				Thread.sleep(d / 100, d % 100);
			} catch (InterruptedException e) {
			}
			if( speed > 5 ) {
				return i % (int)(speed*2) == 0;
			}
			else {
				return true;
			}
		}
		return i % 200 == 0;
	}

	private double RunOneSimulation( final Position[] InitPos )
	{
		final int Runs = forms.edciclos();
		final int TotalCharge = FMiningWorld.ApplyInitialPositions( InitPos );
		popperian.Clear();
		int i = 0;
		while( !FShouldStop && FMiningWorld.NMoves < Runs ) {
			i++;
			if( ok2(i) ) {
				Mostra();
			}
			popperian.MoveNeuralAgent();
			if( forms.ChMostra() ) {
				Mostra();
			}
			final int charge = FMiningWorld.BASE().Charge;
			if( charge == TotalCharge ) {
				System.out.println("Success: " + charge);
				FSuccessCount++;
				break;
			}
		}
		return 0;
	}

	public void RunManySimulations()
	{
		try {
	//		forms.Form1.setDisable(true);
			FSuccessCount = 0;
			FShouldStop = false;
			final Position[] InitPos = DefineInitialPositions();	// initial positions
			forms.setLearningMode();
			RunLearning( InitPos );		// 1 000 000
			forms.setMiningMode();
			forms.ShowFormOpt();
			forms.ShowFormViewPlans();
			while( !FShouldStop ) {
				RunOneSimulation( InitPos );	// 2 000 000
			}
	//		forms.Form1.setDisable(false);
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

}
