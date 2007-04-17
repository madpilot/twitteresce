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
			if(this.oneShot) {
				Alert refreshing = new Alert("Please wait", "Retrieving current tweets...", null, AlertType.INFO);
				Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
				refreshing.setIndicator(gauge);
				refreshing.setTimeout(1000 * 600); // Set the time out really large - once a new displayable is setup this will go away
				this.display.setCurrent(refreshing);
			}
			
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
		}
		catch(HTTPIOException hie) 
		{
			// Only display an error on a one shot
			if(this.oneShot) 
			{
				Alert error = new Alert("Can't get updates", "Please try again later", null, AlertType.ERROR);
				if(hie.getHttpCode() == HttpConnection.HTTP_FORBIDDEN) 
				{
					error = new Alert("You aren't allowed to get updated", "Please check your username and password", null, AlertType.ERROR);
				}
				else if(hie.getHttpCode() == HttpConnection.HTTP_INTERNAL_ERROR) 
				{
					error = new Alert("Can't get updates", "The server is down, please try again later", null, AlertType.ERROR);
				}
				
				this.display.setCurrent(error);
			}
		}
		catch(IOException ioe)
		{
			if(this.oneShot) 
			{
				Alert error = new Alert("Can't get updates", "Please try again later", null, AlertType.ERROR);
				this.display.setCurrent(error);
			}
		}
	}
		
	public void run() 
	{
		// Run once, then keep running if continuous, otherwise return.
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
		while(!this.oneShot && this.parent.getSettings().getRefreshRate() > 0 && this.parent.getSettings().getAutomatic());
	}
}