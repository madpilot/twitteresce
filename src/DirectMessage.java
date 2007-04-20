import java.util.Date;
import org.kxml2.kdom.*;

public class DirectMessage {
	private int id;
	private String text;
	private int senderID;
	private int recipientID;
	private Date createdAt;
	private String senderScreenName;
	private String recipientScreenName;
	
	public DirectMessage() {
	
	}
	
	public DirectMessage(int id, String text, int senderID, int recipientID, Date createdAt, String senderScreenName, String recipientScreenName) {
		this.id = id;
		this.text = text;
		this.senderID = senderID;
		this.recipientID = recipientID;
		this.createdAt = createdAt;
		this.senderScreenName = senderScreenName;
		this.recipientScreenName = recipientScreenName;
	}
	
	public DirectMessage(Element element) {
		for(int i = 0; i < element.getChildCount(); i++) {
			if(element.getType(i) == Node.ELEMENT) {
				Element child = (Element)element.getChild(i);
			
				if(child.getName().equals("id")) {
					this.setID(Integer.parseInt(child.getText(0)));
				} 
				else if(child.getName().compareTo("text") == 0) 
				{
					this.setText(child.getText(0));
				} 
				else if(child.getName().equals("sender_id")) {
					this.setSenderID(Integer.parseInt(child.getText(0)));
				}
				else if(child.getName().equals("recipient_id")) {
					this.setRecipientID(Integer.parseInt(child.getText(0)));
				} 
				else if(child.getName().compareTo("sender_screen_name") == 0) 
				{
					this.setSenderScreenName(child.getText(0));
				} 
				else if(child.getName().compareTo("recipient_screen_name") == 0) 
				{
					this.setRecipientScreenName(child.getText(0));
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
	
	public int getSenderID() {
		return this.senderID;
	}
	
	public void setSenderID(int senderID) {
		this.senderID = senderID;
	}
	
	public int getRecipientID() {
		return this.recipientID;
	}
	
	public void setRecipientID(int recipientID) {
		this.recipientID = recipientID;
	}
	
	public String getSenderScreenName() {
		return this.senderScreenName;
	}
	
	public void setSenderScreenName(String senderScreenName) {
		this.senderScreenName = senderScreenName;
	}
	
	public String getRecipientScreenName() {
		return this.recipientScreenName;
	}
	
	public void setRecipientScreenName(String recipientScreenName) {
		this.recipientScreenName = recipientScreenName;
	}
}