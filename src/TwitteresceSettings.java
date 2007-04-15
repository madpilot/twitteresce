public class TwitteresceSettings {
	public static final int MODE_PUBLIC = 0;
	public static final int MODE_FRIENDS = 1;
	
	private String username;
	private String password;
	private int timelineMode;
	private boolean automatic;
	private int refreshRate;
	
	private static TwitteresceSettings instance;
	
	private TwitteresceSettings() {
		this.username = "myles@madpilot.com.au";
		this.password = "Ffe3wtEt";
		this.refreshRate = 60;
		this.timelineMode = MODE_FRIENDS;
		this.automatic = false;
	}
	
	public static TwitteresceSettings getSettings() {
		if(TwitteresceSettings.instance == null) {
			TwitteresceSettings.instance = new TwitteresceSettings();
		}
		return TwitteresceSettings.instance;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getTimelineMode() {
		return this.timelineMode;
	}
	
	public void setTimelineMode(int timelineMode) {
		this.timelineMode = timelineMode;
	}
	
	public boolean getAutomatic() {
		return this.automatic;
	}
	
	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}
	
	public int getRefreshRate() {
		return this.refreshRate;
	}
	
	public void setRefreshRate(int refreshRate) {
		this.refreshRate = refreshRate;
	}
}