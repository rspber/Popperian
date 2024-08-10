package org.jpss.cai.mining;

public class URobMin2 {

	public final static int csNumberActions = 7;
// Actions
	public final static byte csLeft = 0;
	public final static byte csRight = 1;
	public final static byte csUp = 2;
	public final static byte csDown = 3;
	public final static byte csHold = 4;
	public final static byte csLoad = 5;
	public final static byte csUnLoad = 6;
	public final static byte csUpLeft = 7;
	public final static byte csUpRight = 8;
	public final static byte csDownLeft = 9;
	public final static byte csDownRight = 10;
	public final static byte csCenter = 11;
//States
	public final static int csLoaded = 0;
	public final static int csUnloaded = 1;
// base index
	public final static int csBaseIdx = 0;
// mining index
	public final static int csMiningIdx = 1;
	public final static int csRunners = 1;

	public static int csMaxAgents = csMiningIdx + 3 + csRunners;

	public final static int csAgentCharge = 100;

}
