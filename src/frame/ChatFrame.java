package frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import entity.LocalFriend;

public class ChatFrame extends JFrame implements ActionListener, KeyListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private ClientFrame chatClient;
	LocalFriend me;
	
	//Private variables for component use
	private JPanel southPanel;
	private JPanel centerPanel;
	private JButton sendButton;
	private JTextArea messageTextArea;
	private JTextArea chatTextArea;
	private JScrollPane chatScrollPane;
	private JScrollPane messageScrollPane;
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private Dimension screenSize = toolkit.getScreenSize();
	
	//String name, long userId, String friendUserName, Socket serverSocket
	
	public ChatFrame(ClientFrame chatClient, LocalFriend me)
	{
		super(me.getFirstName() + " " + me.getLastName());
		setLayout(new BorderLayout());
		
		this.chatClient = chatClient;
		this.me = me;
		
		//Creating panels
		centerPanel = new JPanel();
		centerPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		
		southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout());
		
		//Creating Buttons
		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		sendButton.setPreferredSize(new Dimension(65,40));
		
		//Creating message box to recieve messages from a user
		chatTextArea = new JTextArea(20, 35);
		Font font2 = new Font("Arial", Font.PLAIN, 17);
		chatScrollPane = new JScrollPane(chatTextArea);
		chatTextArea.setFont(font2);
		chatTextArea.setEditable(false);
		chatTextArea.setLineWrap(true);
		chatTextArea.setMargin(new Insets(10,10,10,10));
		
		//Creating the message box to write messages to a user
		messageTextArea = new JTextArea(2,30);
		Font font = new Font("Arial", Font.PLAIN, 17);
		messageScrollPane = new JScrollPane(messageTextArea);
		messageTextArea.setFont(font);
		messageTextArea.setMargin(new Insets(5,5,5,5));
		messageTextArea.setLineWrap(true);
		messageTextArea.addKeyListener(this);
		
		
		Action test = new AbstractAction() 
		{
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) 
		    {
		        String message = messageTextArea.getText();
		        sendMessage(message);
		    }
		};
		
		messageTextArea.getActionMap().put("test", test);
		messageTextArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), test);
		
		//Adding components to panels
		centerPanel.add(chatScrollPane, BorderLayout.CENTER);
		southPanel.add(messageScrollPane);
		southPanel.add(sendButton);
		
		//Adding components to frame
		add(southPanel, BorderLayout.SOUTH);
		add(centerPanel, BorderLayout.CENTER);
		
		this.addWindowListener(this);
		
		//Frame settings
		this.setSize(499, 524);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocation((int)((screenSize.getWidth() / 2) - (this.getWidth() / 2)), (int)((screenSize.getHeight() / 2) - (this.getHeight() / 2)));
		
		messageTextArea.grabFocus();
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		String message = messageTextArea.getText();
		
		if(source == sendButton)
		{
			if(!message.equals(""))
			{
				sendMessage(message);
			}
		}
	}
	
	public void appendMessage(String sender, String message)
	{
		chatTextArea.append(sender + ": " + message + "\n");
	}
	
	public void appendErrorMessage(String message)
	{
		chatTextArea.append("Error: " + message + "\n");
	}

	public void keyPressed(KeyEvent e) 
	{
		if(e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			messageTextArea.append("\n");
		}
	}

	public void keyReleased(KeyEvent e) 
	{	
	}

	public void keyTyped(KeyEvent e) 
	{
	}

	
	
	public void sendMessage(String message)
	{
		chatTextArea.append("Me: " + message + "\n");
		chatClient.sendMessageRequest(me.getUserId(), message);
		messageTextArea.setText("");
		messageTextArea.grabFocus();
	}

	public void windowActivated(WindowEvent e) 
	{
	}

	public void windowClosed(WindowEvent e) 
	{
		me.closeChatFrame();	
	}

	public void windowClosing(WindowEvent e) 
	{
	}

	public void windowDeactivated(WindowEvent e) 
	{
	}

	public void windowDeiconified(WindowEvent e) 
	{
	}

	public void windowIconified(WindowEvent e) 
	{
	}

	public void windowOpened(WindowEvent e) 
	{
		me.emptyMessageQueue();
	}
}
