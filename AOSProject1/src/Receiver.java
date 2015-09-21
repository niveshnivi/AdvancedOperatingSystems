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
	static ConcurrentHashMap<Integer,Long> requestTable = new ConcurrentHashMap<Integer,Long>();
	static int replyCount = 0;
	static int notifyCount = 0;
	static int completeCount = 0;
	static ArrayList<Integer> permissionList = new ArrayList<Integer> ();
	
	static boolean flag;
	
	
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

		if(Controller.getNodePhase().equals("Phase1"))
		{
              Process(in.readUTF());
              server.close();
		}
		else 
		{
		Process2(in.readUTF());
	    server.close();
		}
		
		}
		}
	private void Process2(String Message) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		System.out.println(Message);
		String type = Message.substring(0, 3);
		int nodeNumber;
		Long time;
		
		if(type.equals("req"))
		{	
			nodeNumber = Character.getNumericValue(Message.charAt(18));
			time = Long.parseLong(Message.substring(23,36));
			if(time <= RequestSender.getSelfTime())
			{
			RequestSender.sendReply(nodeNumber);
			removePermission(nodeNumber);
			}
			else
			{
			updateRequestTable(nodeNumber,time);
			}
		}
		
		else if(type.equals("rep"))
		{
			nodeNumber = Character.getNumericValue(Message.charAt(9));
			addPermission(nodeNumber);
			setReplyCount2();
		}
		else if(type.equals("Com"))
		{
			if(node.getNodeNumber() == 0)
			{
			IncrementComplete();
			}
			System.out.println("received complete notification " + Controller.getNodePhase() + Receiver.getCompleteCount());
		}
	}
	private void setReplyCount2() {
		// TODO Auto-generated method stub
		
	}
	private void addPermission(int nodeNumber) {
		// TODO Auto-generated method stub
		permissionList.add(nodeNumber);
		Collections.sort(permissionList);
	}
	private void Process(String Message) throws IOException {
		// TODO Auto-generated method stub
		//requestTable.put(arg0, arg1)
		System.out.println(Message);
		String type = Message.substring(0, 3);
		int nodeNumber;
		Long time;
		if(type.equals("req"))
		{
		nodeNumber = Character.getNumericValue(Message.charAt(18));
		time = Long.parseLong(Message.substring(23,36));
		if((time <= RequestSender.getSelfTime()) || (RequestSender.getSelfTime() == 0) )
		 {
	 	RequestSender.sendReply(nodeNumber);
	         }
	        else
	         { 
	         	updateRequestTable(nodeNumber,time);
         	 }

		}
		
		else if(type.equals("rep"))
		{
			setReplyCount();
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
		else if(type.equals("Com"))
		{
			if(node.getNodeNumber() == 0)
			{
			IncrementComplete();
			}
			System.out.println("received complete notification " + Controller.getNodePhase() + Receiver.getCompleteCount());
		}
	}
        
        
    private void IncrementComplete() {
		// TODO Auto-generated method stub
		completeCount++;
	}
    public static int getCompleteCount()
    {
    	return completeCount;
    }
	private void IncrementNotify() {
		// TODO Auto-generated method stub
		notifyCount++;
                System.out.println("Receiver notify Count is" + getNotifyCount());
	}
     
     public synchronized static int getNotifyCount()
    {
    	 return notifyCount;
     }
	private synchronized void updateRequestTable(int nodeNumber, Long time) {
		  requestTable.put(nodeNumber,time);
    	}
	private synchronized void setReplyCount() {
		// TODO Auto-generated method stub
		replyCount++;
		System.out.println("The reply count is incremented to " + replyCount);
	}
       public static synchronized int getReplyCount()
       {
    	   return replyCount;
       }
       
          
	public static synchronized ConcurrentHashMap<Integer,Long> getrequestTable()
       {
			return requestTable;
	}
	public static synchronized void updateRequestTable(int nodeNum) {
		requestTable.remove(nodeNum);
		}
	public static synchronized void clearReplyCount() {
		// TODO Auto-generated method stub
		replyCount = 0;
		
	}
	public static synchronized ArrayList<Integer> getPermissionList()
	{
		return permissionList;
	}
	public static synchronized void removePermission(int nodeNum) {
		// TODO Auto-generated method stub
		permissionList.remove(nodeNum);
	}
		
	}


