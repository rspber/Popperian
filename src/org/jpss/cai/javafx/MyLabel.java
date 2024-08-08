package org.jpss.cai.javafx;

import javafx.scene.control.Label;

public class MyLabel extends Label
{
	public MyLabel( final int x, final int y, final String text )
	{
		super( text );
		setTranslateX(x);
		setTranslateY(y);
	}

	public MyLabel( final String text, final int w )
	{
		super( text );
		setWidth(w);
	}

	public MyLabel( final int x, final int y, final String text, final int w )
	{
		super( text );
		setTranslateX(x);
		setTranslateY(y);
		setWidth(w);
	}

}
