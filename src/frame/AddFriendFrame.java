package frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import request.AddFriendRequest;

public class AddFriendFrame extends JFrame implements ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private ObjectOutputStream out;
	private ClientFrame clientFrame;
	
	private JLabel friendUsernameLabel;
	
	private JTextField friendUsernameTextField;
	
	private JButton addFriendButton;
	private JButton cancelButton;
	
	private JPanel usernamePanel;
	private JPanel buttonPanel;
	
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	private Dimension screenSize = toolkit.getScreenSize();

	public AddFriendFrame(ObjectOutputStream out, ClientFrame clientFrame)
	{
		super("Add friend");
		this.setLayout(new BorderLayout());
		
		JComponent pane = (JComponent)this.getContentPane();
		pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		this.out = out;
		this.clientFrame = clientFrame;
		
		//Creating Panels and setting layouts
		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		//Creating Labels
		friendUsernameLabel = new JLabel("Friend username:");
		friendUsernameLabel.setPreferredSize(new Dimension(120, 25));
		friendUsernameLabel.setMaximumSize(new Dimension(120, 25));
		friendUsernameLabel.setMinimumSize(new Dimension(120, 25));
		
		//Creating TextFields
		friendUsernameTextField = new JTextField(10);
		friendUsernameTextField.setPreferredSize(new Dimension(200, 25));
		friendUsernameTextField.setMaximumSize(new Dimension(200, 25));
		friendUsernameTextField.setMinimumSize(new Dimension(200, 25));
		
		//Creating Buttons
		addFriendButton = new JButton("Add friend");
		cancelButton = new JButton("Cancel");
		
		//Adding components to panels
		usernamePanel.add(friendUsernameLabel);
		usernamePanel.add(friendUsernameTextField);
		
		buttonPanel.add(addFriendButton);
		buttonPanel.add(cancelButton);
		
		this.add(usernamePanel, BorderLayout.NORTH);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		this.addWindowListener(this);
		addFriendButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		this.setSize(358, 152);
		this.setLocation((int)((screenSize.getWidth() / 2) - (this.getWidth() / 2)), (int)((screenSize.getHeight() / 2) - (this.getHeight() / 2)));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		
		if(source == addFriendButton)
		{
			String friendUsername = friendUsernameTextField.getText();
			
			if(!friendUsername.isEmpty())
			{
				try
				{
					synchronized(out)
					{
						out.writeObject(new AddFriendRequest(friendUsername));
					}
				}
				catch(IOException ioe)
				{
					System.err.println(ioe);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Friend username cannot be empty", "Add friend failed", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(source == cancelButton)
		{
			this.dispose();
		}
	}

	public void windowActivated(WindowEvent e) 
	{
	}

	public void windowClosed(WindowEvent e)
	{
		clientFrame.closeAddFriendFrame();
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
	}
}
