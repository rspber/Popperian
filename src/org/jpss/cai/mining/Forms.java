package org.jpss.cai.mining;

import org.jpss.cai.javafx.FormUtils;
import org.jpss.cai.libs.ufway44.Plan;

import javafx.application.Platform;
import javafx.stage.Stage;

public class Forms {

	final UFRob1 uFRob1 = new UFRob1(this);	// FormCreate

	final Form1 form1 = new Form1(this);

	final FormOpt formOpt = FormUtils.form("Options...", new FormOpt(this));
	final FormPlans formPlans = FormUtils.form("Plans", new FormPlans());

	public void ShowFormOpt()
	{
		Platform.runLater(() -> {
			final Stage frame1 = FormUtils.stage(form1); 
			final Stage fmOpt = FormUtils.stage(formOpt); 
			fmOpt.show();
			fmOpt.setY(frame1.getY());
			fmOpt.setX(frame1.getX() + frame1.getWidth() + 2);
		});
	}

	public void ShowFormViewPlans()
	{
		Platform.runLater(() -> {
			final Stage frame1 = FormUtils.stage(form1); 
			final Stage fmPlans = FormUtils.stage(formPlans); 
			fmPlans.show();
			fmPlans.setY(frame1.getY());
			fmPlans.setX(frame1.getX() - fmPlans.getWidth() - 10);
		});
	}

	public void setAgents(final Integer mines)
	{
		URobMin2.csMaxAgents = URobMin2.csMiningIdx + mines + URobMin2.csRunners;
		form1.initShps();
		formPlans.initShps();
	}

	public void setAgentsDisable()
	{
		formOpt.EdMines.setDisable(true);
	}

	public double Optimization()
	{
		final double result = formOpt.TBOptimization.getValue();
		return result;
	}

	public boolean EliminateIncorrectPlan()
	{
		final boolean result = formOpt.CBEliminaIncorreto.checked();
		return result;
	}

	public double RandomBehaviour()
	{
		final double result = formOpt.TBRandom.getValue();
		return result;
	}

	public double Speed()
	{
		final double result = formOpt.TBSpeed.getValue();
		return result;
	}

	public boolean CreateNewPlan()
	{
		final boolean result = formOpt.CBNovoPlano.checked();
		return result;
	}

	public boolean ShowPlan()
	{
		final boolean result = formOpt.CBShowPlan.checked();
		return result;
	}

	public void ShowPlan(final Plan Plan, final int FWorldLength)
	{
		if( ChMostra() ) {
			if( ShowPlan() ) { // ------------- show plan
				formPlans.ShowPlan(Plan, FWorldLength);
			}
		}
	}

	public boolean UsePlanning()
	{
		final boolean result = formOpt.CHPlan.isSelected();
		return result;
	}

	public boolean UseLearning()
	{
		final boolean result = formOpt.CHABC.isSelected();
		return result;
	}

	public void setChMostra(final boolean value)
	{
		formOpt.ChMostra.setSelected(value);
	}

	public boolean ChMostra()
	{
		final boolean result = formOpt.ChMostra.checked();
		return result;
	}

	public int edciclos()
	{
		final String result = formOpt.EdCiclos.getText();
		return Integer.parseInt(result);
	}

	public void setLearningMode()
	{
		form1.setLearningMode();
		formOpt.setLearningMode();
	}

	public void setMiningMode()
	{
		form1.setMiningMode();
		formOpt.setMiningMode();
	}

	public void exit()
	{
		uFRob1.FormClose();
		FormUtils.exit();
	}

}
