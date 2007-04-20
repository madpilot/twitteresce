import java.util.*;
import java.lang.Math.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class Twitteresce extends MIDlet {
	private TwitteresceSettings settings;
	public TwitteresceThread displayThread;
	private View defaultView;
	
	public TwitteresceSettings getSettings() {
		return this.settings;
	}
	
	public Twitteresce() {
		settings = TwitteresceSettings.getSettings();
				
		// Initialise some variables so if there are errors, we can display stuff
		this.defaultView = new TweetsView(this);
	}
	
	public void startApp() {
		if(this.settings.getInitialised()) {
			Display display = Display.getDisplay(this);
	
			Alert loading = new Alert("Please wait", "Retrieving current tweets...", null, AlertType.INFO);
			Gauge gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
			loading.setIndicator(gauge);
			loading.setTimeout(1000 * 600); // Set the time out really large - once a new displayable is setup this will go away
			
			display.setCurrent(loading);
		
			this.displayThread = new TwitteresceThread(this);
			this.displayThread.start();
		} else {
			// no settings yet - needs to be initialised
			SettingsView settings = new SettingsView(this);
			settings.display();
		}
	}
		
	
	public void pauseApp() {
		Display display = Display.getDisplay(this);
		display.setCurrent(null);
	}
	
	public void destroyApp(boolean unconditional) {
		this.defaultView = null;
	}			
	
	public void setDefaultView(View view) {
		this.defaultView = view;
	}
	
	public View getDefaultView() {
		return this.defaultView;
	}
	
	public void displayDefaultView() {
		this.defaultView.display();
	}
}