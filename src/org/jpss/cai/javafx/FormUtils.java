package org.jpss.cai.javafx;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FormUtils {

	public static Stage stage( final Node node )
	{
		return (Stage)node.getScene().getWindow();
	}

	public static <T extends Group> T form( final String title, final T form )
	{
		final Stage stage = new Stage();
		stage.setTitle(title);
		final Scene scene = new Scene(form);
		stage.setScene(scene);
//		stage.show();
		return form;
	}

	public static void exit()
	{
		Platform.exit();
	}

}
