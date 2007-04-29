import java.util.*;
import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;

public class DirectMessageThread extends TimerTask  {
	private Twitteresce parent;
	private TwitterAPI api;
	
	private Display display;
	
	private boolean firstRun;
		
	public DirectMessageThread(Twitteresce parent) 
	{
		this.parent = parent;
		this.api = new TwitterAPI(parent.getSettings().getUsername(), parent.getSettings().getPassword());
		this.display = Display.getDisplay(this.parent);
		firstRun = true;
	}
	
	public DirectMessageThread(Twitteresce parent, boolean oneShot) 
	{
		this.parent = parent;
		
		this.api = new TwitterAPI(parent.getSettings().getUsername(), parent.getSettings().getPassword());
		this.display = Display.getDisplay(this.parent);
	}
	
	public void Retrieve() 
	{			
		try 
		{			
			Vector messages = this.api.DirectMessages();
			DirectMessageView directMessageView = null;
			try {
				directMessageView = (DirectMessageView)this.parent.getDefaultView();
			} catch (ClassCastException cce) {
				directMessageView = new DirectMessageView(this.parent);
			}

			if(this.parent.getCurrentView().interruptible()) {
				directMessageView.display(messages);
				this.parent.setDefaultView(directMessageView);
				this.parent.setCurrentView(directMessageView);
			}
		}
		catch(HTTPIOException hie) 
		{
			// Only display an error on a one shot
			this.parent.displayDefaultView();
			
			Alert error = new Alert("Can't get direct messages", "Twitter is unavailable, please try again later", null, AlertType.ERROR);
			
			if(hie.getHttpCode() == HttpConnection.HTTP_FORBIDDEN || hie.getHttpCode() == HttpConnection.HTTP_UNAUTHORIZED)
			{
				error = new Alert("You aren't allowed to get direct messages", "It looks like your username or password is incorrect", null, AlertType.ERROR);
			}
			
			try {
				Display.getDisplay(this.parent).setCurrent(error, this.parent.getDefaultView().getDisplayable());
			} catch(IllegalArgumentException iae) {
				// Another thread has slipped in between the DisplayTwits call above and here.
			}
		}
		catch(Exception e)
		{	
			TweetsView tweetsView = new TweetsView(this.parent);
			tweetsView.display();
			
			Alert error = new Alert("Can't get direct messages", "Twitter is unavailable, please try again later", null, AlertType.ERROR);
			
			try {
				Display.getDisplay(this.parent).setCurrent(error, this.parent.getDefaultView().getDisplayable());
			} catch(IllegalArgumentException iae) {
				
			}
		}
	}
		
	public void run() 
	{
		// Always show the gauge on the first run
		if(this.firstRun && this.parent.getCurrentView().interruptible()) {
			this.parent.displayDefaultView();
				
			Alert refreshing = new Alert("Please wait", "Retrieving direct messages...", null, AlertType.INFO);
			Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
			refreshing.setIndicator(gauge);
			refreshing.setTimeout(1000 * 600); // Set the time out really large - once a new displayable is setup this will go away
			
			try {
				this.display.setCurrent(refreshing);
			} catch(IllegalArgumentException iae) {
			
			}
		}
		
		Retrieve();
		this.firstRun = false;
	}
}