import java.util.Date;
import org.kxml2.kdom.*;

public class Status {
	private int id;
	private String text;
	private Date createdAt;
	private User user;
	
	public Status() {
	
	}
	
	public Status(int id, String text, Date createdAt, User user) {
		this.id = id;
		this.text = text;
		this.createdAt = createdAt;
		this.user = user;
	}
	
	public Status(Element element) {
		for(int i = 0; i < element.getChildCount(); i++) {
			if(element.getType(i) == Node.ELEMENT) {
				Element child = (Element)element.getChild(i);
			
				if(child.getName().compareTo("id") == 0) 
				{
					this.setID(Integer.parseInt(child.getText(0)));
				} 
				else if(child.getName().compareTo("text") == 0) 
				{
					this.setText(child.getText(0));
				} 
				else if(child.getName().compareTo("created_at") == 0) 
				{
					// This is actually pretty in accurate, but we are only going to use an estimate
					// so that is ok.
					// Day MMM dd hh:mm:ss +zzzz yyyy
					
					String date = child.getText(0);
					
					int year = Integer.parseInt(date.substring("Day MMM dd hh:mm:ss +zzzz ".length(), "Day MMM dd hh:mm:ss +zzzz yyyy".length()));
					int month = 0;
					
					String shortMonth = date.substring("Day ".length(), "Day MMM".length());
					
					if(shortMonth.compareTo("Jan") == 0) {
						month = 1;
					} else if(shortMonth.compareTo("Feb") == 0) {
						month = 2;
					} else if(shortMonth.compareTo("Mar") == 0) {
						month = 3;
					} else if(shortMonth.compareTo("Apr") == 0) {
						month = 4;
					} else if(shortMonth.compareTo("May") == 0) {
						month = 5;
					} else if(shortMonth.compareTo("Jun") == 0) {
						month = 6;
					} else if(shortMonth.compareTo("Jul") == 0) {
						month = 7;
					} else if(shortMonth.compareTo("Aug") == 0) {
						month = 8;
					} else if(shortMonth.compareTo("Sep") == 0) {
						month = 9;
					} else if(shortMonth.compareTo("Oct") == 0) {
						month = 10;
					} else if(shortMonth.compareTo("Nov") == 0) {
						month = 11;
					} else if(shortMonth.compareTo("Dec") == 0) {
						month = 12;
					} 
					
					int day = Integer.parseInt(date.substring("Day MMM ".length(), "Day MMM dd".length())) - 1;
					int hour = Integer.parseInt(date.substring("Day MMM dd ".length(), "Day MMM dd hh".length()));
					int minute = Integer.parseInt(date.substring("Day MMM dd hh:".length(), "Day MMM dd hh:mm".length()));
					int seconds = Integer.parseInt(date.substring("Day MMM dd hh:mm:".length(), "Day MMM dd hh:mm:ss".length()));
					
					boolean leapYear = false;
					if (year % 4 == 0) {
						if (year % 100 == 0) {
							leapYear = true;
						} else {
							if (year % 400 == 0) {
								leapYear = true;
							}
						}
					}
					
					// number of seconds in a year
					long secondsSinceEpoch = ((long)year - 1970) * 31556926;
					
					switch(month) {
						case 12:
							secondsSinceEpoch += 2592000;
						case 11:
							secondsSinceEpoch += 2678400;
						case 10:
							secondsSinceEpoch += 2592000;
						case 9:
							secondsSinceEpoch += 2678400;
						case 8:
							secondsSinceEpoch += 2592000;
						case 7:
							secondsSinceEpoch += 2592000;
						case 6:
							secondsSinceEpoch += 2678400;
						case 5:
							secondsSinceEpoch += 2592000;
						case 4:
							secondsSinceEpoch += 2678400;
						case 3:
							secondsSinceEpoch += (leapYear ? 2505600 : 2419200);
						case 2:
							secondsSinceEpoch += 2678400;
						default:
							secondsSinceEpoch += 0;
					}
					
					secondsSinceEpoch += (long)day * (24 * 60 * 60);
					secondsSinceEpoch += (long)hour * (60 * 60);
					secondsSinceEpoch += (long)minute * 60;
					secondsSinceEpoch += (long)seconds;
					
					secondsSinceEpoch *= 1000;
					
					this.createdAt = new Date(secondsSinceEpoch);
				} 
				else if(child.getName().compareTo("user") == 0) 
				{
					this.setUser(new User(child));
				}
			}
		}
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Date getCreatedAt() {
		return this.createdAt;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}