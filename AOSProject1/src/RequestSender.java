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
	
	static Socket sender;
	static int Port=1234;
	static Node node;
	public static int requestCount = 0;
	public static Long selfTimeStamp = (long) 0;
	ArrayList<Integer> requestSent = new ArrayList<Integer>();
	public static int requestCount2 = 0;
	
	
	public RequestSender(Node node,int port) throws UnknownHostException, IOException, InterruptedException
	{
		System.out.println("Sender Thread " + Thread.currentThread());
		this.node = node;
		this.Port = port;
		/*Initiate a thread that controls sending the Requests*/
		Thread.sleep(10000);
		SendRequestThread();
		SendReplyThread();
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
	for(int i=1;i<=5;i++)
	{
	 	 String hostName;
		 if(i == 10)
		 {
	       hostName = "dc10.utdallas.edu";		 
		 }
		 else{
		   hostName = "dc0"+i+".utdallas.edu";
		 }
		 sender = new Socket("localhost",Port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("start from Node "+ node.getNodeNumber() + " at " + System.currentTimeMillis());
		 
	  
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
							FileIO.write("Sending the complete notification to node 0 from" + node.getNodeNumber());
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
		 sender = new Socket("localhost",Port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("Complete Notification from "+ Node.getNodeNumber() + " at " +  System.currentTimeMillis());
	}
	private void SendReplyThread() {
		// TODO Auto-generated method stub
		Thread sendReplyThread = new Thread()
		{
			public void run()
			{
				
				while(true){
				   if((!Controller.getNodeState().equals("criticalSection")) && (!Controller.getNodeState().equals("requesting")))
				   {
					    ConcurrentHashMap<Integer, Long> requestTable = Receiver.getrequestTable(); 
						//Iterator it = requestTable.entrySet().iterator();
						Set<Integer> keys = requestTable.keySet();
						
					   for(int key:keys)
					   {
						   int nodeNum = key;
						   Long time = (Long) requestTable.get(key);
						   if(time <= getSelfTime())
						   {
							   try {
								
								sendReply(nodeNum);
								if(Controller.getNodePhase().equals("Phase2"))
								{
									if(Controller.getCriticalCount() > 3)
									{
									Receiver.removePermission(nodeNum);
									}
								}
								Receiver.updateRequestTable(nodeNum);
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						   }
				
					   }
					 
					}
				}
				
			}
			};
			sendReplyThread.start();
			
	}
	public static void sendReply(int nodeNum) throws UnknownHostException, IOException {
		 //System.out.print("Inside send Reply");
		 String hostName;
		 if(nodeNum == 9)
		 {
			 hostName = "dc10.utdallas.edu";
		 }
		 else{
		     hostName = "dc0"+nodeNum+1+".utdallas.edu";
		 }
		 sender = new Socket("localhost",Port);
		 OutputStream outToServer = sender.getOutputStream();
		 DataOutputStream out = new DataOutputStream(outToServer);
		 out.writeUTF("reply to "+ nodeNum + " at " + System.currentTimeMillis());
		 
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
					else if(Controller.getNodePhase().equals("Phase2"))
					{
					 System.out.println("in Phase 2");
					 if(node.getNodeNumber()%2 == 0)
					 {
						 Thread.sleep(getRandom2());
					 }
					 else
					 {
						 Thread.sleep(getRandom());
					 }
					}
					Controller.setNodeState("requesting");
					SendRequest();
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
		int[] array = {5000,6000,7000,8000,9000,10000};
		int rnd = new Random().nextInt(array.length);
	    return array[rnd];
	}
	private long getRandom2() {
		// TODO Auto-generated method stub
		int[] array = {45000,46000,47000,48000,49000,50000};
		int rnd = new Random().nextInt(array.length);
	    return array[rnd];
	}
	
	public void SendRequest() throws UnknownHostException, IOException, InterruptedException
	{
		Long timeStamp = System.currentTimeMillis();
		setSelfTime(timeStamp);
		if(Controller.getNodePhase().equals("Phase1"))
		{
		for(int i=0;i<5;i++)
		{
		 if(i != node.getNodeNumber())
		  {
			 String hostName;
			 if(i == 9)
			 {
		       hostName = "dc10.utdallas.edu";		 
			 }
			 else{
			   hostName = "dc0"+(i+1)+".utdallas.edu";
			 }
			 sender = new Socket("localhost",Port);
			 OutputStream outToServer = sender.getOutputStream();
			 DataOutputStream out = new DataOutputStream(outToServer);
			 out.writeUTF("request from Node "+ i + " at " + timeStamp);
			 //System.out.println("request from Node "+ i + " at " + timeStamp);
			 requestCount++;
		  }
		}
		}
		else if(Controller.getNodePhase().equals("Phase2"))
		{
			System.out.println("The list of permissions obtained is " + Receiver.getPermissionList());
			ArrayList<Integer> permissionList = Receiver.getPermissionList();
			for(int i=0;i<3;i++)
			{
				if(Receiver.getPermissionList().size() != 0)
				{	
				if(!Receiver.getPermissionList().contains(i))
				{
					 if(i != node.getNodeNumber())
					  {
						 String hostName;
						 if(i == 9)
						 {
					       hostName = "dc10.utdallas.edu";		 
						 }
						 else{
						   hostName = "dc0"+(i+1)+".utdallas.edu";
						 }
						 sender = new Socket("localhost",Port);
						 OutputStream outToServer = sender.getOutputStream();
						 DataOutputStream out = new DataOutputStream(outToServer);
						 out.writeUTF("request from Node "+ node.getNodeNumber() + " at " + timeStamp);
						 //System.out.println("request from Node "+ node.getNodeNumber() + " at " + timeStamp);
						 requestCount2++;
					  }
				}
				}
			}
		}
		
	}
	public static Long getSelfTime()
    {
		return selfTimeStamp;
    	
    }
	public static void setSelfTime(long selfTime)
    {
		selfTimeStamp = selfTime;
    	
    }
	private void IncRequestCount() {
   		// TODO Auto-generated method stub
   		requestCount++;
   		}
          public static int getRequestCount()
          {
       	   return requestCount;
          }
          
          public static int getRequestCount2()
          {
       	   return requestCount2;
          }
          public static void clearRequestCount()
          {
        	  requestCount = 0;
          }
		public void sendNotify() throws IOException {
			
			 String hostName = "dc01.utdallas.edu";
			 sender = new Socket("localhost",Port);
			 OutputStream outToServer = sender.getOutputStream();
			 DataOutputStream out = new DataOutputStream(outToServer);
			 out.writeUTF("notify from Node " + node.getNodeNumber() + " at " + System.currentTimeMillis() + " to node0");
			 System.out.println("notify from Node " + node.getNodeNumber() + " at " + System.currentTimeMillis() + " to node0");
			
		}
		public static synchronized void sendFinish() throws IOException {
			// TODO Auto-generated method stub
			String hostName;
            for(int i=1;i<=10;i++)
            {
              if(i == 10)
              {
               hostName = "dc11.utdallas.edu";
              }
              else
              {
               hostName = "dc0"+i+".utdallas.edu";
              }
              sender = new Socket(hostName,Port);
              OutputStream outToServer = sender.getOutputStream();
              DataOutputStream out = new DataOutputStream(outToServer);
//              System.out.println("Sending Finish to Nodenumber " + i + " hostName " + hostName + " from " + node.getNodeNumber());
              out.writeUTF("Finish from "+ node.getNodeNumber() + " at " + System.currentTimeMillis() + " to " + i);
            }
	
		}
		
}
