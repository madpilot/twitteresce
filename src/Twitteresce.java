import java.lang.*;
import java.util.*;
import java.lang.Math.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class Twitteresce extends MIDlet {
	private TwitteresceSettings settings;
	
	public Timer timerThread;
	
	private View defaultView;
	private View currentView;
	
	public TwitteresceSettings getSettings() {
		return this.settings;
	}
	
	public Twitteresce() {
		settings = TwitteresceSettings.getSettings();
				
		// Initialise some variables so if there are errors, we can display stuff
		this.defaultView = new TweetsView(this);
		this.currentView = this.defaultView;
	}
	
	public void startApp() {
		if(this.settings.getInitialised()) {
			this.timerThread = new Timer();
			
			if(this.getSettings().getRefreshRate() == 0) {
				this.timerThread.schedule(new TwitteresceThread(this), new Date());
			} else {
				this.timerThread.schedule(new TwitteresceThread(this), new Date(), (long)(this.getSettings().getRefreshRate() * 60 * 1000));
			}
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
		this.currentView = null;
		
		try {
			this.timerThread.cancel();
		} catch (IllegalStateException ise) {
		
		}
	}			
	
	public void setDefaultView(View view) {
		this.defaultView = view;
	}
	
	public View getCurrentView() {
		return this.currentView;
	}
	
	public void setCurrentView(View view) {
		this.currentView = view;
	}
	
	public View getDefaultView() {
		return this.defaultView;
	}
	
	public void displayDefaultView() {
		this.defaultView.display();
		this.currentView = this.defaultView;
	}
}