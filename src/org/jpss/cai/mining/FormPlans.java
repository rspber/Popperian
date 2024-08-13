package org.jpss.cai.mining;

import org.jpss.cai.libs.State;
import org.jpss.cai.libs.ufway44.ActionStateList;
import org.jpss.cai.libs.ufway44.Plan;
import org.jpss.cai.util.MM;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class FormPlans extends Group
{
	void initShps()
	{
		Shps = new Shape[URobMin2.csMaxAgents];
		Shps[0] = new AgentShapes.TBase(144,  56, 15);
		int y = 80;
		int i = 1;
		for( ; i < URobMin2.csMaxAgents - URobMin2.csRunners; ++i ) {
			Shps[i] = new AgentShapes.TAgent(144,  y, 10, AgentShapes.RNDC[i]);
			y += 24;
		}
		while( i < URobMin2.csMaxAgents ) {
			Shps[i] = new AgentShapes.TRunner(144, y, 10);
			++i;
		}
		pane.getChildren().clear();
		pane.getChildren().add(canvas);
		pane.getChildren().addAll(Shps);
	}
	
	FormPlans()
	{
		final BorderPane borderPane = new BorderPane();

		canvas = new Canvas(640,480);
		gc = canvas.getGraphicsContext2D();

		pane = new Pane();
		initShps();

		borderPane.setCenter(pane);

		getChildren().add(borderPane);

//		FormCreate();
	}

	private final Pane pane;
	private final Canvas canvas;
	private final GraphicsContext gc;

	private Shape[] Shps;

	private void Clear__(final ArtificialMiningWorld FMiningWorld)
	{
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		if( FMiningWorld.showGrid() ) {
			final int L = UFRob1.FWorldLength;

			final double w = canvas.getWidth() / L;
			final double h = canvas.getHeight() / L;
	
			gc.setStroke(Color.LIGHTGRAY);
			for( int i = 0; i <= L; i++ ) {
				gc.strokeLine((0+1) * w, (i+1) * h, (L+1) * w, (i+1) * h);
				gc.strokeLine((i+1) * w, (0+1) * h, (i+1) * w, (L+1) * h);
			}
		}
	}
	static final Color[] RNDC = new Color[] {
			Color.SILVER,
			Color.LIGHTGRAY,
			Color.DIMGRAY,
			Color.GRAY,
			Color.DARKGRAY
		};

	private void ShowPlan__(final Plan Plan, final int L, final Color color)
	{
		final double w = canvas.getWidth() / L;
		final double h = canvas.getHeight() / L;

		try {
			final ActionStateList FPlan = Plan.FPlan;
			for( int i = FPlan.numStates(); --i > 0; ) {
				final State p1 = FPlan.state(i - 1);
				final State p2 = FPlan.state(i);
				final int x1 = p1.x();
				final int y1 = p1.y();
				final int x2 = p2.x();
				final int y2 = p2.y();
				if( x1 != x2 || y1 != y2 || p1.charge() != p2.charge() ) {
					final double X1 = (-UFRob1.csWorldMin + x1 + 0.5) * w;
					final double Y1 = (-UFRob1.csWorldMin + y1 + 0.5) * h;
					final double X2 = (-UFRob1.csWorldMin + x2 + 0.5) * w;
					final double Y2 = (-UFRob1.csWorldMin + y2 + 0.5) * h;
					if( Plan.Found ) {
						gc.setStroke(p2.charge() == 0 ? Color.BLUE : Color.GREEN);
					}
					else {
						gc.setStroke(color);
					}
					gc.strokeLine(X1, Y1, X2, Y2);
				}
			}
		}
		catch( Exception e ) {
			// pass
		}
	}

	private void ShowAgents__(final ArtificialMiningWorld FMiningWorld)
	{
		final int L = UFRob1.FWorldLength;
		final double w = canvas.getWidth() / L;
		final double h = canvas.getHeight() / L;

		for( int i = 0; i < FMiningWorld.AgentCount(); i++ ) {
			final Position p = FMiningWorld.Agent(i);
			Shps[i].setTranslateX((-UFRob1.csWorldMin + p.x) * w);
			Shps[i].setTranslateY((-UFRob1.csWorldMin + p.y) * h);
		}
	}

	public void MostraPlanos__(final Plan[] Plans, final ArtificialMiningWorld FMiningWorld)
	{
		Clear__(FMiningWorld);
		int cl = 0;
		for( final Plan Plan : Plans ) {
			ShowPlan__(Plan, UFRob1.FWorldLength, RNDC[cl++]);
			if( cl >= RNDC.length ) {
				cl = 0;
			}
		}
		ShowAgents__(FMiningWorld);
	}

	public void ShowPlan(final Plan Plan, final int FWorldLength)
	{
		Platform.runLater(() -> {
			int cl = MM.random.nextInt(RNDC.length);
			ShowPlan__(Plan, FWorldLength, RNDC[cl]);
		});
	}

	public void MostraPlanos(final Plan[] Plans, final ArtificialMiningWorld FMiningWorld)
	{
		Platform.runLater(() -> {
			MostraPlanos__(Plans, FMiningWorld);
		});
	}
}
