package org.jpss.cai.javafx;

import javafx.scene.control.Spinner;

public class MySpinner<T> extends Spinner<T> {

	public MySpinner(final int min, final int max, final int value, final int step)
	{
		super(min, max, value, step);
	}

	public void setEnabled(final boolean enabled)
	{
		setDisabled(!enabled);
	}

	public MySpinner(final int min, final int max, final int value, final int step, final int w)
	{
		super(min, max, value, step );
		setPrefWidth(w);
//		setHeight(h);
	}

	public MySpinner(final int x, final int y, final int min, final int max, final int value, final int step, final int w)
	{
		super(min, max, value, step );
		setTranslateX(x);
		setTranslateY(y);
		setPrefWidth(w);
//		setHeight(h);
	}

}
