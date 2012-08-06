package frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import request.AcceptFriendRequest;
import request.DeclineFriendRequest;
import request.FriendListRequest;
import request.PendingFriendListRequest;
import request.ReceiveUpdatesRequest;
import request.RemoveFriendRequest;
import request.SendMessageRequest;
import request.SignOutRequest;
import response.Response;
import entity.LocalFriend;
import entity.UpdateHandler;

public class ClientFrame extends JFrame implements ActionListener, MouseListener, WindowListener
{
	private static final long serialVersionUID = 1L;

	private final String server = "uniwiki.dyndns.org";
	private final int port = 22288;
	
	private static final String title = "Chat";
	
	private UpdateHandler updateHandler;
	
	private volatile boolean signedIn;
	private Socket serverSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	//Menu with operations
	private JMenuBar menuBar;
	//File menu
	private JMenu fileMenu;
	private JMenuItem exitMenuItem;
	//Friends menu
	private JMenu friendsMenu;
	private JMenuItem addFriendMenuItem;
	private JMenuItem removeFriendMenuItem;
	//Account menu
	private JMenu accountMenu;
	private JMenuItem signInMenuItem;
	private JMenuItem signOutMenuItem;
	private JMenuItem createAccountMenuItem;

	//Popup menus
	//Pending friend
	private JPopupMenu pendingFriendPopupMenu;
	private JMenuItem acceptPendingFriendMenuItem;
	private JMenuItem declinePendingFriendMenuItem;
	
	//Online friend
	private JPopupMenu onlineFriendPopupMenu;
	private JMenuItem openChatPopupMenuItem;
	private JMenuItem removeOnlineFriendPopupMenuItem;
	
	//Friends list
	private JList friendList;
	private JScrollPane friendListScrollPane;

	//Toolkits
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	
	//Icons
	private ImageIcon onlineIcon = createImageIcon("/online_contact.png", "Online");
	private ImageIcon offlineIcon = createImageIcon("/offline_contact.png", "Offline");
	
	//Frames
	private AddFriendFrame addFriendFrame;
	private CreateAccountFrame createAccountFrame;
	
	public ClientFrame()
	{
		super(title);
		
		menuBar = new JMenuBar();
		//File menu
		fileMenu = new JMenu("File");
		exitMenuItem = new JMenuItem("Exit");
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
		//Friends menu
		friendsMenu = new JMenu("Friends");
		addFriendMenuItem = new JMenuItem("Add friend");
		friendsMenu.add(addFriendMenuItem);
		removeFriendMenuItem = new JMenuItem("Remove friend");
		friendsMenu.add(removeFriendMenuItem);
		friendsMenu.setEnabled(false);
		menuBar.add(friendsMenu);
		//Account menu
		accountMenu = new JMenu("Account");
		signInMenuItem = new JMenuItem("Sign in");
		accountMenu.add(signInMenuItem);
		signOutMenuItem = new JMenuItem("Sign out");
		createAccountMenuItem = new JMenuItem("Create account");
		accountMenu.add(createAccountMenuItem);
		menuBar.add(accountMenu);
		
		menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));		
		this.add(menuBar, BorderLayout.NORTH);
		
		//FriendList settings
		friendList = new JList(new DefaultListModel());
		friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		friendList.setCellRenderer(new FriendListCellRenderer());
		friendListScrollPane = new JScrollPane(friendList);
		
		Border inner = BorderFactory.createEtchedBorder();
		Border outer = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		Border compoundBorder = BorderFactory.createCompoundBorder(outer, inner);
		friendListScrollPane.setBorder(compoundBorder);
		
		this.add(friendListScrollPane, BorderLayout.CENTER);
		
		friendList.addMouseListener(this);
		
		//Pending friend
		pendingFriendPopupMenu = new JPopupMenu();
		acceptPendingFriendMenuItem = new JMenuItem("Accept");
		pendingFriendPopupMenu.add(acceptPendingFriendMenuItem);
		declinePendingFriendMenuItem = new JMenuItem("Decline");
		pendingFriendPopupMenu.add(declinePendingFriendMenuItem);
		
		//Online friend
		onlineFriendPopupMenu = new JPopupMenu();
		openChatPopupMenuItem = new JMenuItem("Open chat");
		removeOnlineFriendPopupMenuItem = new JMenuItem("Remove friend");
		onlineFriendPopupMenu.add(openChatPopupMenuItem);
		onlineFriendPopupMenu.add(removeOnlineFriendPopupMenuItem);
		
		//Action listeners
		exitMenuItem.addActionListener(this);
		addFriendMenuItem.addActionListener(this);
		removeFriendMenuItem.addActionListener(this);
		signInMenuItem.addActionListener(this);
		signOutMenuItem.addActionListener(this);
		createAccountMenuItem.addActionListener(this);
		
		acceptPendingFriendMenuItem.addActionListener(this);
		openChatPopupMenuItem.addActionListener(this);
		removeOnlineFriendPopupMenuItem.addActionListener(this);
		declinePendingFriendMenuItem.addActionListener(this);
		
		this.addWindowListener(this);
		
		this.setSize(300, 600);
		//this.setResizable(false);
		this.setLocation((int)(((toolkit.getScreenSize().getWidth() / 5) - (this.getWidth() / 5)) * 4), (int)(toolkit.getScreenSize().getHeight() / 2) - (this.getHeight() / 2));
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e)
	{		
		Object source = e.getSource();
		
		if(source == openChatPopupMenuItem)
		{
			LocalFriend friend = (LocalFriend)getFriendListModel().get(friendList.getSelectedIndex());
			friend.openChatFrame(this);
		}
	
		if(source == exitMenuItem)
		{
			if(signedIn)
			{
				signOut();
			}
			
			System.exit(0);
		}
		
		if(source == addFriendMenuItem)
		{
			addFriend();
		}
		
		if(source == removeFriendMenuItem|| source == removeOnlineFriendPopupMenuItem)
		{
			removeFriend();
		}
		
		if(source == acceptPendingFriendMenuItem)
		{
			acceptPendingFriend();
		}
		
		if(source == declinePendingFriendMenuItem)
		{
			declinePendingFriend();
		}
		
		if(source == signInMenuItem)
		{
			signIn();
		}
		
		if(source == signOutMenuItem)
		{
			signOut();
		}
		
		if(source == createAccountMenuItem)
		{
			createAccount();
		}
	}
	
	public void sendMessageRequest(long senderId, String message)
	{
		try
		{
			synchronized(out)
			{
				out.writeObject(new SendMessageRequest(senderId, message));
				out.flush();
			}
		}
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
		}
	}
	
	public void displayErrorDialog(Response response)
	{
		JOptionPane.showMessageDialog(this, response.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public synchronized DefaultListModel getFriendListModel()
	{
		return (DefaultListModel)friendList.getModel();
	}
	
	public void setSignedIn(boolean signedIn)
	{
		this.signedIn = signedIn;
	}
	
	public boolean isSignedIn()
	{
		return signedIn;
	}

	private void signIn()
	{
		new SignInFrame(this);
		
		if(signedIn)
		{
			friendsMenu.setEnabled(true);
			accountMenu.remove(signInMenuItem);
			accountMenu.add(signOutMenuItem, 0);
			createAccountMenuItem.setEnabled(false);
			updateHandler = new UpdateHandler(serverSocket, in, this);
			updateHandler.start();
			
			try
			{				
				synchronized(out)
				{
					out.writeObject(new FriendListRequest());
					out.flush();
					
					out.writeObject(new PendingFriendListRequest());
					out.flush();
					
					out.writeObject(new ReceiveUpdatesRequest());
					out.flush();
				}
			}
			catch(IOException ioe)
			{
				System.err.println(ioe.toString());
			}
		}
	}
	
	public void signClientOut()
	{
		this.setSignedIn(false);
		
		this.setTitle(title);
		friendsMenu.setEnabled(false);
		accountMenu.remove(signOutMenuItem);
		accountMenu.add(signInMenuItem, 0);
		createAccountMenuItem.setEnabled(true);
		
		DefaultListModel listModel = (DefaultListModel)friendList.getModel();
		listModel.removeAllElements();
		
		closeObjectStreams();
		closeSocket();
	}
	
	private void signOut()
	{
		synchronized(out)
		{
			try
			{
				out.writeObject(new SignOutRequest());
				out.flush();
			}
			catch(IOException ioe)
			{
				System.out.println(ioe.toString());
			}
		}
	}
	
	public void closeCreateAccountFrame()
	{
		createAccountFrame = null;
	}
	
	public void closeAddFriendFrame()
	{
		addFriendFrame = null;
	}
	
	private void addFriend()
	{
		if(addFriendFrame == null)
		{
			addFriendFrame = new AddFriendFrame(out, this);
		}
		else
		{
			addFriendFrame.toFront();
		}
	}
	
	private void createAccount()
	{
		if(createAccountFrame == null)
		{
			createAccountFrame = new CreateAccountFrame(this);
		}
		else
		{
			createAccountFrame.toFront();
		}
	}
	
	private void removeFriend()
	{
		LocalFriend friend = (LocalFriend)getFriendListModel().get(friendList.getSelectedIndex());
		try
		{				
			synchronized(out)
			{
				out.writeObject(new RemoveFriendRequest(friend.getUserId()));
				out.flush();
			}
		}
	
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
		}
	}
	
	private void acceptPendingFriend()
	{
		LocalFriend friend = (LocalFriend)getFriendListModel().get(friendList.getSelectedIndex());
		try
		{				
			synchronized(out)
			{
				out.writeObject(new AcceptFriendRequest(friend.getUserId()));
				out.flush();
			}
		}
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
		}
	}
	
	private void declinePendingFriend()
	{
		LocalFriend friend = (LocalFriend)getFriendListModel().get(friendList.getSelectedIndex());
		try
		{				
			synchronized(out)
			{
				out.writeObject(new DeclineFriendRequest(friend.getUserId()));
				out.flush();
			}
		}
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
		}
	}
	
	public void refreshListModel()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				DefaultListModel listModel = new DefaultListModel();
				DefaultListModel temp = (DefaultListModel)friendList.getModel();
				
				for(int i = 0; i < temp.size(); i++)
				{
					listModel.addElement(temp.get(i));
				}
				
				friendList.setModel(listModel);
			}
		});
	}

	public class FriendListCellRenderer extends JLabel implements ListCellRenderer
	{
		private static final long serialVersionUID = 1L;

		public FriendListCellRenderer()
		{
		}
		
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			LocalFriend friend = (LocalFriend)value;
			
			this.setOpaque(true);
			this.setText(friend.getFirstName() + " " + friend.getLastName());
			
			if(!friend.getMessageQueue().isEmpty())
			{
				this.setFont(new Font("Arial", Font.BOLD, 14));
			}
			else
			{
				this.setFont(new Font("Arial", Font.PLAIN , 14));
			}
			
			if(isSelected)
			{
				this.setBackground(new Color(200, 200, 200));
			}
			else
			{
				this.setBackground(Color.WHITE);
			}
			
			if(friend.isOnline())
			{
				this.setIcon(onlineIcon);
			}
			else
			{
				this.setIcon(offlineIcon);
			}
			
			if(friend.isPending())
			{
				this.setText(this.getText() + " (Pending)");
			}
			
			return this;
		}
	}
	
	public ImageIcon createImageIcon(String path, String description)
	{
		URL imageURL = getClass().getResource(path);
		
		if(imageURL != null)
		{
			return new ImageIcon(imageURL, description);
		}
		else
		{
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void mouseClicked(MouseEvent e) 
	{	
		if(friendList.getModel().getSize() <= 0)
		{
			return;
		}
		
		if(e.getClickCount() == 2)
		{
			LocalFriend friend = (LocalFriend)getFriendListModel().get(friendList.getSelectedIndex());
			friend.openChatFrame(this);
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e) 
	{
		friendList.setSelectedIndex(friendList.locationToIndex(e.getPoint()));
	}

	public void mouseReleased(MouseEvent e) 
	{
		showPopup(e);
	}
	
	private void showPopup(MouseEvent e)
	{
		if(friendList.getModel().getSize() <= 0)
		{
			return;
		}
		
		LocalFriend friend = (LocalFriend)(getFriendListModel().get(friendList.getSelectedIndex()));
		if(e.isPopupTrigger() && signedIn)
		{
			friendList.setSelectedIndex(friendList.locationToIndex(e.getPoint()));
			if(!friend.isPending())
			{
				onlineFriendPopupMenu.show(friendList, e.getX(), e.getY());
			}
			else
			{
				pendingFriendPopupMenu.show(friendList, e.getX(), e.getY());
			}
		}
	}
	
	public int getPort()
	{
		return port;
	}
	
	public String getServer()
	{
		return server;
	}
	
	public void setSocket(Socket serverSocket)
	{
		this.serverSocket = serverSocket;
	}
	
	public Socket getSocket()
	{
		return serverSocket;
	}
	
	public void setObjectInputStream(ObjectInputStream in)
	{
		this.in = in;
	}
	
	public void setObjectOutputStream(ObjectOutputStream out)
	{
		this.out = out;
	}
	
	public ObjectInputStream getObjectInputStream()
	{
		return in;
	}
	
	public ObjectOutputStream getObjectOutputStream()
	{
		return out;
	}
	
	private void closeObjectStreams()
	{
		try
		{
			if(out != null)
			{
				out.close();
			}
			
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

	public void windowActivated(WindowEvent arg0) 
	{	
	}

	public void windowClosed(WindowEvent arg0) 
	{
	}

	public void windowClosing(WindowEvent arg0) 
	{
	}

	public void windowDeactivated(WindowEvent arg0) 
	{		
	}

	public void windowDeiconified(WindowEvent arg0) 
	{
	}

	public void windowIconified(WindowEvent arg0) 
	{
	}

	public void windowOpened(WindowEvent arg0) 
	{
		signIn();
	}
}
