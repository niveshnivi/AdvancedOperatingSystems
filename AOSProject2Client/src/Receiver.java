import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Receiver {
	Node node;
	int port;
	ServerSocket receiver;
	boolean listen = true;
	static int notifyCount = 0;
	static int completeCount = 0;
	static int totalReceivedMessage = 0;
	static boolean[] Quorum = new boolean[8];
	
	
	
	
	public Receiver(Node node,int port) throws IOException
	{
		synchronized(this)
		{
		System.out.println("Receiver Thread " + Thread.currentThread());
		this.node = node;
		this.port = port;
		receiver = new ServerSocket(port);
		Listen();
		}
	}
	private void Listen() throws IOException {
		
		while(true)
		{
		System.out.println("Listening on port " + receiver.getLocalPort() + "...");
		Socket server = receiver.accept();
		DataInputStream in = new DataInputStream(server.getInputStream());
        Process(in.readUTF());
        server.close();
		}
	}

	private void Process(String Message) throws IOException {
		//requestTable.put(arg0, arg1)
		IncTotalReceivedMessage();
//		RequestSender.IncExchanged();
 		System.out.println(Message);
		String type = Message.substring(0, 3);
		int nodeNumber;
		Long time;
		if(type.equals("gra"))
		{
		nodeNumber = Character.getNumericValue(Message.charAt(17));
		updateQuorum(nodeNumber);
		}
		else if(type.equals("sta"))
		{
			FileIO.write("Setting node to ready");
			Controller.setNodeState("ready");
		}
		else if(type.equals("not"))
		{
			System.out.println("incrementing notifycount");
			IncrementNotify();
		}
		else if(type.equals("fin"))
		{
			System.exit(0);
		}
	}
        
        
    private synchronized void updateQuorum(int nodeNumber) {
		// TODO Auto-generated method stub
		Quorum[nodeNumber] = true;
	}
    
    public static synchronized boolean[] getQuorum()
    {
    	return Quorum;
    }
    
	private void IncrementComplete() {
		// TODO Auto-generated method stub
		completeCount++;
	}
    public static synchronized int getCompleteCount()
    {
    	return completeCount;
    }
	private void IncrementNotify() {
		// TODO Auto-generated method stub
		notifyCount++;
                System.out.println("Receiver notify Count at node 0 is" + getNotifyCount());
	}
     
     public synchronized static int getNotifyCount()
    {
    	 return notifyCount;
     }
      
	
	private static synchronized void IncTotalReceivedMessage()
	{
		totalReceivedMessage++;
	}

	public static synchronized int getTotalReceivedMessage()
	{
		return totalReceivedMessage;
	}

	

	public static synchronized void clearQuorum() {
		// TODO Auto-generated method stub
		for(int i = 0;i < Quorum.length;i++)
		{
			Quorum[i] = false;
		}
	}
		
	}


