import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class AboutView implements View, CommandListener {
	private Command cmdBack;
	private Twitteresce parent;
	private Form form;
	
	public AboutView(Twitteresce parent) {
		this.parent = parent;
		
		form = new Form("About Twitteresce");
		cmdBack = new Command("Back", Command.EXIT, 1);
		
	}
	
	public void display() {
		
		StringItem about = new StringItem(null, "Twitteresce version 0.4\n\nBy MadPilot Productions 2007\n\nMadPilot has no association with Twitter and can't guarentee if their services is up\n\nPlease check http://www.madpilot.com.au/twitteresce for updates.");

		form.append(about);
		
		form.addCommand(cmdBack);
		
		form.setCommandListener(this);
		
		Display.getDisplay(this.parent).setCurrent(form);
	}
	
	public Displayable getDisplayable() {
		return this.form;
	}
	
	public void commandAction(Command c, Displayable s) {
		if(c == cmdBack) {
			this.parent.displayDefaultView();
		}
	}
}