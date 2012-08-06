package entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import response.AcceptFriendResponse;
import response.AddFriendResponse;
import response.DeclineFriendResponse;
import response.FriendListResponse;
import response.PendingFriendListResponse;
import response.SendMessageResponse;
import response.SignOutResponse;
import update.FriendAddedUpdate;
import update.FriendOfflineUpdate;
import update.FriendOnlineUpdate;
import update.FriendRemovedUpdate;
import update.MessageUpdate;
import update.PendingFriendAddedUpdate;
import update.PendingFriendRemovedUpdate;
import client.authenticated.Friend;
import frame.ClientFrame;

public final class UpdateHandler extends Thread
{
	private Socket serverSocket;
	private ObjectInputStream in;
	private ClientFrame clientFrame;
	
	public UpdateHandler(Socket serverSocket, ObjectInputStream in, ClientFrame clientFrame)
	{
		this.serverSocket = serverSocket;
		this.in = in;
		this.clientFrame = clientFrame;
	}
	
	public void run()
	{
		while(clientFrame.isSignedIn())
		{
			try
			{
				Object object = null;
				
				synchronized(in)
				{
					object = in.readObject();
				}
				
				if(object instanceof MessageUpdate)
				{
					boolean found = false;
					
					for(int i = 0; i < clientFrame.getFriendListModel().getSize() && !found; i++)
					{
						if( ((MessageUpdate)object).getSenderUserId() == ((LocalFriend)clientFrame.getFriendListModel().get(i)).getUserId() )
						{
							if(((LocalFriend)clientFrame.getFriendListModel().get(i)).isChatFrameOpen())
							{
								((LocalFriend)clientFrame.getFriendListModel().get(i)).appendMessage(((MessageUpdate)object).getMessage());
							}
							else
							{
								((LocalFriend)clientFrame.getFriendListModel().get(i)).getMessageQueue().putMessage(((MessageUpdate)object));
							}
				
							found = true;
						}
					}
					
					clientFrame.refreshListModel();
				}
				else if(object instanceof SendMessageResponse)
				{
					SendMessageResponse sendMessageResponse = (SendMessageResponse)object;
					
					if(sendMessageResponse.isError())
					{
						boolean found = false;
						
						for(int i = 0; i < clientFrame.getFriendListModel().size() && !found; i++)
						{
							LocalFriend friend = (LocalFriend)clientFrame.getFriendListModel().get(i);
							
							if(friend.getUserId() == sendMessageResponse.getFriendUserId())
							{
								friend.appendErrorMessage(sendMessageResponse.getMessage());
								found = true;
							}
						}
					}
				}
				else if(object instanceof FriendOnlineUpdate)
				{
					boolean found = false;
					
					for(int i = 0; i < clientFrame.getFriendListModel().getSize() && !found; i++)
					{
						if( ((FriendOnlineUpdate)object).getFriendUserId() == ((LocalFriend)clientFrame.getFriendListModel().get(i)).getUserId() )
						{
							((LocalFriend)clientFrame.getFriendListModel().get(i)).setOnline(true);
							found = true;
						}
					}
					
					clientFrame.refreshListModel();
				}
				else if(object instanceof FriendOfflineUpdate)
				{
					boolean found = false;
					
					for(int i = 0; i < clientFrame.getFriendListModel().getSize() && !found; i++)
					{
						if( ((FriendOfflineUpdate)object).getFriendUserId() == ((LocalFriend)clientFrame.getFriendListModel().get(i)).getUserId() )
						{
							((LocalFriend)clientFrame.getFriendListModel().get(i)).setOnline(false);
							found = true;
						}
					}
					
					clientFrame.refreshListModel();
				}
				else if(object instanceof FriendListResponse)
				{
					FriendListResponse friendListResponse = (FriendListResponse)object;
					
					if(!friendListResponse.isError())
					{
						LinkedList<Friend> friendList = friendListResponse.getFriendList();
						
						for(int i = 0; i < friendList.size(); i++)
						{
							long userId = friendList.get(i).getUserId();
							String username = friendList.get(i).getUsername();
							String firstName = friendList.get(i).getFirstName();
							String lastName = friendList.get(i).getLastName();
							String emailAddress = friendList.get(i).getEmailAdress();
							boolean online = friendList.get(i).isOnline();
							boolean pending = friendList.get(i).isPending();
							
							clientFrame.getFriendListModel().addElement(new LocalFriend(userId, username, firstName, lastName, emailAddress, online, pending));
						}
						
						clientFrame.refreshListModel();
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Friend list retrieval failed", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(object instanceof PendingFriendListResponse)
				{
					PendingFriendListResponse pendingFriendListResponse = (PendingFriendListResponse)object;
					
					if(!pendingFriendListResponse.isError())
					{
						LinkedList<Friend> pendingFriendList = pendingFriendListResponse.getPendingFriendList();
						
						for(int i = 0; i < pendingFriendList.size(); i++)
						{
							long userId = pendingFriendList.get(i).getUserId();
							String username = pendingFriendList.get(i).getUsername();
							String firstName = pendingFriendList.get(i).getFirstName();
							String lastName = pendingFriendList.get(i).getLastName();
							String emailAddress = pendingFriendList.get(i).getEmailAdress();
							boolean online = pendingFriendList.get(i).isOnline();
							boolean pending = pendingFriendList.get(i).isPending();
							
							clientFrame.getFriendListModel().addElement(new LocalFriend(userId, username, firstName, lastName, emailAddress, online, pending));
						}
						
						clientFrame.refreshListModel();
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Pending friend list retrieval failed", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(object instanceof SignOutResponse)
				{
					if(!((SignOutResponse)object).isError())
					{
						clientFrame.signClientOut();
					}
					else
					{
						JOptionPane.showMessageDialog(clientFrame, ((SignOutResponse)object).getMessage());
					}
				}
				else if(object instanceof AddFriendResponse)
				{
					AddFriendResponse addFriendResponse = (AddFriendResponse)object;
					
					if(addFriendResponse.isError())
					{
						JOptionPane.showMessageDialog(null, addFriendResponse.getMessage(), "Add friend failed", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(object instanceof AcceptFriendResponse)
				{
					AcceptFriendResponse acceptFriendResponse = (AcceptFriendResponse)object;
					
					if(acceptFriendResponse.isError())
					{
						JOptionPane.showMessageDialog(null, acceptFriendResponse.getMessage(), "Accept friend failed", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(object instanceof DeclineFriendResponse)
				{
					DeclineFriendResponse declineFriendResponse = (DeclineFriendResponse)object;
					
					if(declineFriendResponse.isError())
					{
						JOptionPane.showMessageDialog(null, declineFriendResponse.getMessage(), "Decline friend failed", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(object instanceof FriendAddedUpdate)
				{
					FriendAddedUpdate friendAddedUpdate = (FriendAddedUpdate)object;
					Friend friend = friendAddedUpdate.getFriend();
					
					clientFrame.getFriendListModel().addElement(new LocalFriend(friend.getUserId(), friend.getUsername(), friend.getFirstName(), friend.getLastName(), friend.getEmailAdress(), friend.isOnline(), friend.isPending()));
					clientFrame.refreshListModel();
				}
				else if(object instanceof FriendRemovedUpdate)
				{
					boolean found = false;
					
					FriendRemovedUpdate friendRemovedUpdate = (FriendRemovedUpdate)object;
					
					for(int i = 0; i < clientFrame.getFriendListModel().size() && !found; i++)
					{
						LocalFriend friend = (LocalFriend)clientFrame.getFriendListModel().get(i);
						
						if(friend.getUserId() == friendRemovedUpdate.getFriendUserId())
						{
							clientFrame.getFriendListModel().remove(i);
							found = true;
						}
					}
					
					clientFrame.refreshListModel();
				}
				else if(object instanceof PendingFriendAddedUpdate)
				{
					PendingFriendAddedUpdate pendingFriendAddedUpdate = (PendingFriendAddedUpdate)object;
					Friend friend = pendingFriendAddedUpdate.getFriend();
					
					clientFrame.getFriendListModel().addElement(new LocalFriend(friend.getUserId(), friend.getUsername(), friend.getFirstName(), friend.getLastName(), friend.getEmailAdress(), friend.isOnline(), friend.isPending()));
					clientFrame.refreshListModel();
				}
				else if(object instanceof PendingFriendRemovedUpdate)
				{
					boolean found = false;
					
					PendingFriendRemovedUpdate pendingFriendRemovedUpdate = (PendingFriendRemovedUpdate)object;
					
					for(int i = 0; i < clientFrame.getFriendListModel().size() && !found; i++)
					{
						LocalFriend friend = (LocalFriend)clientFrame.getFriendListModel().get(i);
						
						if(friend.getUserId() == pendingFriendRemovedUpdate.getPendingFriendUserId() && friend.isPending() == true)
						{
							clientFrame.getFriendListModel().remove(i);
							found = true;
						}
					}
					
					clientFrame.refreshListModel();
				}
			}
			catch(IOException ioe)
			{
				System.err.println(ioe);
				clientFrame.signClientOut();
			}
			catch(ClassNotFoundException cnfe)
			{
				System.err.println(cnfe);
				clientFrame.signClientOut();
			}			
		}
		
		//Redundant
		closeObjectStream();
		closeSocket();
	}
	
	private void closeObjectStream()
	{
		try
		{
			if(in != null)
			{
				in.close();
			}
		}
		catch(IOException ioe)
		{
			System.err.println(ioe);
		}
	}
	
	private void closeSocket()
	{
		try
		{
			if(serverSocket != null)
			{
				serverSocket.close();
			}
		}
		catch(IOException ioe)
		{
			System.err.println(ioe);
		}
	}
}