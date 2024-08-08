package org.jpss.cai.libs.uabfun;

import org.jpss.cai.util.MM;

public class CreateOperationSettings
{
	public final boolean AddSetOp;
	public final boolean AddBinaryOp;
	public final boolean AddBinaryTest;
	public final boolean AddEqualTest;
	public final boolean AddTrueTest;
	public final boolean AddIncOp;
	public final boolean AddDecOp;
	public final boolean AddInjOp;
	public final boolean AddNotOp;
	public final boolean TestOnActions;
	public final boolean TestOnStates;
	public final boolean Bidimensional;
	public final boolean PartialTestEval;
	public final int MinTests;
	public final int MaxTests;
	public final int FeatureSize;
	public final int ImageSizeX;
	public final int ImageSizeY;
	public final int MinSampleForPrediction;
	public CreateOperationSettings(
		final boolean AddSetOp,
		final boolean AddBinaryOp,
		final boolean AddBinaryTest,
		final boolean AddEqualTest,
		final boolean AddTrueTest,
		final boolean AddIncOp,
		final boolean AddDecOp,
		final boolean AddInjOp,
		final boolean AddNotOp,
		final boolean TestOnActions,
		final boolean TestOnStates,
		final boolean Bidimensional,
		final boolean PartialTestEval,
		final int MinTests,
		final int MaxTests,
		final int FeatureSize,
		final int ImageSizeX,
		final int ImageSizeY,
		final int MinSampleForPrediction
	 )
	{
		this.AddSetOp =               AddSetOp;
		this.AddBinaryOp =            AddBinaryOp;
		this.AddBinaryTest =          AddBinaryTest;
		this.AddEqualTest =           AddEqualTest;
		this.AddTrueTest =            AddTrueTest;
		this.AddIncOp =               AddIncOp;
		this.AddDecOp =               AddDecOp;
		this.AddInjOp =               AddInjOp;
		this.AddNotOp =               AddNotOp;
		this.TestOnActions =          TestOnActions;
		this.TestOnStates =           TestOnStates;
		this.Bidimensional =          Bidimensional;
		this.PartialTestEval =        PartialTestEval;
		this.MinTests =               MinTests;
		this.MaxTests =               MaxTests;
		this.FeatureSize =            FeatureSize;
		this.ImageSizeX =             ImageSizeX;
		this.ImageSizeY =             ImageSizeY;
		this.MinSampleForPrediction = MinSampleForPrediction; 
	}

	// 2D Image Specific Functions
	public int Make2D(final int x, final int y)
	{
		return x + y * ImageSizeY;
	}

	public int CreateFeatureCenter()
	{
		final int x = FeatureSize + MM.random(ImageSizeX - FeatureSize * 2);
		final int y = FeatureSize + MM.random(ImageSizeY - FeatureSize * 2);
		return Make2D(x, y);
	}

	public int GetRandom2DPos(final int Center)
	{
		return Center + Make2D(UABFUN.GetRandom2DDist(FeatureSize), UABFUN.GetRandom2DDist(FeatureSize));
	}

}
