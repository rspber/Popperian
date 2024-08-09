package org.jpss.cai.mining;

import org.jpss.cai.javafx.MyButton;
import org.jpss.cai.javafx.MyLabel;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;

public class Form1 extends Group {

	public void initShps()
	{
		Shps = new Shape[URobMin2.csMaxAgents];
		Labs = new Label[URobMin2.csMaxAgents];
		Shps[0] = new AgentShapes.TBase(142,  40, 15);
		Labs[0] = new MyLabel(158, 42, "");
		int y = 66;
		int i = 1;
		for( ; i < URobMin2.csMaxAgents - URobMin2.csRunners; ++i ) {
			Shps[i] = new AgentShapes.TAgent(142,  y, 10, AgentShapes.RNDC[i] );
			Labs[i] = new MyLabel(158,  y, "");
			y += 24;
		}
		while( i < URobMin2.csMaxAgents ) {
			Shps[i] = new AgentShapes.TRunner(142, y, 10 );
			Labs[i] = new MyLabel(158, y, "");
			++i;
		}
		GrMundo.getChildren().clear();
		GrMundo.getChildren().addAll(Shps);
		GrMundo.getChildren().addAll(Labs);
		
	}

	Form1(final Forms forms)
	{
		final BorderPane borderPane = new BorderPane();
		borderPane.setPrefSize(432, 441);

		GrMundo = new Pane();
		initShps();

		final Label LabCiclos;
		LabCiclos = new Label("Cycles: ");
		LabNMoves = new Label("0");
		final Button BBComeca;
		BBComeca = new MyButton("&Start", 75, 32);
		BBComeca.setOnAction( new EventHandler<ActionEvent>() {
			public void handle(ActionEvent exent) {
				Task<Void> task = new Task<Void>() {
					@Override 
					public Void call() throws Exception {
						forms.uFRob1.RunManySimulations();
						return null ;
					}
				};
				forms.setAgentsDisable();
				new Thread(task).start();
			}
		});
		BBOpcoes = new MyButton("&Options", 75, 32);
		BBOpcoes.setOnAction( new EventHandler<ActionEvent>() {
			public void handle(ActionEvent exent) {
				forms.ShowFormOpt();
			}
		});
		BBPlanos = new MyButton("&Plans", 88, 32);
//		BBPlanos.setEnabled(false);
		BBPlanos.setOnAction( new EventHandler<ActionEvent>() {
			public void handle(ActionEvent exent) {
				forms.ShowFormViewPlans();
			}
		});

		final Button BBSair;
		BBSair = new Button("&Quit");
		BBSair.setOnAction( new EventHandler<ActionEvent>() {
			public void handle(ActionEvent exent) {
				forms.exit();
			}
		});

		LBStatus = new MyLabel("Idle - Please Press Start", 150);

		HBox box1 = new HBox(BBComeca, BBOpcoes, BBPlanos);
		box1.setSpacing(10);
		HBox box3 = new HBox(LabCiclos, LabNMoves);
		box3.setSpacing(10);
		HBox box4 = new HBox(LBStatus);
		box4.setSpacing(10);
		HBox box5 = new HBox(/*BBMedicao, */BBSair);
		box5.setSpacing(10);
		box5.setAlignment(Pos.BASELINE_RIGHT);
		final GridPane pane2 = new GridPane();
		pane2.setPadding(new Insets(0, 10, 10, 10));
		pane2.setHgap(30);
		pane2.setVgap(10);
		pane2.add(box1, 0, 0);
		pane2.add(new VBox( box3, box4), 0, 1);
		pane2.add(box5, 1, 1);

		borderPane.setCenter(GrMundo);
		borderPane.setBottom(pane2);

		getChildren().add(borderPane);
	}

	private final Pane GrMundo;
	private final Label LabNMoves;
	private final MyButton BBOpcoes;
	private final MyButton BBPlanos;
	private final Label LBStatus;

	private Shape[] Shps;
	private Label[] Labs;

	private void MostraWorld__(final ArtificialMiningWorld FMiningWorld)
	{
		final int L = UFRob1.FWorldLength;
		final double w = GrMundo.getWidth() / L;
		final double h = GrMundo.getHeight() / L;
		for( int i = 0; i < FMiningWorld.AgentCount(); i++ ) {
			final Position p = FMiningWorld.Agent(i);
			final double x = (-UFRob1.csWorldMin + p.x) * w;
			final double y = (-UFRob1.csWorldMin + p.y) * h;
			final Shape shp = Shps[i];
			shp.setTranslateX(x);
			shp.setTranslateY(y);
			final Label lab = Labs[i];
			lab.setTranslateX(x+15);
			lab.setTranslateY(y+5);
			lab.setText(""  + p.Charge);
		}
		LabNMoves.setText("" + FMiningWorld.NMoves);
	}

	public void MostraWorld(final ArtificialMiningWorld FMiningWorld)
	{
		Platform.runLater(() -> {
			MostraWorld__(FMiningWorld);
		});
	}
	
	public void setLearningMode()
	{
		Platform.runLater(() -> {
			LBStatus.setText("Neural Network is Learning - Please Wait");
//			BBPlanos.setEnabled(false);
//			BBOpcoes.setEnabled(false);
		});
	}

	public void setMiningMode()
	{
		Platform.runLater(() -> {
			LBStatus.setText("Robot is Mining - Please Enjoy :-)");
			BBPlanos.setEnabled(true);
			BBOpcoes.setEnabled(true);
		});
	}

}
