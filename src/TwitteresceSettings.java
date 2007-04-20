import javax.microedition.rms.*;

public class TwitteresceSettings {
	public static final int MODE_PUBLIC = 0;
	public static final int MODE_FRIENDS = 1;
	public static final int MODE_DIRECT = 2;
		
	private boolean initialised;
	private String username;
	private String password;
	private int timelineMode;
	private boolean automatic;
	private int refreshRate;
	
	private static TwitteresceSettings instance;
	
	static final String RECORD_STORE = "Twitteresce";
	
	private TwitteresceSettings() {
		this.initialised = false;
		// Read in the settings from the record store. If anything is missing, open the settings window
		try
		{
			this.read();
			this.initialised = true;
		}
		catch(RecordStoreException e) {
			// There was an error - this will probably trigger the Settings system
		}
	}
	
	public static TwitteresceSettings getSettings() {
		if(TwitteresceSettings.instance == null) {
			TwitteresceSettings.instance = new TwitteresceSettings();
		}
		return TwitteresceSettings.instance;
	}
	
	public boolean getInitialised() {
		return this.initialised;
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
	
	public void read() throws RecordStoreException {
		boolean usernameSet = false;
		boolean passwordSet = false;
		boolean refreshRateSet = false;
		boolean timelineModeSet = false;
		boolean automaticSet = false;
		
		RecordStore store = RecordStore.openRecordStore(RECORD_STORE, true);
		
		RecordEnumeration re = store.enumerateRecords(null, null, false);		
		
		while(re.hasNextElement()) {
			String str = new String(re.nextRecord());
			if(str.startsWith("username:")) 
			{
				this.setUsername(str.substring("username:".length()));
				usernameSet = true;
			}
			else if (str.startsWith("password:"))
			{
				this.setPassword(str.substring("password:".length()));
				passwordSet = true;
			}
			else if (str.startsWith("refreshrate:"))
			{
				this.setRefreshRate(Integer.parseInt(str.substring("refreshrate:".length())));
				refreshRateSet = true;
				
			}
			else if (str.startsWith("timelinemode:"))
			{
				this.setTimelineMode(Integer.parseInt(str.substring("timelinemode:".length())));
				timelineModeSet = true;
				
			}
			else if (str.startsWith("automatic:"))
			{
				this.setAutomatic(str.substring("automatic:".length()) == "true");
				automaticSet = true;
				
			}
		}
		
		if(!usernameSet || !passwordSet || !refreshRateSet || !timelineModeSet || !automaticSet) {
			throw new RecordStoreException();
		}
	}
	
	public void save() throws RecordStoreException {
		Integer usernameID = null;
		Integer passwordID = null;
		Integer refreshRateID = null;
		Integer timelineModeID = null;
		Integer automaticID = null;
		
		RecordStore store = RecordStore.openRecordStore(RECORD_STORE, true);
		
		// Check to see if the the records need to be created or updated
		for (int i = 1; i <= store.getNumRecords(); i++) {
			byte[] record = store.getRecord(i);
			String str = new String(record, 0, record.length);
			
			// Rewind to get the the ID
			Integer id = new Integer(i);
			
			if(str.startsWith("username:")) 
			{
				usernameID = id;
			}
			else if (str.startsWith("password:"))
			{
				passwordID = id;
			}
			else if (str.startsWith("refreshrate:"))
			{
				refreshRateID = id;
				
			}
			else if (str.startsWith("timelinemode:"))
			{
				timelineModeID = id;
				
			}
			else if (str.startsWith("automatic:"))
			{
				automaticID = id;
			}
		}
		
		byte[] bUsername = ("username:" + getUsername()).getBytes();
		byte[] bPassword = ("password:" + getPassword()).getBytes();
		byte[] bRefreshRate = ("refreshrate:" + (new Integer(getRefreshRate())).toString()).getBytes();
		byte[] bTimelineMode = ("timelinemode:" + (new Integer(getTimelineMode())).toString()).getBytes();
		byte[] bAutomatic = ("automatic:" + (getAutomatic() ? "true" : "false")).getBytes();
		
		if(usernameID == null) {
			store.addRecord(bUsername, 0, bUsername.length);
		} else {
			store.setRecord(usernameID.intValue(), bUsername, 0, bUsername.length);
		}
		
		if(passwordID == null) {
			store.addRecord(bPassword, 0, bPassword.length);
		} else {
			store.setRecord(passwordID.intValue(), bPassword, 0, bPassword.length);
		}
		
		if(refreshRateID == null) {
			store.addRecord(bRefreshRate, 0, bRefreshRate.length);
		} else {
			store.setRecord(refreshRateID.intValue(), bRefreshRate, 0, bRefreshRate.length);
		}
		
		if(timelineModeID == null) {
			store.addRecord(bTimelineMode, 0, bTimelineMode.length);
		} else {
			store.setRecord(timelineModeID.intValue(), bTimelineMode, 0, bTimelineMode.length);
		}
		
		if(automaticID == null) {
			store.addRecord(bAutomatic, 0, bAutomatic.length);
		} else {
			store.setRecord(automaticID.intValue(), bAutomatic, 0, bAutomatic.length);
		}
	}
}