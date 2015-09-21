/* Class that maintains threads to various message Sending Attributes of the client node
 *Used to send requests,releases,Notifications like start and complete
 *All these threads are initialized in the Constructor and they keep running until
 *the server 1 sends a finish notification
 */

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
	public static int totalMessageSent = 0;
	public static int totalExchanged = 0;
	public static Long selfTimeStamp = (long)0;
	ArrayList<Integer> requestSent = new ArrayList<Integer>();
	public static int requestCount2 = 0;
	public static int ObjectRequested = 0;
	
	
	public RequestSender(Node node,int port) throws UnknownHostException, IOException, InterruptedException
	{
		System.out.println("Sender Thread " + Thread.currentThread());
		this.node = node;
		this.Port = port;
		/*Initiate a thread that controls sending the Requests*/
		SendRequestThread();
		//Initiate a thread that controls sending the Releases
		SendReleaseThread();
		//Sends complete Notification when CLient completes 20 critical Sections
		SendCompleteThread();
		// Sends notify at the beginning
		SendNotifyThread();
		// Node 1 sends a start to other CLients on receiving all Notify messages
		SendStartThread();
	}
	// Thread that sends a start to all clients
	private void SendStartThread() {
		Thread start_thread = new Thread()
		{
			public void run()
			{
				if(node.getNodeNumber() == 1)
				{
				while(true)
				{   
				 if(Receiver.getNotifyCount() == 5)
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

	// On receiving all the 5 notify messages from the clients,the client1 sends a start notification
	private void sendStart() throws UnknownHostException, IOException {
	for(int i=21;i<=25;i++)
	{
	 	 String hostName;
		 hostName = "dc"+i+".utdallas.edu";
		 sender = new Socket(hostName,Port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("start from Node "+ node.getNodeNumber() + " at " + System.currentTimeMillis());
		 IncTotalMessageSent();
	  
	}
	}
	
	// All the clients send a notify Message to the client 1	
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
//							FileIO.write("Sending a notify message from " + node.getNodeNumber() + " to node 0");
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

	// All the clients send a Complete notification to Server 1 on coming out of critical Section 20times
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
//							FileIO.write("Sending the complete notification to Server 0 from " + node.getNodeNumber());
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
		 String hostName = "dc11.utdallas.edu";
		 sender = new Socket(hostName,Port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("Complete Notification from Client "+ Node.getNodeNumber() + " at " +  System.currentTimeMillis());
		 IncTotalMessageSent();
	}

	// A client sends a release to Servers 1 to 7 on exiting the critical Section
	private void SendReleaseThread() {
		// TODO Auto-generated method stub
		Thread sendReleaseThread = new Thread()
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
			sendReleaseThread.start();
			
	}
	public static synchronized void sendRelease() throws UnknownHostException, IOException {
		 
		 for(int i=11;i<=17;i++)
		 {
		 String hostName = "dc"+i+".utdallas.edu";
		 sender = new Socket(hostName,Port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		// System.out.println("Release from " + node.getNodeNumber() + " hostName " + hostName + " from " + node.getNodeNumber());
		 out.writeUTF("release from "+ node.getNodeNumber() + " for Object " + RequestSender.getObjectRequested() +" at " + System.currentTimeMillis());
		 IncTotalMessageSent();
		 }
		 
	}

	public static synchronized void sendFinish() throws UnknownHostException, IOException {
		String hostName;
		for(int i=21;i<=25;i++)
		{
		 
		   hostName = "dc"+i+".utdallas.edu";
		  
		  sender = new Socket(hostName,Port);
                  OutputStream outToServer = sender.getOutputStream();
                  DataOutputStream out = new DataOutputStream(outToServer);
                  System.out.println("Sending Finish to Nodenumber " + i + " hostName " + hostName + " from " + node.getNodeNumber());
                  out.writeUTF("Finish from "+ node.getNodeNumber() + " at " + System.currentTimeMillis() + " to " + i);
		  IncTotalMessageSent();
		}
	
	}
	// A thread that sends requests to all the 7 Servers on initilaizing and waits for a random period to issue requests
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
				//	System.out.println("in Phase 1");
				 	Thread.sleep(getRandom());
				 	}
					else if(Controller.getNodePhase().equals("Complete"))
					{
					break;
					}
		//			System.out.println("Requesting");
					Controller.setNodeState("requesting");
					SendRequest();
					Controller.setNodeState("waiting");
		//			System.out.println("Waiting");
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
  	// Function to generate a random time period to send requests in milliseconds	
	private long getRandom() {
		// TODO Auto-generated method stub
		int[] array = {500,600,700,800,900,1000};
		int rnd = new Random().nextInt(array.length);
	    return array[rnd];
	}
	
	// Function to select random object for which request is meant to be made
	private int getRandomObject() {
		// TODO Auto-generated method stub
		int[] array = {1,2,3,4,5,6,7};
		int rnd = new Random().nextInt(array.length);
	    return array[rnd];
	}
	// The actual fuction where requests are sent to all the 7 servers	
	public void SendRequest() throws UnknownHostException, IOException, InterruptedException
	{
		Long timeStamp = System.currentTimeMillis();
		setSelfTime(timeStamp);
		int Object = getRandomObject();
		setObjectRequested(Object);
//		System.out.println("Sending requests with timestamp " + timeStamp);
		for(int i=11;i<=17;i++)
		{
			 			
			 String  hostName = "dc"+(i)+".utdallas.edu";
			 sender = new Socket(hostName,Port);
			 OutputStream outToServer = sender.getOutputStream();
			 DataOutputStream out = new DataOutputStream(outToServer);
			 out.writeUTF("request from Node "+ node.getNodeNumber() + " for Object " + Object + " at " + timeStamp);
		//	 System.out.println("request from Client "+ node.getNodeNumber() + " at " + timeStamp);
			 IncRequestCount();
			 IncTotalMessageSent();
			 IncExchanged();
		  
		}
		
	}
	// Set the Requested Object by the Client
	private synchronized void setObjectRequested(int object) {
		// TODO Auto-generated method stub
		ObjectRequested = object;
	}
	// get the requested Object by the Client
	public static synchronized int getObjectRequested()
	{
		return ObjectRequested;
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
   		requestCount++;
   		}
	private static synchronized void IncTotalMessageSent()
	{
		totalMessageSent++;
	}

	private static synchronized void IncRequestCount2()
	{
		requestCount2++;
	}
	
          public static synchronized int getRequestCount()
          {
       	   return requestCount;
          }
	  public static synchronized int getTotalMessageSent()
	  {
	   return totalMessageSent;
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
	 
	  // Function that sends the notify messages to client 1
		public synchronized void sendNotify() throws IOException {
			
			 String hostName = "dc21.utdallas.edu";
			 sender = new Socket(hostName,Port);
			 OutputStream outToServer = sender.getOutputStream();
			 DataOutputStream out = new DataOutputStream(outToServer);
			 out.writeUTF("notify from Node " + node.getNodeNumber() + " at " + System.currentTimeMillis() + " to node0");
		     System.out.println("notify from Node " + node.getNodeNumber() + " at " + System.currentTimeMillis() + " to node0");
			
		}
		public static void sendCommitRequests() throws UnknownHostException, IOException {
			int i = getObjectRequested();
			if(i <= 5)
			{
			for(int j=i;j<=i+2;i++)
			{
				int k = j+10;
				sendReq(k,i);
			}
			}
			else if(i == 6)
			{
				for(int j=i;j<=i+1;i++)
				{
					int k = j+10;
					sendReq(k,i);
				}
				sendReq(11,i);
			}
			else if(i == 7)
			{
				for(int j=1;j<=2;j++)
				{
					int k=j+10;
					sendReq(k,i);
				}
				sendReq(17,i);
			}
			
		}
		private static void sendReq(int k, int i) throws UnknownHostException, IOException {
			// TODO Auto-generated method stub
			String hostName = "dc"+k+".utdallas.edu";
			sender = new Socket(hostName,Port);
			OutputStream outToServer = sender.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF("creq for Object " + i + " from "  + node.getNodeNumber());
			System.out.println("Sending Commit requests to " + k);
			
		}
	
		public static void sendCommit() throws UnknownHostException, IOException {
			// TODO Auto-generated method stub
			int i = getObjectRequested();
			if(i <= 5)
			{
			for(int j=i;j<=i+2;i++)
			{
				int k = j+10;
				sendCommit(k,i);
			}
			}
			else if(i == 6)
			{
				for(int j=i;j<=i+1;i++)
				{
					int k = j+10;
					sendCommit(k,i);
				}
				sendCommit(11,i);
			}
			else if(i == 7)
			{
				for(int j=1;j<=2;j++)
				{
					int k=j+10;
					sendCommit(k,i);
				}
				sendCommit(17,i);
			}
		}
		private static void sendCommit(int k, int i) throws UnknownHostException, IOException {
			// TODO Auto-generated method stub
			String hostName = "dc"+k+".utdallas.edu";
			sender = new Socket(hostName,Port);
			OutputStream outToServer = sender.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF("critical Section for Object " + i + " from "  + node.getNodeNumber() + " for " + (Controller.getCriticalCount()+1) + " th time");
			System.out.println("Sending commit to " + k);
		}
	
		
}
