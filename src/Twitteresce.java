import java.util.*;
import java.lang.Math.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class Twitteresce extends MIDlet implements CommandListener {
	private TwitteresceSettings settings;
	private TwitteresceThread displayThread;
	
	private Vector statuses;
	
	// For the main tweets window
	private Command cmdReadTweet;
	private Command cmdPostAt;
	private Command cmdUpdate;
	private Command cmdRefresh;
	private Command cmdSettings;
	private Command cmdExit;
	
	// For Sessions
	private Command cmdSettingsOK;
	private Command cmdSettingsCancel;
	
	// For Sending
	private Command cmdSend;
	private Command cmdSendCancel;
	
	// For Reading Tweets
	private Command cmdReadTweetBack;
	
	private List list;
	
	public TwitteresceSettings getSettings() {
		return this.settings;
	}
	
	public Twitteresce() {
		settings = TwitteresceSettings.getSettings();
		
		cmdReadTweet = new Command("Read", Command.ITEM, 1);
		cmdUpdate = new Command("Post Update", Command.ITEM, 1);
		cmdPostAt = new Command("Post at User", Command.ITEM, 1);
		cmdRefresh = new Command("Refresh", Command.ITEM, 1);
		cmdSettings = new Command("Settings", Command.ITEM, 1);
		cmdExit = new Command("Exit", Command.EXIT, 1);
		
		cmdSettingsOK = new Command("OK", Command.ITEM, 1);
		cmdSettingsCancel = new Command("Cancel", Command.BACK, 1);
		
		cmdSend = new Command("Send", Command.ITEM, 1);
		cmdSendCancel = new Command("Cancel", Command.BACK, 1);
		
		cmdReadTweetBack = new Command("Back", Command.BACK, 1);
	}
	
	public void startApp() {
		if(this.settings.getInitialised()) {
			Display display = Display.getDisplay(this);
			
			Alert loading = new Alert("Please wait", "Retrieving current tweets...", null, AlertType.INFO);
			Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
			loading.setIndicator(gauge);
			loading.setTimeout(1000 * 600); // Set the time out really large - once a new displayable is setup this will go away
			
			display.setCurrent(loading);
			
			this.displayThread = new TwitteresceThread(Display.getDisplay(this), this);
			this.displayThread.start();
		} else {
			// no settings yet - needs to be initialised
			DisplaySettings();
		}
	}
		
	
	public void pauseApp() {
		
	}
	
	public void destroyApp(boolean unconditional) {
		list = null;
		statuses = null;
	}
	
	// This will get called by the running thread
	public void DisplayTwits(Vector statuses, String title) {
		// First we will check to see if it there are new tweets
		int newTweets = 0;
		
		if(this.statuses == null) {
			newTweets = statuses.size();
		} else {
			if(!statuses.isEmpty()) {
				if(this.statuses.size() != statuses.size()) {
					newTweets = Math.max(0, statuses.size() - this.statuses.size());
				} else {
					if(this.statuses.isEmpty()) {
						newTweets = statuses.size();
					} else {
						Enumeration e = statuses.elements();
						while(e.hasMoreElements() && ((Status)e.nextElement()).getID() != ((Status)this.statuses.firstElement()).getID()) {
							newTweets++;
						}
					}
				}
			}
		}
		
		this.statuses = statuses;
		
		Display display = Display.getDisplay(this);
		
		list = new List(title, Choice.IMPLICIT);
		list.setFitPolicy(Choice.TEXT_WRAP_ON);
		
		for(int i = 0; i < statuses.size(); i++) {
			Status status = (Status)statuses.elementAt(i);
			list.append(status.getUser().getScreenName() + ": " + status.getText(), null);
		}
		
		list.setSelectCommand(cmdReadTweet);
		
		list.addCommand(cmdReadTweet);
		list.addCommand(cmdUpdate);
		list.addCommand(cmdPostAt);
		list.addCommand(cmdRefresh);
		list.addCommand(cmdSettings);
		list.addCommand(cmdExit);
		
		list.setCommandListener(this);
		
		
		if(newTweets > 0) {
			/*
			Alert newTweetAlert = new Alert("New Tweets", "There are " + newTweets + " new tweets", null, AlertType.INFO);
			display.setCurrent(newTweetAlert);
			display.vibrate(1);
			display.flashBacklight(1);
			*/
		}
		display.setCurrent(list);
	}
	
	public void DisplayUpdate(String initial) {
		Display display = Display.getDisplay(this);
		
		TextBox textBox = new TextBox("Enter your message", initial, 140, TextField.ANY);
				
		textBox.addCommand(cmdSend);
		textBox.addCommand(cmdSendCancel);
		
		textBox.setCommandListener(this);
		
		display.setCurrent(textBox);
	}
	
	public void DisplaySettings() {
		Display display = Display.getDisplay(this);
		Form form = new Form("Twitteresce settings");
		
		// Item #0
		TextField txtUsername = new TextField("Username", this.settings.getUsername(), 255, TextField.ANY);
		// Item #1
		TextField txtPassword = new TextField("Password", this.settings.getPassword(), 255, TextField.PASSWORD);
		
		// Item #3
		ChoiceGroup choiceTimeline = new ChoiceGroup("Timeline", ChoiceGroup.EXCLUSIVE);
		// This will be 0
		choiceTimeline.append("Public", null); 
		// This will be 1
		choiceTimeline.append("Friends", null);
		choiceTimeline.setSelectedIndex(this.settings.getTimelineMode(), true);
		
		// Item #4
		ChoiceGroup choiceAutomatic = new ChoiceGroup("Refresh", ChoiceGroup.EXCLUSIVE);
		// This will be 0
		choiceAutomatic.append("Manually", null); 
		// This will be 1
		choiceAutomatic.append("Automatically", null);
		choiceAutomatic.setSelectedIndex(this.settings.getAutomatic() ? 1 : 0, true);
		
		// Item #5
		TextField txtRefreshRate = new TextField("Minutes between update", new Integer(this.settings.getRefreshRate()).toString(), 3, TextField.NUMERIC);
		
		form.append(txtUsername);
		form.append(txtPassword);
		form.append(choiceTimeline);
		form.append(choiceAutomatic);
		form.append(txtRefreshRate);
		
		form.addCommand(cmdSettingsOK);
		form.addCommand(cmdSettingsCancel);
		
		form.setCommandListener(this);
		
		display.setCurrent(form);
	}
	
	// Hardware button callbacks
	public void commandAction(Command c, Displayable s) {
		if (c == cmdExit) 
		{
			destroyApp(false);
			notifyDestroyed();
		}
		else if (c == cmdRefresh) 
		{
			// Refresh the display again by creating and running a new thread (in one shot mode)
			(new TwitteresceThread(Display.getDisplay(this), this, true)).start();
		}
		else if (c == cmdUpdate || c == cmdPostAt) 	
		{
			if(c == cmdPostAt) {
				Status status = (Status)statuses.elementAt(list.getSelectedIndex());
				DisplayUpdate("@" + status.getUser().getScreenName() + ": ");
			} else {
				DisplayUpdate("");
			}
		}
		else if (c == cmdSettings) 
		{
			DisplaySettings();
		}
		else if (c == cmdSend)
		{
			TextBox textBox = (TextBox)s;
			UpdateThread updateThread = new UpdateThread(textBox.getString(), this);
			updateThread.start();
			// The thread takes care of the alert gauge and the refreshing of the list...
		}
		else if (c == cmdSendCancel)
		{
			Display display = Display.getDisplay(this);
			display.setCurrent(list);
		}
		else if (c == cmdSettingsCancel)
		{
			Display display = Display.getDisplay(this);
			display.setCurrent(list);
		}
		else if (c == cmdSettingsOK)
		{
			Form settingsForm = (Form)s;
			
			TextField txtUsername = (TextField)settingsForm.get(0);
			TextField txtPassword = (TextField)settingsForm.get(1);
			ChoiceGroup choiceTimeline = (ChoiceGroup)settingsForm.get(2);
			ChoiceGroup choiceAutomatic = (ChoiceGroup)settingsForm.get(3);
			TextField txtRefreshRate = (TextField)settingsForm.get(4);
			
			settings.setUsername(txtUsername.getString());
			settings.setPassword(txtPassword.getString());
			settings.setTimelineMode(choiceTimeline.getSelectedIndex());
			settings.setAutomatic(choiceAutomatic.getSelectedIndex() == 1);
			settings.setRefreshRate(Integer.parseInt(txtRefreshRate.getString()));
			try {
				settings.save();
			} catch (javax.microedition.rms.RecordStoreException rse) {
				
			}
			
			Display display = Display.getDisplay(this);
						
			if(this.displayThread == null) {
				this.displayThread = new TwitteresceThread(display, this);
				this.displayThread.start();
			}
			
			// Need to fire a one shot to show the update alert.
			(new TwitteresceThread(Display.getDisplay(this), this, true)).start();
		}
		else if (c == cmdReadTweet)
		{
			Status status = (Status)statuses.elementAt(list.getSelectedIndex());
			Display display = Display.getDisplay(this);
			
			Form form = new Form("Update from " + status.getUser().getScreenName());
			form.append(status.getText());
			
			form.addCommand(cmdReadTweetBack);
			
			form.setCommandListener(this);
		
			display.setCurrent(form);
		}
		else if (c == cmdReadTweetBack)
		{
			Display display = Display.getDisplay(this);
			display.setCurrent(list);
		}
	}
}