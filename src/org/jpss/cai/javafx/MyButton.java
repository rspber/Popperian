package org.jpss.cai.javafx;

import javafx.scene.control.Button;

public class MyButton extends Button {

	public MyButton(final String text)
	{
		super(text);
	}

	public void setEnabled(final boolean enabled)
	{
		setDisabled(!enabled);
	}

	public MyButton(final String text, final int w, final int h)
	{
		super(text);
		setWidth(w);
		setHeight(h);
	}

	public MyButton(final int x, final int y, final String text, final int w, final int h)
	{
		super(text);
		setTranslateX(x);
		setTranslateY(y);
		setWidth(w);
		setHeight(h);
	}

}
