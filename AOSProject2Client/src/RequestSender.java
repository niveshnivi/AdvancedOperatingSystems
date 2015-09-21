import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
public class RequestSender {
	
        static	Socket sender;
        static	int Port;
	static Node node;
	public static int requestCount = 0;
	public static int totalMessageCount = 0;
	public static int totalExchanged = 0;
	public static Long selfTimeStamp = (long)0;
	ArrayList<Integer> requestSent = new ArrayList<Integer>();
	public static int requestCount2 = 0;
	
	
	public RequestSender(Node node,int port) throws UnknownHostException, IOException, InterruptedException
	{
		System.out.println("Sender Thread " + Thread.currentThread());
		this.node = node;
		this.Port = port;
		/*Initiate a thread that controls sending the Requests*/
		SendRequestThread();
		SendReleaseThread();
		SendCompleteThread();
		SendNotifyThread();
		SendStartThread();
	}
	private void SendStartThread() {
		Thread start_thread = new Thread()
		{
			public void run()
			{
				if(node.getNodeNumber() == 0)
				{
				while(true)
				{   
				 if(Receiver.getNotifyCount() == 4)
					{
						try {
							sendStart();
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
				}
				
			}
		};
		start_thread.start();		
	}
	private void sendStart() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
	for(int i=11;i<=15;i++)
	{
	 	 String hostName;
		 hostName = "dc"+i+".utdallas.edu";
		 sender = new Socket(hostName,Port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("start from Node "+ node.getNodeNumber() + " at " + System.currentTimeMillis());
		 IncTotalMessageCount();
	  
	}
	}
	
	
	private void SendNotifyThread() {
		
		Thread Initiator = new Thread()
		{
			public void run()
			{
				if(node.getNodeNumber() != 0)
				{
				while(true)
				{
					if(Controller.getflag())
					{
						try {
							sendNotify();
							System.out.println("Sending a notify message from " + node.getNodeNumber() + " to node 0");
							FileIO.write("Sending a notify message from " + node.getNodeNumber() + " to node 0");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
				}
			}
			
		};
		Initiator.start();
		
	}
	private void SendCompleteThread() {
		// TODO Auto-generated method stub
		Thread sendCompleteThread = new Thread()
		{
			public void run()
			{
				while(true)
				{
					//if(node.getNodeNumber() != 0)
					//{
					if(Controller.getNodePhase().equals("Complete"))
					{
						try {
							System.out.println("Sending the complete notification to node 0");
							FileIO.write("Sending the complete notification to Server 0 from " + node.getNodeNumber());
							sendComplete();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
					//}					
				}
			}

			
		};
		sendCompleteThread.start();
	}
	
	private void sendComplete() throws IOException {
		// TODO Auto-generated method stub
		 String hostName = "dc01.utdallas.edu";
		 sender = new Socket(hostName,Port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("Complete Notification from Client "+ Node.getNodeNumber() + " at " +  System.currentTimeMillis());
		 IncTotalMessageCount();
	}
	private void SendReleaseThread() {
		// TODO Auto-generated method stub
		Thread sendReplyThread = new Thread()
		{
			public void run()
			{
				
				while(true){
				 //  if((!Controller.getNodeState().equals("criticalSection")) && (!Controller.getNodeState().equals("requesting")))
				   if(Controller.getNodeState().equals("release"))
				   {
					   try {
						sendRelease();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					   Controller.setNodeState("ready");
					 }
					}
					
				
				
			}
			};
			sendReplyThread.start();
			
	}
	public static synchronized void sendRelease() throws UnknownHostException, IOException {
		 
		 for(int i=1;i<=7;i++)
		 {
		 String hostName = "dc0"+i+"utdallas.edu";
		 sender = new Socket(hostName,Port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 System.out.println("Release from " + node.getNodeNumber() + " hostName " + hostName + " from " + node.getNodeNumber());
		 out.writeUTF("release from "+ node.getNodeNumber() + " at " + System.currentTimeMillis());
		 IncTotalMessageCount();
		 IncExchanged();
		 }
		 
	}


	private void SendRequestThread() {
		
		Thread sendRequestThread = new Thread()
		{
			public void run()
			{
				try {
					System.out.println("The state of the node is " + Controller.getNodeState());
					while(true)
					{
					if(Controller.getNodeState().equals("ready"))
					{
					if(Controller.getNodePhase().equals("Phase1"))
					{
					System.out.println("in Phase 1");
				 	Thread.sleep(getRandom());
				 	}
					else if(Controller.getNodePhase().equals("Complete"))
					{
					break;
					}
					System.out.println("Requesting");
					Controller.setNodeState("requesting");
					SendRequest();
					System.out.println("Waiting");
					Controller.setNodeState("waiting");
					
					}
					
					
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
		};
		sendRequestThread.start();
	}
	
	private long getRandom() {
		// TODO Auto-generated method stub
		int[] array = {500,600,700,800,900,1000};
		int rnd = new Random().nextInt(array.length);
	    return array[rnd];
	}
	private long getRandom2() {
		// TODO Auto-generated method stub
		int[] array = {4500,4600,4700,4800,4900,5000};
		int rnd = new Random().nextInt(array.length);
	    return array[rnd];
	}
	
	public void SendRequest() throws UnknownHostException, IOException, InterruptedException
	{
		Long timeStamp = System.currentTimeMillis();
		setSelfTime(timeStamp);
		System.out.println("Sending requests with timestamp " + timeStamp);
		for(int i=1;i<7;i++)
		{
			 			
			 String  hostName = "dc0"+(i)+".utdallas.edu";
			 sender = new Socket(hostName,Port);
			 OutputStream outToServer = sender.getOutputStream();
			 DataOutputStream out = new DataOutputStream(outToServer);
			 out.writeUTF("request from Node "+ node.getNodeNumber() + " at " + timeStamp);
			 System.out.println("request from Client "+ node.getNodeNumber() + " at " + timeStamp);
			 IncRequestCount();
			 IncTotalMessageCount();
			 IncExchanged();
		  
		}
		
	}
	public static synchronized Long getSelfTime()
    {
		return selfTimeStamp;
    	
    }
	public static synchronized void  setSelfTime(long selfTime)
    {
		selfTimeStamp = selfTime;
    	
    }
	private static synchronized  void IncRequestCount() {
   		// TODO Auto-generated method stub
   		requestCount++;
		System.out.println("The requestCount is " + requestCount);
   		}
	private static synchronized void IncTotalMessageCount()
	{
		totalMessageCount++;
	}

	private static synchronized void IncRequestCount2()
	{
		requestCount2++;
	}
	
          public static synchronized int getRequestCount()
          {
       	   return requestCount;
          }
	  public static synchronized int getTotalMessageCount()
	  {
	   return totalMessageCount;
	  }
	  public static synchronized void IncExchanged()
	  {
	   totalExchanged++;
	  }
	  public static synchronized void clearExchanged()
	  {
	   totalExchanged = 0;
	  }
	  public static synchronized int getExchanged()
	  {
	  return totalExchanged;
	  }          
          public static synchronized int getRequestCount2()
          {
       	   return requestCount2;
          }
	 
	  public static synchronized void clearRequestCount2()
	  {
	  	requestCount2 = 0;
	  }

          public static synchronized void clearRequestCount()
          {
        	  requestCount = 0;
          }
		public synchronized void sendNotify() throws IOException {
			
			 String hostName = "dc11.utdallas.edu";
			 sender = new Socket(hostName,Port);
			 OutputStream outToServer = sender.getOutputStream();
			 DataOutputStream out = new DataOutputStream(outToServer);
			 out.writeUTF("notify from Node " + node.getNodeNumber() + " at " + System.currentTimeMillis() + " to node0");
		     System.out.println("notify from Node " + node.getNodeNumber() + " at " + System.currentTimeMillis() + " to node0");
			
		}
		
}
