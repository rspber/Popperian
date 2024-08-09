package org.jpss.cai.mining;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class PMinera extends Application
{
	public void start(final Stage stage)
	{
		try {
			final Forms forms = new Forms();
			stage.setTitle("Popperian Mining Robot");
			stage.setScene(new Scene(forms.form1));
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}
