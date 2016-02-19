//package com.sms.india;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.io.*;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;
import java.text.*;
import java.util.Date;

import java.io.InputStreamReader;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;


class SerialTest extends Database implements SerialPortEventListener {


	public static String inputLine;
	SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
	};
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	
	
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	/*String path = System.getProperty("java.library.path");
	System.out.println(path);*/
	
static{
System.loadLibrary("rxtxSerial"); 
}
	public void initialize() {
		
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();
				new Database();
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent)  
	{
		Connection con;
		   Statement st;
		    ResultSet rs;
		
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				try{
			        
			        //MAKE SURE YOU KEEP THE mysql_connector.jar file in java/lib folder
			        //ALSO SET THE CLASSPATH
			        Class.forName("com.mysql.jdbc.Driver");
			       
			           
			       }
			    catch (Exception e)
			    {
			        System.out.println(e);
			    }
				
				inputLine=input.readLine();
				frame2.setVisible(true);
				try{
					int status =0;
					Date dNow = new Date();
					SimpleDateFormat ft= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					dateSample.setText(ft.format(dNow));
					
					String values = inputLine;
					String[] parts = values.split("t");
					String part1 = parts[0];
					String part2 = parts[1];
					smokeValue.setText(part1);
					tempValue.setText(part2);
					int smokeNumerical = Integer.parseInt(part1);
					int tempNumerical = Integer.parseInt(part2);
					if(smokeNumerical > 100 && tempNumerical > 100)
					{
						limit.setVisible(true);
						status = 1;
						if(status==1)
						{
							try
							{
								con=DriverManager.getConnection("jdbc:mysql://localhost:3306/FireAlarm","root","krish123");
								st=con.createStatement();
								st.executeUpdate("insert into Detected(Smoke,Temperature,DateAndTime)" + "values('"+ part1 +"','"+ part2 +"','"+dateSample.getText()+"')");
								con.close();

							}
							catch (SQLException e) 
							  {
								// TODO Auto-generated catch block
								e.printStackTrace();
							  }

						}

						new SendMailTLS();
						}
					else
						limit.setVisible(false);
										 
			try{
						
						
						con=DriverManager.getConnection("jdbc:mysql://localhost:3306/FireAlarm","root","krish123");
						st=con.createStatement();
						st.executeUpdate("insert into STValues2(Smoke,Temperature,DateAndTime,Status)" + "values('"+ smokeValue.getText() +"','"+ tempValue.getText() +"','"+dateSample.getText()+"','"+status+"')");
						con.close();

			    		}
			    	catch (SQLException e) 
					  {
						// TODO Auto-generated catch block
						e.printStackTrace();
					  }


				}
				catch(NullPointerException e)
				{
					e.printStackTrace();
				}
				//System.out.println(inputLine);
			//	o.setLText();
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	public static void main (String[] args) throws Exception, SQLException, ClassNotFoundException {
	
		Thread.sleep(11000);
		SerialTest main = new SerialTest();
		main.initialize();
		Thread t=new Thread() {
			public void run() {
				//the following line will keep this app alive for 1000 seconds,
				//waiting for events to occur and responding to them (printing incoming messages to console).
			
				try 
				{
					Thread.sleep(100000); 
					
				} catch (InterruptedException ie) {}
			
			}
		};
		//t.start();
		//System.out.println("Started");
		
	}

	}


/*
class Wireless extends JFrame
{
	
	
	String checkUname1,checkPassword1;
	JFrame frame1 = new JFrame("Login");
	JPanel panel1 = new JPanel();
	JButton login = new JButton("Login");
	JButton clear = new JButton("Clear");
	JTextField usernameTextBox = new JTextField();
	JPasswordField passwordTextBox = new JPasswordField();
	JLabel usernameLabel = new JLabel("Username :");
	JLabel passwordLabel = new JLabel("Password :");
	
	JLabel titleLabel = new JLabel("Wireless Security Alarm system for Smoke detection and Temperature Monitoring");
	public Wireless()
	{
		titleLabel.setFont(new Font("Courier New",Font.BOLD,24));
		usernameLabel.setFont(new Font("Courier New",Font.BOLD,20));
		passwordLabel.setFont(new Font("Courier New",Font.BOLD,20));
		usernameTextBox.setFont(new Font("Courier New",Font.BOLD,20));
		passwordTextBox.setFont(new Font("Courier New",Font.BOLD,20));
		panel1.setBackground(Color.black);
		titleLabel.setForeground(Color.white);
		usernameLabel.setForeground(Color.white);
		passwordLabel.setForeground(Color.white);
    		
    		
 		
		//panel1.add(image);
		panel1.add(titleLabel);
		panel1.add(usernameLabel);
		panel1.add(usernameTextBox);
		panel1.add(passwordLabel);
		panel1.add(passwordTextBox);
		panel1.add(login);
		panel1.add(clear);
		//panel1.add(login);
		//panel1.add(clear);
		
		

	pack();

		panel1.getComponent(0).setBounds(60,20,1100,25);
		panel1.getComponent(1).setBounds(150,150,150,25);
		panel1.getComponent(2).setBounds(300,150,250,30);
		panel1.getComponent(3).setBounds(150,225,150,25);   //75 difference in height of 2 components
		panel1.getComponent(4).setBounds(300,225,250,30);
		panel1.getComponent(5).setBounds(350,300,100,50);
		panel1.getComponent(6).setBounds(550,300,100,50);
		
		

		
		
				
		login.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				String gotUname, gotPassword;
				gotUname = usernameTextBox.getText();
				gotPassword = passwordTextBox.getText();
				checkUname1 = "ck2";	
				checkPassword1 = "united";
				if(gotUname.equals("") || gotPassword.equals(""))
					JOptionPane.showMessageDialog(null,"Enter Username and Password","Inane warning",JOptionPane.WARNING_MESSAGE);
				else if(gotUname.equals(checkUname1) && gotPassword.equals(checkPassword1))
				{
					JOptionPane.showMessageDialog(null,"Login Successful!!!!");
					usernameTextBox.setText("");
					passwordTextBox.setText("");
					frame1.dispose();
					new SerialTest();
				}
				else
					JOptionPane.showMessageDialog(null,"Incorrect username or password","Inane warning",JOptionPane.WARNING_MESSAGE);	
				
			}
		});
		clear.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae)
			{
				usernameTextBox.setText("");
				passwordTextBox.setText("");
				JOptionPane.showMessageDialog(null,"Textboxes cleared","Inane warning",JOptionPane.WARNING_MESSAGE);	
			}
		});
		panel1.setLayout(null);
		panel1.setBounds(0, 0,1160,500);
		frame1.add(panel1,null);

		frame1.setTitle("Login");
		frame1.setSize(1160,500);
		frame1.setVisible(true);
		frame1.setLocationRelativeTo(null);
    	frame1.setDefaultCloseOperation(EXIT_ON_CLOSE);
    	
	}		
	

}

*/
class Database extends JFrame 
{
	JFrame frame2 = new JFrame("Value Analysis");
	JLabel smoke = new JLabel("Smoke :");
	JLabel temperature = new JLabel("Temperature :");
	JLabel limit = new JLabel("Overlimit!!!!!!!!!!!");
	JLabel title = new JLabel("Value Analysis");
	JLabel smokeValue = new JLabel();	
	JLabel tempValue = new JLabel();
	JLabel dateSample = new JLabel();	
	
	JPanel panel2 = new JPanel();
	JButton detected = new JButton("Detected Time and date");
	
	//Timer timer=null;
	Database()
	{
		    
		try{
	        
	        //MAKE SURE YOU KEEP THE mysql_connector.jar file in java/lib folder
	        //ALSO SET THE CLASSPATH
			Class.forName("com.mysql.jdbc.Driver");
	       
	           
	       }
	       catch (Exception e)
	       {
	    	   System.out.println(e);
	       }
		title.setFont(new Font("Courier New",Font.BOLD,24));
		smoke.setFont(new Font("Courier New",Font.BOLD,20));
		temperature.setFont(new Font("Courier New",Font.BOLD,20));
		limit.setFont(new Font("Courier New",Font.BOLD,20));
		limit.setVisible(false);
		dateSample.setFont(new Font("Courier New",Font.BOLD,20));
		smokeValue.setFont(new Font("Courier New",Font.BOLD,20));
		
		tempValue.setFont(new Font("Courier New",Font.BOLD,24));
		
		panel2.setBackground(Color.black);
		title.setForeground(Color.white);
		smoke.setForeground(Color.white);
		temperature.setForeground(Color.white);	
		limit.setForeground(Color.red);
		smokeValue.setForeground(Color.yellow);
		tempValue.setForeground(Color.yellow);	
		dateSample.setForeground(Color.cyan);
		
		panel2.add(title);
		panel2.add(smoke);
		panel2.add(smokeValue);
		panel2.add(temperature);
		panel2.add(tempValue);
		panel2.add(limit);
		panel2.add(dateSample);
		//panel2.add(detected);
		pack();
		
		panel2.getComponent(0).setBounds(500,20,1100,25);
		panel2.getComponent(1).setBounds(150,150,150,25);
		panel2.getComponent(2).setBounds(300,150,250,30);
		panel2.getComponent(3).setBounds(150,225,250,25);   //75 difference in height of 2 components
		panel2.getComponent(4).setBounds(410,225,250,30);
		panel2.getComponent(5).setBounds(350,300,300,50);
		panel2.getComponent(6).setBounds(550,80,400,30);
		//panel2.getComponent(7).setBounds(750,400,200,25);
		panel2.setLayout(null);

		panel2.setBounds(0, 0,1160,500);
		frame2.add(panel2,null);

		frame2.setTitle("Value Analysis");
		frame2.setSize(1160,500);
		frame2.setVisible(false);
		frame2.setLocationRelativeTo(null);
    	frame2.setDefaultCloseOperation(EXIT_ON_CLOSE);
    		}    	
	
}

//File Name SendEmail.java


class SendMailTLS {
	 
	SendMailTLS()
	{
 
		final String username = "krish.bhanushali13@gmail.com";
		final String password = "ilovemaself";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("krish.bhanushali13@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("krish.bhanushali2405@gmail.com"));
			message.setSubject("Smoke and Temperature above limit");
			message.setText("Dear Owner, your company is at Fire!! Please come soon!!,"
				+ "\n\n No spam to my email, please!");
 
			Transport.send(message);
 
			System.out.println("Done");
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}


