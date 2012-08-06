package frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import request.SignInRequest;
import response.Response;
import response.SignInResponse;

public class SignInFrame extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;

	//Server information
	private final String server;
	private final int port;
	
	private Socket serverSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private ClientFrame clientFrame;
	
	//Frame components
	private JPanel inputPanel;
	
	private JPanel usernamePanel;
	private JLabel usernameLabel;
	private JTextField usernameTextField;
	
	private JPanel passwordPanel;
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	
	private JPanel buttonPanel;
	private JButton signInButton;
	private JButton cancelButton;
	
	//Toolkit
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	
	public SignInFrame(ClientFrame clientFrame)
	{		
		super(clientFrame, "Sign in", true);
		
		this.clientFrame = clientFrame;
		this.server = clientFrame.getServer();
		this.port = clientFrame.getPort();

		inputPanel = new JPanel(new GridLayout(2, 1));
		
		usernamePanel = new JPanel(new FlowLayout());
		usernameLabel = new JLabel("Username:");
		usernameLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		usernamePanel.add(usernameLabel);
		usernameTextField = new JTextField(30);
		usernameTextField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		usernamePanel.add(usernameTextField);
		
		passwordPanel = new JPanel(new FlowLayout());
		passwordLabel = new JLabel("Password:");
		passwordLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		passwordPanel.add(passwordLabel);
		passwordField = new JPasswordField(30);
		passwordField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		passwordPanel.add(passwordField);
		
		inputPanel.add(usernamePanel);
		inputPanel.add(passwordPanel);
		inputPanel.setBorder(BorderFactory.createEmptyBorder(8, 4, 4, 4));
		this.add(inputPanel, BorderLayout.CENTER);
		
		buttonPanel = new JPanel(new GridLayout(1, 8));
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		
		signInButton = new JButton("Sign in");
		signInButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		buttonPanel.add(signInButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		buttonPanel.add(cancelButton);

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		signInButton.addActionListener(this);
		cancelButton.addActionListener(this);

		this.setSize(450, 150);
		this.setLocation((int)(toolkit.getScreenSize().getWidth() / 2) - (this.getWidth() / 2), (int)(toolkit.getScreenSize().getHeight() / 2) - (this.getHeight() / 2));
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if(source == signInButton)
		{
			if(!usernameTextField.getText().isEmpty())
			{
				createSocket();
				createObjectStreams();
				
				if(out == null || in == null)
				{
					return;
				}
					
				try
				{
					synchronized(out)
					{
						out.writeObject(new SignInRequest(usernameTextField.getText(), new String(passwordField.getPassword())));
						out.flush();
					}
					
					Response response = null;

					synchronized(in)
					{
						response = (Response)in.readObject();
					}

					if(response instanceof SignInResponse)
					{
						SignInResponse signInResponse = (SignInResponse)response;
						
						if(!signInResponse.isError())
						{
							clientFrame.setTitle(clientFrame.getTitle() + " - " + signInResponse.getUsername());
							clientFrame.setSocket(serverSocket);
							clientFrame.setObjectInputStream(in);
							clientFrame.setObjectOutputStream(out);
							clientFrame.setSignedIn(true);
							this.dispose();
						}
						else
						{
							JOptionPane.showMessageDialog(this, signInResponse.getMessage(), "Sign in failed", JOptionPane.ERROR_MESSAGE);
							closeObjectStreams();
							closeSocket();
						}
					}
					else
					{
						JOptionPane.showMessageDialog(this, "An unkown error occured", "Sign in failed", JOptionPane.ERROR_MESSAGE);
						closeObjectStreams();
						closeSocket();
					}
				}
				catch(ClassNotFoundException cnfe)
				{
					System.err.println(cnfe);
				}
				catch(IOException ioe)
				{
					System.err.println(ioe);
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Username field cannot be empty", "Sign in failed", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(source == cancelButton)
		{
			this.dispose();
		}
	}
	
	private void createSocket()
	{
		try
		{
			serverSocket = new Socket(server, port);
		}
		catch(ConnectException ce)
		{
			JOptionPane.showMessageDialog(this, "The connection timed out", "Sign in failed", JOptionPane.ERROR_MESSAGE);
		}
		catch(UnknownHostException uhe)
		{
			System.err.println(uhe.toString());
		}
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
		}
	}
	
	private void createObjectStreams()
	{
		try
		{
			if(serverSocket != null)
			{
				out = new ObjectOutputStream(serverSocket.getOutputStream());
				in = new ObjectInputStream(serverSocket.getInputStream());
			}
		}
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
		}
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
			System.err.println(ioe.toString());
		}
	}
}
