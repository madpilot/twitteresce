import java.util.*;
import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;

public class TwitteresceThread extends TimerTask  {
	private Twitteresce parent;
	private TwitterAPI api;
	private boolean firstRun;
	
	private Display display;
		
	public TwitteresceThread(Twitteresce parent) 
	{
		this.parent = parent;
		this.api = new TwitterAPI(parent.getSettings().getUsername(), parent.getSettings().getPassword());
		this.display = Display.getDisplay(this.parent);
		this.firstRun = true;
	}
	
	public void Retrieve() 
	{			
		try 
		{			
			if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_FRIENDS) 
			{						
				Vector statuses = this.api.FriendsTimeLine();
				TweetsView tweetsView = null;
				try {
					tweetsView = (TweetsView)this.parent.getDefaultView();
				} catch (ClassCastException cce) {
					tweetsView = new TweetsView(this.parent);
				}
				
				if(this.parent.getCurrentView().interruptible()) {
					tweetsView.display(statuses);
					this.parent.setDefaultView(tweetsView);
					this.parent.displayDefaultView();
				}
			} 
			else if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_PUBLIC) 
			{
				Vector statuses = this.api.PublicTimeLine();
				TweetsView tweetsView = null;
				try {
					tweetsView = (TweetsView)this.parent.getDefaultView();
				} catch (ClassCastException cce) {
					tweetsView = new TweetsView(this.parent);
				}
				
				if(this.parent.getCurrentView().interruptible()) {
					tweetsView.display(statuses);
					this.parent.setDefaultView(tweetsView);
					this.parent.displayDefaultView();
				}
			}
		}
		catch(HTTPIOException hie) 
		{
			// Only display an error on a one shot
			this.parent.displayDefaultView();
			
			Alert error = new Alert("Can't get updates", "Twitter is unavailable, please try again later", null, AlertType.ERROR);
			
			if(hie.getHttpCode() == HttpConnection.HTTP_FORBIDDEN || hie.getHttpCode() == HttpConnection.HTTP_UNAUTHORIZED)
			{
				error = new Alert("You aren't allowed to get updated", "It looks like your username or password is incorrect", null, AlertType.ERROR);
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
			
			Alert error = new Alert("Can't get updates", "Twitter is unavailable, please try again later", null, AlertType.ERROR);
			
			try {
				Display.getDisplay(this.parent).setCurrent(error, this.parent.getDefaultView().getDisplayable());
			} catch(IllegalArgumentException iae) {
				
			}
		}
	}
		
	public void run() 
	{		
		if(this.firstRun && this.parent.getCurrentView().interruptible()) {
			// Always show the gauge on the first run
			this.parent.displayDefaultView();
		
			Alert refreshing = new Alert("Please wait", "Retrieving current tweets...", null, AlertType.INFO);
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