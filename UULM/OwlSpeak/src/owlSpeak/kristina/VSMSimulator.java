package owlSpeak.kristina;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

public class VSMSimulator extends JFrame implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1901637974111977706L;
	JButton sendButton;
	JTextArea recvField;
	JList<String> sendField;
	
	
	DatagramSocket mSocket ;
	String hostName = "127.0.0.1";
	int portNumber = 4445;
	int portNumberR = 4444;
	

	public VSMSimulator(){
		this.setTitle("VSM Simulator");
		this.setSize(500, 500);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		
		recvField = new JTextArea();
		sendField = new JList<String>();
		sendField.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(sendField);
		
		this.getContentPane().setLayout(new GridLayout(2, 1));
		JPanel tmp = new JPanel();
		tmp.setLayout(new BorderLayout());
		tmp.add(sendButton, BorderLayout.SOUTH);
		tmp.add(scrollPane, BorderLayout.CENTER);
		this.getContentPane().add(recvField);
		this.getContentPane().add(tmp);
		this.pack();
		this.setVisible(true);

		try {
            // Create The Addresses
            SocketAddress mLAddr = new InetSocketAddress(hostName, portNumber);
            // Create The UDP Socket
            mSocket = new DatagramSocket(mLAddr);
            // Connect The UDP Socket

                // Create The Addresses
                SocketAddress mRAddr = new InetSocketAddress(hostName, portNumberR);
                // Connect The UDP Socket
                mSocket.connect(mRAddr);

                send("ready");
        		listenToOWl();
                
        } catch (final Exception exc) {
            System.err.println(exc.getMessage());
        }
		
	}
	
	public static void main(String[] atestrgs){
		new VSMSimulator();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String message = (String)sendField.getSelectedValue();
		send(message);
	}
	
	public void listenToOWl() throws IOException{
		final byte[] buffer = new byte[4096];
        final DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length);
		
		while (mSocket != null && !mSocket.isClosed()) {
			mSocket.receive(packet);
			
			final String message = new String(
                    packet.getData(), 0,
                    packet.getLength(), "UTF-8");
			
		    if (message.equals("bye"))
		        break;
		    else {
		    	String[] msg = message.split("\n");
		    	recvField.setText(msg[0]);
		    	if(msg[1].equals("new Grammar")){
		    		loadGrammar();
		    	}
		    }
		}
	}
	
	public void loadGrammar(){
		LinkedList<String> usrmoves = new LinkedList<String>();
		try {
			
			BufferedReader br = new BufferedReader(new FileReader("C:/OwlSpeak/grammar.xml"));

			String line = "";

			while ((line = br.readLine()) != null )
		    {
		      if(line.contains("out.TAGfunction = \"")){
		    	  line = line.replace("out.TAGfunction = \"", "");
		    	  usrmoves.add(line.split("\"")[0]);
		      }
		    }
		    
			
			br.close();
 
		} catch (IOException e) {
			System.err.println("Dieser Codeabschnitt wurde speziell für das Zusammenspiel von SceneMaker und OWLSpeak geschrieben.\nEine falsche Ordnerstruktur oder ein falsch gesetzter HomePath führen zu Fehlern.");
			e.printStackTrace();
		}
		
		sendField.setListData(usrmoves.toArray(new String[0]));
	}
	
	public void send(String message){
		if (mSocket != null) {
            // Try To Receive New Data
            try {
                // Create The Datagram Buffer
                final byte[] buffer = message.getBytes("UTF-8");
                // Create The Datagram Packet
                final DatagramPacket packet
                        = new DatagramPacket(buffer, buffer.length);
                // Send The Datagram Packet
                mSocket.send(packet);
            } catch (final Exception exc) {
                // Debug Some Information
                System.err.println(exc.getMessage());
            }
        } 
	}
	
}
