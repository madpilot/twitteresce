import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class Twitteresce extends MIDlet implements CommandListener {
	private TwitteresceThread displayThread;
	
	private Form form;
	
	private Command cmdUpdate;
	private Command cmdRefresh;
	private Command cmdSettings;
	private Command cmdExit;
	
	private Command cmdOK;
	private Command cmdBack;
	
	private Command cmdSend;
	private Command cmdCancel;
	
	public Twitteresce() {
		cmdUpdate = new Command("Update", Command.ITEM, 1);
		cmdRefresh = new Command("Refresh", Command.ITEM, 1);
		cmdSettings = new Command("Settings", Command.ITEM, 1);
		cmdExit = new Command("Exit", Command.EXIT, 1);
		
		cmdOK = new Command("OK", Command.ITEM, 1);
		cmdBack = new Command("Back", Command.BACK, 1);
		
		cmdSend = new Command("Send", Command.ITEM, 1);
		cmdCancel = new Command("Cancel", Command.BACK, 1);
		
		displayThread = new TwitteresceThread(Display.getDisplay(this), 10, "friends", false, this);
	}
	
	public void startApp() {
		Display display = Display.getDisplay(this);
		Form loading = new Form("Twitteresce");
		loading.append("Loading...");
		display.setCurrent(loading);
		
		this.displayThread.start();
	}
		
	
	public void pauseApp() {
		
	}
	
	public void destroyApp(boolean unconditional) {
		form = null;
	}
	
	// This will get called by the running thread
	public void DisplayTwits(Vector statuses, String title) {
		Display display = Display.getDisplay(this);
		form = new Form(title);
		
		for(int i = 0; i < statuses.size(); i++) {
			Status status = (Status)statuses.elementAt(i);
			form.append(status.getUser().getScreenName() + ": " + status.getText());
		}
		
		form.addCommand(cmdUpdate);
		form.addCommand(cmdRefresh);
		form.addCommand(cmdSettings);
		form.addCommand(cmdExit);
		
		form.setCommandListener(this);
		
		display.setCurrent(form);
	}
	
	public void DisplayUpdate() {
		Display display = Display.getDisplay(this);
		
		TextBox textBox = new TextBox("Enter you message (Limited to 140 chars)", "", 140, TextField.ANY);
				
		textBox.addCommand(cmdSend);
		textBox.addCommand(cmdCancel);
		
		textBox.setCommandListener(this);
		
		display.setCurrent(textBox);
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
			// Refresh the display again by creating and running a new thread
			TwitteresceThread refreshThread = new TwitteresceThread(Display.getDisplay(this), "friends", this);
			refreshThread.start();
		}
		else if (c == cmdUpdate) 	
		{
			DisplayUpdate();
		}
		else if (c == cmdSettings) 
		{
		
		}
		else if (c == cmdSend)
		{
			TextBox textBox = (TextBox)s;
			UpdateThread updateThread = new UpdateThread(textBox.getString());
			updateThread.start();
			
			Display display = Display.getDisplay(this);
			display.setCurrent(form);
			
			TwitteresceThread refreshThread = new TwitteresceThread(Display.getDisplay(this), "friends", this);
			refreshThread.start();
		}
	}
}