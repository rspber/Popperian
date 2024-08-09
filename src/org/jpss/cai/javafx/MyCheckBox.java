package org.jpss.cai.javafx;

import javafx.scene.control.CheckBox;

public class MyCheckBox extends CheckBox
{
	public MyCheckBox(final String text)
	{
		super(text);
	}

	public MyCheckBox(final String text, final int w)
	{
		super(text);
		setMinWidth(w);
	}
/*
	public void setEnabled(final boolean enabled)
	{
		setDisabled(!enabled);
	}
*/
	public boolean checked()
	{
		return isSelected();
	}

	public void setChecked()
	{
		setSelected(true);
	}

}
