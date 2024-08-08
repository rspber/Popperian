package org.jpss.cai.javafx;

import javafx.scene.control.ScrollBar;

public class MyScrollBar extends ScrollBar
{
	public MyScrollBar( final int min, final int max, final int value, final int w, final int h ) {
		setMin(min);
		setMax(max);
		setValue(value);
		setMinWidth(w);
		setMinHeight(h);
	}

}
