package org.jpss.cai.mining;

import org.jpss.cai.javafx.MyCheckBox;
import org.jpss.cai.javafx.MyScrollBar;
import org.jpss.cai.javafx.MySpinner;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FormOpt extends Group
{
	FormOpt( final Forms forms )
	{
		final BorderPane borderPane = new BorderPane();
		borderPane.setPrefSize(280, 440);

			final Label LabMines = new Label("Mines:");
			EdMines = new MySpinner<>(1, 15, 3, 1, 60);
			EdMines.valueProperty().addListener((observable, oldValue, newValue) -> {
				forms.setAgents( newValue );
			});
		final TitledPane GroupPopSet = new TitledPane( "Popperian Mining Agent Options: ", new VBox(new HBox( LabMines, EdMines) ) );
			CHPlan = new MyCheckBox("Follow Planning", 155 );
			CHPlan.setDisable(true);
		//	OnChange = CHPlanChange
			CHPlan.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
				}
			});
			ShowPlanningGrid = new MyCheckBox("Show Grid");
			ShowPlanningGrid.setDisable(true);
			ShowPlanningGrid.selectedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
					forms.uFRob1.FMiningWorld.FShowGrid = new_val;
				}
			});
			CHABC = new MyCheckBox("1 step satisfaction");
			CHABC.setSelected(true);
			CHABC.setVisible(false);

			CBNovoPlano = new MyCheckBox( "Create New Plans", 155 );
			CBNovoPlano.setDisable(true);

			CBEliminaIncorreto = new MyCheckBox( "Remove Incorrect Plan" );
			CBEliminaIncorreto.setSelected(true);
			CBEliminaIncorreto.setDisable(true);
//			GroupPopOpt.getChildren().addAll( w HBox(CHPlan, ShowGrid), CHABC, new HBox(CBNovoPlano, NoRove), CBEliminaIncorreto );
		final VBox GroupPopOpt = new VBox(new HBox(CHPlan, ShowPlanningGrid), CHABC, CBNovoPlano, CBEliminaIncorreto );

			TBRandom = new MyScrollBar( 0, 100, 100, 193, 33);
			//	OnChange = TBRandomChange
			GBRandom = new TitledPane( "Random Behaviour: 100%", TBRandom );
			TBRandom.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
				GBRandom.setText( "Random Behaviour: " + new_val.intValue() + "%" );
			});
			GBRandom.setPadding(new Insets(5,0,0,0));

				TBOptimization =  new MyScrollBar( 0, 100, 1, 193, 33);
			//	OnChange = TBOptimizationChange
			GBOtim = new TitledPane( "Optimization: 1%", TBOptimization );
			TBOptimization.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
				GBOtim.setText( "Optimization: " + new_val.intValue() + "%" );
			});
			GBOtim.setPadding(new Insets(5,0,0,0));

				TBSpeed =  new MyScrollBar( 0, 100, 50, 193, 33);
			//	OnChange = TBOptimizationChange
			GBSpeed = new TitledPane( "Speed: 50%", TBSpeed );
			TBSpeed.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
				GBSpeed.setText( "Speed: " + new_val.intValue() + "%" );
			});
			GBSpeed.setPadding(new Insets(5,0,0,0));
		borderPane.setCenter( new VBox(GroupPopSet, GroupPopOpt, GBRandom, GBOtim, GBSpeed) );

			ChMostra = new MyCheckBox( "Show World (CPU Intensive)");
			ChMostra.setSelected(true);
			CBShowPlan = new MyCheckBox( "Show Planning (CPU Intensive)");
				LabCiclos = new Label("Cycles:");
				EdCiclos = new TextField( "2000000" );
		final VBox GroupAmbiente = new VBox( ChMostra, CBShowPlan, new HBox( LabCiclos, EdCiclos ) );
		borderPane.setBottom( new TitledPane( "Virtual World Options : ", GroupAmbiente) );

		getChildren().add(borderPane);
	}

	final Spinner<Integer> EdMines;
	final MyCheckBox CHPlan;
	final MyCheckBox CHABC;
	private final MyCheckBox ShowPlanningGrid;
	final ScrollBar TBRandom;		// TTrackBar
	final TitledPane GBRandom;
	final MyCheckBox ChMostra;
	private final Label LabCiclos;
	final TextField EdCiclos;
	final MyCheckBox CBNovoPlano;
	final MyCheckBox CBEliminaIncorreto;
	final ScrollBar TBOptimization;		// TTrackBar
	final TitledPane GBOtim;
	final ScrollBar TBSpeed;		// TTrackBar
	final TitledPane GBSpeed;
	final MyCheckBox CBShowPlan;
//	private final CheckBoxEnabled CBDebug;

	public void setLearningMode()
	{
		Platform.runLater(() -> {
			TBOptimization.setValue(0);
			ChMostra.setSelected(false);
			CHPlan.setSelected(false);
			ShowPlanningGrid.setSelected(false);
			CBNovoPlano.setSelected(false);
			CBEliminaIncorreto.setSelected(false);
		});
	}

	public void setMiningMode()
	{
		Platform.runLater(() -> {
			TBOptimization.setValue(20);
			CHPlan.setChecked();
			CBNovoPlano.setChecked();
			CBEliminaIncorreto.setChecked();
			ChMostra.setChecked();
			ChMostra.setDisable(false);
			CHPlan.setDisable(false);
			ShowPlanningGrid.setDisable(false);
			CBNovoPlano.setDisable(false);
			CBEliminaIncorreto.setDisable(false);
			CBShowPlan.setChecked();
		});
	}

}
