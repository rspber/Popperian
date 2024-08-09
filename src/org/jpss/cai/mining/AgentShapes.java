package org.jpss.cai.mining;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

public class AgentShapes {

	static final Color[] RNDC = new Color[] {
		Color.YELLOW,
		Color.AQUA,
		Color.TEAL,
		Color.INDIGO,
		Color.BLUEVIOLET,
		Color.AQUAMARINE,
		Color.BURLYWOOD,
		Color.CHARTREUSE,
		Color.CORAL,
		Color.CORNFLOWERBLUE,
		Color.CRIMSON,
		Color.DARKGOLDENROD,
		Color.DARKGRAY,
		Color.DARKORANGE,
		Color.DARKGOLDENROD
	};

	static class TBase extends Rectangle {
		TBase(final int x, final int y, final int w ) {
			super(0, 0, w, w );
			setTranslateX(x);
			setTranslateY(y);
			setArcWidth(5);
			setArcHeight(5);
			setFill(Color.RED);
			setStroke(Color.MAROON);
			setStrokeWidth(1);
		}
	}
	
	static class TAgent extends Rectangle {
		TAgent(final int x, final int y, final int w, final Color color ) {
			super(0, 0, w, w );
			setTranslateX(x);
			setTranslateY(y);
			setFill(color);
		}
	}

	static class TRunner extends Ellipse {
		TRunner(final int x, final int y, final int w ) {
			super(0, 0, w, w );
			setTranslateX(x);
			setTranslateY(y);
			setFill(Color.BLUE);
			setRadiusX(5d);
			setRadiusY(5d);
		}
	}

}
