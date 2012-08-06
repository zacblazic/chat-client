package entity;

import frame.ChatFrame;
import frame.ClientFrame;

public final class LocalFriend
{
	private final long userId;
	private final String username;
	private final String firstName;
	private final String lastName;
	private final String emailAdress;
	private volatile boolean online;
	private volatile boolean pending;
	private final MessageQueue messageQueue;
	private ChatFrame chatFrame;
	
	public LocalFriend(long userId, String username, String firstName, String lastName, String emailAddress, boolean online, boolean pending)
	{
		this.userId = userId;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAdress = emailAddress;
		this.online = online;
		this.pending = pending;
		this.messageQueue = new MessageQueue(1000);
	}
	
	public synchronized void setOnline(boolean online)
	{
		this.online = online;
	}
	
	public synchronized long getUserId()
	{
		return userId;
	}

	public synchronized String getUsername()
	{
		return username;
	}

	public synchronized String getFirstName()
	{
		return firstName;
	}

	public synchronized String getLastName()
	{
		return lastName;
	}

	public synchronized String getEmailAdress()
	{
		return emailAdress;
	}
	
	//Testing synchronization
	public synchronized boolean isOnline()
	{
		return online;
	}
	
	//Testing synchronization
	public synchronized boolean isPending()
	{
		return pending;
	}

	public synchronized MessageQueue getMessageQueue()
	{
		return messageQueue;
	}
	
	public synchronized void closeChatFrame()
	{
		chatFrame = null;
	}
	
	public synchronized void emptyMessageQueue()
	{
		while(!messageQueue.isEmpty())
		{
			appendMessage(messageQueue.takeMessage().getMessage());
		}
	}
	
	public synchronized void openChatFrame(ClientFrame chatClient)
	{
		if(!isChatFrameOpen())
		{
			chatFrame = new ChatFrame(chatClient, this);
		}
		else
		{
			chatFrame.toFront();
		}
	}
	
	public synchronized boolean isChatFrameOpen()
	{
		if(chatFrame != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public synchronized void appendMessage(String message)
	{
		chatFrame.appendMessage(firstName, message);
	}
	
	public synchronized void appendErrorMessage(String message)
	{
		chatFrame.appendErrorMessage(message);
	}
}
