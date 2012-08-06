package frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import request.CreateAccountRequest;
import response.CreateAccountResponse;
import response.Response;

public final class CreateAccountFrame extends JFrame implements ActionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private final String server;
	private final int port;
	private final ClientFrame clientFrame;
	private Socket serverSocket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	//Panels
	private JPanel firstNamePanel;
	private JPanel lastNamePanel;
	private JPanel usernamePanel;
	private JPanel emailAddressPanel;
	private JPanel passwordPanel;
	private JPanel confirmPasswordPanel;
	private JPanel buttonPanel;
	private JPanel firstNameCheckPanel;
	private JPanel lastNameCheckPanel;
	private JPanel usernameCheckPanel;
	private JPanel emailAddressCheckPanel;
	private JPanel passwordCheckPanel;
	private JPanel confirmPasswordCheckPanel;
	
	//Text fields
	private JTextField firstNameTextField;
	private JTextField lastNameTextField;
	private JTextField usernameTextField;
	private JTextField emailAddressTextField;
	
	//Password fields
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	
	//Labels
	private JLabel firstNameLabel;
	private JLabel lastNameLabel;
	private JLabel usernameLabel;
	private JLabel emailAddressLabel;
	private JLabel passwordLabel;
	private JLabel confirmPasswordLabel;
	private JLabel firstNameCheckLabel;
	private JLabel lastNameCheckLabel;
	private JLabel usernameCheckLabel;
	private JLabel emailAddressCheckLabel;
	private JLabel passwordCheckLabel;
	private JLabel confirmPasswordCheckLabel;
	
	//Buttons
	private JButton createAccountButton;
	private JButton cancelButton;
	
	//Toolkit
	private Toolkit toolkit = Toolkit.getDefaultToolkit();
	
	//Screen size
	private Dimension screenSize = toolkit.getScreenSize();
	
	public CreateAccountFrame(ClientFrame clientFrame)
	{
		super("Create Account");
		
		JComponent pane = (JComponent)this.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		this.clientFrame = clientFrame;
		this.server = clientFrame.getServer();
		this.port = clientFrame.getPort();
		
		//Creating panels
		firstNamePanel = new JPanel();
		firstNamePanel.setLayout(new BoxLayout(firstNamePanel, BoxLayout.X_AXIS));
		
		firstNameCheckPanel = new JPanel();
		firstNameCheckPanel.setLayout(new FlowLayout());
		
		lastNamePanel = new JPanel();
		lastNamePanel.setLayout(new BoxLayout(lastNamePanel, BoxLayout.X_AXIS));
		
		lastNameCheckPanel = new JPanel();
		lastNameCheckPanel.setLayout(new FlowLayout());
		
		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
		
		usernameCheckPanel = new JPanel();
		usernameCheckPanel.setLayout(new FlowLayout());
		
		emailAddressPanel = new JPanel();
		emailAddressPanel.setLayout(new BoxLayout(emailAddressPanel, BoxLayout.X_AXIS));
		
		emailAddressCheckPanel = new JPanel();
		emailAddressCheckPanel.setLayout(new FlowLayout());
		
		passwordPanel = new JPanel();
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		
		passwordCheckPanel = new JPanel();
		passwordCheckPanel.setLayout(new FlowLayout());
		
		confirmPasswordPanel = new JPanel();
		confirmPasswordPanel.setLayout(new BoxLayout(confirmPasswordPanel, BoxLayout.X_AXIS));
		
		confirmPasswordCheckPanel = new JPanel();
		confirmPasswordCheckPanel.setLayout(new FlowLayout());
	
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		//Creating JLabels
		firstNameLabel = new JLabel("First name:");
		firstNameLabel.setPreferredSize(new Dimension(120, 24));
		firstNameLabel.setMaximumSize(new Dimension(120, 24));
		firstNameLabel.setMinimumSize(new Dimension(120, 24));
		
		lastNameLabel = new JLabel("Last name:");
		lastNameLabel.setPreferredSize(new Dimension(120, 24));
		lastNameLabel.setMaximumSize(new Dimension(120, 24));
		lastNameLabel.setMinimumSize(new Dimension(120, 24));
		
		usernameLabel = new JLabel("Username:");
		usernameLabel.setPreferredSize(new Dimension(120, 24));
		usernameLabel.setMaximumSize(new Dimension(120, 24));
		usernameLabel.setMinimumSize(new Dimension(120, 24));
		
		emailAddressLabel = new JLabel("Email address:");
		emailAddressLabel.setPreferredSize(new Dimension(120, 24));
		emailAddressLabel.setMaximumSize(new Dimension(120, 24));
		emailAddressLabel.setMinimumSize(new Dimension(120, 24));
		
		passwordLabel = new JLabel("Password:");
		passwordLabel.setPreferredSize(new Dimension(120, 24));
		passwordLabel.setMaximumSize(new Dimension(120, 24));
		passwordLabel.setMinimumSize(new Dimension(120, 24));
		
		confirmPasswordLabel = new JLabel("Confirm password:");
		confirmPasswordLabel.setPreferredSize(new Dimension(120, 24));
		confirmPasswordLabel.setMaximumSize(new Dimension(120, 24));
		confirmPasswordLabel.setMinimumSize(new Dimension(120, 24));
		
		firstNameCheckLabel = new JLabel();
		firstNameCheckLabel.setPreferredSize(new Dimension(310, 24));
		firstNameCheckLabel.setMaximumSize(new Dimension(310, 24));
		firstNameCheckLabel.setMinimumSize(new Dimension(310, 24));
		firstNameCheckLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lastNameCheckLabel = new JLabel();
		lastNameCheckLabel.setPreferredSize(new Dimension(310, 24));
		lastNameCheckLabel.setMaximumSize(new Dimension(310, 24));
		lastNameCheckLabel.setMinimumSize(new Dimension(310, 24));
		lastNameCheckLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		usernameCheckLabel = new JLabel();
		usernameCheckLabel.setPreferredSize(new Dimension(310, 24));
		usernameCheckLabel.setMaximumSize(new Dimension(310, 24));
		usernameCheckLabel.setMinimumSize(new Dimension(310, 24));
		usernameCheckLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		emailAddressCheckLabel = new JLabel();
		emailAddressCheckLabel.setPreferredSize(new Dimension(310, 24));
		emailAddressCheckLabel.setMaximumSize(new Dimension(310, 24));
		emailAddressCheckLabel.setMinimumSize(new Dimension(310, 24));
		emailAddressCheckLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		passwordCheckLabel = new JLabel();
		passwordCheckLabel.setPreferredSize(new Dimension(310, 24));
		passwordCheckLabel.setMaximumSize(new Dimension(310, 24));
		passwordCheckLabel.setMinimumSize(new Dimension(310, 24));
		passwordCheckLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		confirmPasswordCheckLabel = new JLabel();
		confirmPasswordCheckLabel.setPreferredSize(new Dimension(310, 24));
		confirmPasswordCheckLabel.setMaximumSize(new Dimension(310, 24));
		confirmPasswordCheckLabel.setMinimumSize(new Dimension(310, 24));
		confirmPasswordCheckLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		//Creating TextFields
		firstNameTextField = new JTextField(10);
		firstNameTextField.setPreferredSize(new Dimension(200, 24));
		firstNameTextField.setMaximumSize(new Dimension(200, 24));
		firstNameTextField.setMinimumSize(new Dimension(200, 24));
		
		lastNameTextField = new JTextField(18);
		lastNameTextField.setPreferredSize(new Dimension(200, 24));
		lastNameTextField.setMaximumSize(new Dimension(200, 24));
		lastNameTextField.setMinimumSize(new Dimension(200, 24));
		
		usernameTextField = new JTextField(10);
		usernameTextField.setPreferredSize(new Dimension(200, 24));
		usernameTextField.setMaximumSize(new Dimension(200, 24));
		usernameTextField.setMinimumSize(new Dimension(200, 24));
		
		emailAddressTextField = new JTextField(10);
		emailAddressTextField.setPreferredSize(new Dimension(200, 24));
		emailAddressTextField.setMaximumSize(new Dimension(200, 24));
		emailAddressTextField.setMinimumSize(new Dimension(200, 24));
		
		passwordField = new JPasswordField(10);
		passwordField.setPreferredSize(new Dimension(200, 24));
		passwordField.setMaximumSize(new Dimension(200, 24));
		passwordField.setMinimumSize(new Dimension(200, 24));
		
		confirmPasswordField = new JPasswordField(10);
		confirmPasswordField.setPreferredSize(new Dimension(200, 24));
		confirmPasswordField.setMaximumSize(new Dimension(200, 24));
		confirmPasswordField.setMinimumSize(new Dimension(200, 24));
		
		//Creating Buttons
		createAccountButton = new JButton("Create account");
		cancelButton = new JButton("Cancel");
		
		//Adding created components to panels
		firstNamePanel.add(firstNameLabel);
		firstNamePanel.add(firstNameTextField);
		
		lastNamePanel.add(lastNameLabel);
		lastNamePanel.add(lastNameTextField);
		
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameTextField);
		
		emailAddressPanel.add(emailAddressLabel);
		emailAddressPanel.add(emailAddressTextField);
		
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);
		
		confirmPasswordPanel.add(confirmPasswordLabel);
		confirmPasswordPanel.add(confirmPasswordField);
		
		firstNameCheckPanel.add(firstNameCheckLabel);
		lastNameCheckPanel.add(lastNameCheckLabel);
		usernameCheckPanel.add(usernameCheckLabel);
		emailAddressCheckPanel.add(emailAddressCheckLabel);
		passwordCheckPanel.add(passwordCheckLabel);
		confirmPasswordCheckPanel.add(confirmPasswordCheckLabel);
		
		buttonPanel.add(createAccountButton);
		buttonPanel.add(cancelButton);
		
		this.add(firstNamePanel);
		this.add(firstNameCheckPanel);
		
		this.add(lastNamePanel);
		this.add(lastNameCheckPanel);
		
		this.add(usernamePanel);
		this.add(usernameCheckPanel);
		
		this.add(emailAddressPanel);
		this.add(emailAddressCheckPanel);
		
		this.add(passwordPanel);
		this.add(passwordCheckPanel);
		
		this.add(confirmPasswordPanel);
		this.add(confirmPasswordCheckPanel);
		
		this.add(buttonPanel);

		createAccountButton.addActionListener(this);
		cancelButton.addActionListener(this);
		this.addWindowListener(this);
		
		//Frame settings
		this.setSize(359, 440);
		this.setLocation((int)((screenSize.getWidth() / 2) - (this.getWidth() / 2)), (int)((screenSize.getHeight() / 2) - (this.getHeight() / 2)));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		
		if(source == createAccountButton)
		{
			if(isValidInput())
			{
				createAccount();
			}
		}
		else if(source == cancelButton)
		{
			this.dispose();
		}
	}
	
	private boolean isValidInput()
	{
		firstNameCheckLabel.setForeground(Color.BLACK);
		firstNameCheckLabel.setText("");
		lastNameCheckLabel.setForeground(Color.BLACK);
		lastNameCheckLabel.setText("");
		usernameCheckLabel.setForeground(Color.BLACK);
		usernameCheckLabel.setText("");
		emailAddressCheckLabel.setForeground(Color.BLACK);
		emailAddressCheckLabel.setText("");
		passwordCheckLabel.setForeground(Color.BLACK);
		passwordCheckLabel.setText("");
		confirmPasswordCheckLabel.setForeground(Color.BLACK);
		confirmPasswordCheckLabel.setText("");
		
		String firstName = firstNameTextField.getText();
		String lastName = lastNameTextField.getText();
		String emailAddress = emailAddressTextField.getText();
		String username = usernameTextField.getText();
		String password = new String(passwordField.getPassword());
		String confirmPassword = new String(confirmPasswordField.getPassword());
		
		boolean firstNameValid = true;
		boolean lastNameValid = true;
		boolean emailAddressValid = false;
		boolean usernameValid = true;
		boolean passwordValid = true;
		
		//Validate firstName
		if(firstName.length() <= 0 || firstName.length() > 20)
		{
			firstNameValid = false;
			firstNameCheckLabel.setText("Required field cannot be left blank");
			firstNameCheckLabel.setForeground(Color.RED);
		}
		
		for(int i = 0; i < firstName.length() && firstNameValid; i++)
		{
			if(!(Character.isLetter(firstName.charAt(i))))
			{
				firstNameValid = false;
				firstNameCheckLabel.setText("Unsuitable character: " + firstName.charAt(i));
				firstNameCheckLabel.setForeground(Color.RED);
			}
		}
		
		//Validate lastName
		if(lastName.length() <= 0 || lastName.length() > 20)
		{
			lastNameValid = false;
			lastNameCheckLabel.setText("Required field cannot be left blank");
			lastNameCheckLabel.setForeground(Color.RED);
		}
		
		for(int i = 0; i < lastName.length() && lastNameValid; i++)
		{
			if(!(Character.isLetter(lastName.charAt(i))))
			{
				lastNameValid = false;
				lastNameCheckLabel.setText("Unsuitable character: " + lastName.charAt(i));
				lastNameCheckLabel.setForeground(Color.RED);
			}
		}

		//Validate email
		if((emailAddress.indexOf('@') != -1) && (emailAddress.indexOf('.') != -1))
		{
			emailAddressValid = true;
		}
		else
		{
			emailAddressCheckLabel.setText("Email not valid");
			emailAddressCheckLabel.setForeground(Color.RED);
		}
		
		if(emailAddress.isEmpty() == true)
		{
			emailAddressCheckLabel.setText("Required field cannot be left blank");
			emailAddressCheckLabel.setForeground(Color.RED);
		}

		//Validate username
		if(username.length() <= 0)
		{
			usernameValid = false;
			usernameCheckLabel.setText("Required field cannot be left blank");
			usernameCheckLabel.setForeground(Color.RED);
		}
		else if(username.length() < 6 || username.length() > 12)
		{
			usernameValid = false;
			usernameCheckLabel.setText("Invalid username (Length 6-12)");
			usernameCheckLabel.setForeground(Color.RED);
		}
		
		//Validate password
		if(password.length() <= 0)
		{
			passwordValid = false;
			passwordCheckLabel.setText("Required field cannot be left blank");
			passwordCheckLabel.setForeground(Color.RED);
		}
		else if(password.length() < 6 || password.length() > 12)
		{
			passwordValid = false;
			passwordCheckLabel.setText("Invalid password (Length 6-12)");
			passwordCheckLabel.setForeground(Color.RED);
		}
		
		if(!password.equals(confirmPassword))
		{
			passwordValid = false;
			confirmPasswordCheckLabel.setText("Password does not match");
			confirmPasswordCheckLabel.setForeground(Color.RED);
		}
		
		return (firstNameValid && lastNameValid && emailAddressValid && usernameValid && passwordValid);
	}

	private void createAccount() 
	{		
		String firstName = firstNameTextField.getText();
		String lastName = lastNameTextField.getText();
		String emailAddress = emailAddressTextField.getText();
		String username = usernameTextField.getText();
		String password = String.valueOf(passwordField.getPassword());

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
				out.writeObject(new CreateAccountRequest(firstName, lastName, emailAddress, username, password));
				out.flush();
			}
			
			Response response = null;

			synchronized(in)
			{
				response = (Response)in.readObject();
			}
			
			if(response instanceof CreateAccountResponse)
			{
				CreateAccountResponse createAccountResponse = (CreateAccountResponse)response;

				if(createAccountResponse.isError())
				{
					JOptionPane.showMessageDialog(this, createAccountResponse.getMessage(), "Create account failed", JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(this, "Account created", "Success", JOptionPane.INFORMATION_MESSAGE);
					this.dispose();
				}
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
		finally
		{
			closeObjectStreams();
			closeSocket();
		}
	}

	public void windowActivated(WindowEvent e) 
	{
	}
	
	public void windowClosed(WindowEvent e) 
	{
		clientFrame.closeCreateAccountFrame();
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
	
	private void createSocket()
	{
		try
		{
			serverSocket = new Socket(server, port);
		}
		catch(IOException ioe)
		{
			System.err.println(ioe);
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
			System.err.println(ioe);
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
			System.err.println(ioe);
		}
	}
}
