import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class TweetsView implements View, CommandListener {
	private Command cmdReadTweet;
	private Command cmdPostAt;
	private Command cmdUpdate;
	private Command cmdRefresh;
	private Command cmdSettings;
	private Command cmdExit;
	private Command cmdClose;
	private Command cmdDirectMessages;
	private Command cmdAbout;
	
	private Twitteresce parent;
	
	private Vector statuses;
	
	private List list;
	
	public TweetsView(Twitteresce parent) {
		this.parent = parent;
		
		cmdReadTweet = new Command("Read", Command.ITEM, 1);
		cmdUpdate = new Command("Post Tweet", Command.ITEM, 2);
		cmdPostAt = new Command("Post at User", Command.ITEM, 3);
		cmdDirectMessages = new Command("Direct Message User", Command.ITEM, 4);
		cmdRefresh = new Command("Refresh", Command.ITEM, 5);
		cmdSettings = new Command("Settings", Command.ITEM, 6);
		cmdAbout = new Command("About", Command.ITEM, 6);
		cmdExit = new Command("Exit", Command.ITEM, 7);
		cmdClose = new Command("Close", Command.EXIT, 1);
		
		this.statuses = new Vector();
		this.list = new List("Twitteresce", Choice.IMPLICIT);
		
		this.list.addCommand(cmdUpdate);
		this.list.addCommand(cmdRefresh);
		this.list.addCommand(cmdSettings);
		this.list.addCommand(cmdAbout);
		this.list.addCommand(cmdExit);
		this.list.addCommand(cmdClose);
	}
	
	public void display() {
		this.display(this.statuses);
	}
	
	public void display(Vector statuses) {
		Display display = Display.getDisplay(this.parent);
		
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
		
		String title = "Twitteresce";
		if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_FRIENDS) {
			title = "Twitteresce - Friends";
		} else if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_PUBLIC) {
			title = "Twitteresce - Public";
		}
		
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
		//list.addCommand(cmdDirect);
		list.addCommand(cmdSettings);
		list.addCommand(cmdAbout);
		list.addCommand(cmdExit);
		list.addCommand(cmdClose);
		
		list.setCommandListener(this);
		
		display.setCurrent(list);
		
		if(newTweets > 0) {
			Alert newTweetAlert = new Alert("New Tweets", "There are " + newTweets + " new tweets", null, AlertType.INFO);
			try {
				display.setCurrent(newTweetAlert, list);
			} catch(IllegalArgumentException iae) {
			
			}
		}
	}
	
	public void commandAction(Command c, Displayable s) {
		// Exit the entire app
		if (c == cmdExit) 
		{
			this.parent.destroyApp(false);
			this.parent.notifyDestroyed();
		}
		// Just close the app - it's still open, but hidden
		else if (c == cmdClose) 
		{
			this.parent.pauseApp();
			this.parent.notifyPaused();
		}
		// Refresh the tweet list
		else if (c == cmdRefresh) 
		{
			// Refresh the display again by creating and running a new thread (in one shot mode)
			(new TwitteresceThread(this.parent, true)).start();
		}
		// Display the tweet sender
		else if (c == cmdUpdate)
		{
			PostView postView = new PostView(this.parent);
			postView.display();
		}
		else if (c == cmdPostAt) 	
		{
			PostView postView = new PostView(this.parent);
			
			Status status = (Status)statuses.elementAt(list.getSelectedIndex());
			postView.display("@" + status.getUser().getScreenName() + ": ");
			
		}
		// Display the settings screen
		else if (c == cmdSettings) 
		{
			// Refresh the settings to make sure they aren't stale
			try {
				this.parent.getSettings().read();
			} catch(javax.microedition.rms.RecordStoreException rse) {
			
			}
			SettingsView settings = new SettingsView(this.parent);
			settings.display();
		}
		// Read the selected tweet
		else if (c == cmdReadTweet)
		{
			Status status = (Status)statuses.elementAt(list.getSelectedIndex());
				
			ReadTweetView readTweetView = new ReadTweetView(this.parent, status);
			readTweetView.display();
		}
		// Select the about screen
		else if (c == cmdAbout) 
		{
			AboutView about = new AboutView(this.parent);
			about.display();
		}
	}
	
	public Displayable getDisplayable() {
		return this.list;
	}
}