import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class SettingsView implements View, CommandListener {
	private Command cmdOK;
	private Command cmdCancel;
	private Twitteresce parent;
	private Form form;
	
	public SettingsView(Twitteresce parent) {
		this.parent = parent;
		
		form = new Form("Twitteresce settings");
		
		cmdOK = new Command("Ok", Command.ITEM, 1);
		cmdCancel = new Command("Cancel", Command.BACK, 1);
	}
	
	public boolean interruptible() {
		return false;
	}
	
	public void display() {
		this.parent.setCurrentView(this);
		// Item #0
		TextField txtUsername = new TextField("Username", this.parent.getSettings().getUsername(), 255, TextField.ANY);
		// Item #1
		TextField txtPassword = new TextField("Password", this.parent.getSettings().getPassword(), 255, TextField.PASSWORD);
		
		// Item #3
		ChoiceGroup choiceTimeline = new ChoiceGroup("Timeline", ChoiceGroup.EXCLUSIVE);
		// This will be 0
		choiceTimeline.append("Public", null); 
		// This will be 1
		choiceTimeline.append("Friends", null);
		choiceTimeline.setSelectedIndex(this.parent.getSettings().getTimelineMode(), true);
		
		// Item #4
		ChoiceGroup choiceRefreshRate = new ChoiceGroup("Automatically retrieve tweets?", ChoiceGroup.EXCLUSIVE);
		// This will be 0
		choiceRefreshRate.append("No", null); 
		// This will be 1
		choiceRefreshRate.append("Every minute", null);
		// This will be 2
		choiceRefreshRate.append("Every 2 minutes", null);
		// This will be 3
		choiceRefreshRate.append("Every 5 minutes", null);
		// This will be 4
		choiceRefreshRate.append("Every 10 minutes", null);
		// This will be 5
		choiceRefreshRate.append("Every 30 minutes", null);
		// This will be 6
		choiceRefreshRate.append("Every hour", null);
		
		int selected = 0;
		switch (this.parent.getSettings().getRefreshRate()) {
			case 1:
				selected = 1;
				break;
			case 2:
				selected = 2;
				break;
			case 5:
				selected = 3;
				break;
			case 10:
				selected = 4;
				break;
			case 30:
				selected = 5;
				break;
			case 60:
				selected = 6;
				break;
			default:
				selected = 0;
		}
		
		choiceRefreshRate.setSelectedIndex(selected, true);
		
		form.append(txtUsername);
		form.append(txtPassword);
		form.append(choiceTimeline);
		form.append(choiceRefreshRate);
		
		form.addCommand(cmdOK);
		form.addCommand(cmdCancel);
		
		form.setCommandListener(this);
		
		Display.getDisplay(this.parent).setCurrent(form);
	}
	
	private void save(Form settingsForm) {
		this.parent.displayDefaultView();
		
		TextField txtUsername = (TextField)settingsForm.get(0);
		TextField txtPassword = (TextField)settingsForm.get(1);
		ChoiceGroup choiceTimeline = (ChoiceGroup)settingsForm.get(2);
		ChoiceGroup choiceRefreshRate = (ChoiceGroup)settingsForm.get(3);
		
		this.parent.getSettings().setUsername(txtUsername.getString());
		this.parent.getSettings().setPassword(txtPassword.getString());
		this.parent.getSettings().setTimelineMode(choiceTimeline.getSelectedIndex());
		
		switch(choiceRefreshRate.getSelectedIndex()) {
			case 1:
				this.parent.getSettings().setRefreshRate(1);
				break;
			case 2:
				this.parent.getSettings().setRefreshRate(2);
				break;
			case 3:
				this.parent.getSettings().setRefreshRate(5);
				break;
			case 4:
				this.parent.getSettings().setRefreshRate(10);
				break;
			case 5:
				this.parent.getSettings().setRefreshRate(30);
				break;
			case 6:
				this.parent.getSettings().setRefreshRate(60);
				break;
			default:
				this.parent.getSettings().setRefreshRate(0);
		}
		
		try {
			this.parent.getSettings().save();
		} catch (javax.microedition.rms.RecordStoreException rse) {
			
		}
		
		this.parent.timerThread.cancel();
		this.parent.timerThread = new Timer();
		
		if(this.parent.getSettings().getRefreshRate() == 0) {
			this.parent.timerThread.schedule(new TwitteresceThread(this.parent), new Date());
		} else {
			this.parent.timerThread.schedule(new TwitteresceThread(this.parent), new Date(), (long)(this.parent.getSettings().getRefreshRate() * 60 * 1000));
		}
	}
	
	public Displayable getDisplayable() {
		return this.form;
	}
	
	public void commandAction(Command c, Displayable s) {
		if(c == cmdOK) 
		{
			this.save((Form)s);
			
			this.parent.timerThread.cancel();
			this.parent.timerThread = new Timer();
			
			// Restart the timer if the refresh rate has been changed
			if(this.parent.getSettings().getRefreshRate() == 0) {
				this.parent.timerThread.schedule(new TwitteresceThread(this.parent), new Date());
			} else {
				this.parent.timerThread.schedule(new TwitteresceThread(this.parent), new Date(), (long)(this.parent.getSettings().getRefreshRate() * 60 * 1000));
			}
		}
		else if(c == cmdCancel) 
		{
			this.parent.displayDefaultView();
		}
	}
}