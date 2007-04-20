import java.util.*;
import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;

public class TwitteresceThread extends Thread  {
	private Twitteresce parent;
	private TwitterAPI api;
	private boolean oneShot;
	
	private Display display;
		
	public TwitteresceThread(Display display, Twitteresce parent) 
	{
		this.parent = parent;
		
		this.api = new TwitterAPI(parent.getSettings().getUsername(), parent.getSettings().getPassword());
		this.display = display;
		this.oneShot = false;
	}
	
	public TwitteresceThread(Display display, Twitteresce parent, boolean oneShot) 
	{
		this.parent = parent;
		
		this.api = new TwitterAPI(parent.getSettings().getUsername(), parent.getSettings().getPassword());
		this.display = display;
		this.oneShot = oneShot;
	}
	
	public void Retrieve() 
	{
		try 
		{			
			if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_FRIENDS) 
			{						
				Vector statuses = this.api.FriendsTimeLine();
				// Let's try displaying in labels to see if it is working
				this.parent.DisplayTwits(statuses, "Twitteresce - Friends");
			} 
			else if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_PUBLIC) 
			{
				Vector statuses = this.api.PublicTimeLine();
				// Let's try displaying in labels to see if it is working
				this.parent.DisplayTwits(statuses, "Twitteresce - Public");
			}
			else if (this.parent.getSettings().getTimelineMode() == TwitteresceSettings.MODE_DIRECT) 
			{
				Vector messages = this.api.DirectMessages();
				// Let's try displaying in labels to see if it is working
				this.parent.DisplayDirectMessages(messages);
			}
		}
		catch(HTTPIOException hie) 
		{
			// Only display an error on a one shot
			this.parent.DisplayTwits();
			
			Alert error = new Alert("Can't get updates", "Twitter is unavailable, please try again later", null, AlertType.ERROR);
			
			if(hie.getHttpCode() == HttpConnection.HTTP_FORBIDDEN || hie.getHttpCode() == HttpConnection.HTTP_UNAUTHORIZED)
			{
				error = new Alert("You aren't allowed to get updated", "It looks like your username or password is incorrect", null, AlertType.ERROR);
			}
			
			try {
				this.display.setCurrent(error, this.parent.list);
			} catch(IllegalArgumentException iae) {
				// Another thread has slipped in between the DisplayTwits call above and here.
			}
		}
		catch(Exception e)
		{	
			this.parent.DisplayTwits();
			
			Alert error = new Alert("Can't get updates", "Twitter is unavailable, please try again later", null, AlertType.ERROR);
			
			try {
				this.display.setCurrent(error, this.parent.list);
			} catch(IllegalArgumentException iae) {
				
			}
		}
	}
		
	public void run() 
	{
		// Always show the gauge on the first run
		this.parent.DisplayTwits();
				
		Alert refreshing = new Alert("Please wait", "Retrieving current tweets...", null, AlertType.INFO);
		Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
		refreshing.setIndicator(gauge);
		refreshing.setTimeout(1000 * 600); // Set the time out really large - once a new displayable is setup this will go away
		
		try {
			this.display.setCurrent(refreshing);
		} catch(IllegalArgumentException iae) {
		
		}
		
		do
		{
			Retrieve();
			// Sleep for a bit
			try 
			{
				this.sleep(this.parent.getSettings().getRefreshRate() * 1000 * 60);
			}
			catch (InterruptedException ie)
			{
			
			}
		}
		while(!this.oneShot && this.parent.getSettings().getRefreshRate() > 0);
	}
}