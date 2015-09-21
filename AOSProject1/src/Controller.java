import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;


public class Controller extends Thread{
	
	//public boolean criticalSection = true;
	static ArrayList<Integer> nodes_available = new ArrayList<Integer>();
	public static String nodeState; 
	public static int criticalCount = 0;
	public static String nodePhase; 
	static boolean flag = false;
	static int timeOut;
	
	public Controller()
	{
		this.nodeState = "ready";
		this.nodePhase = "Phase1";
		/*Initialize a thread to enter and exit critical Section*/
		monitorCritical();
		/*Initialize a thread to complete the whole Process*/
		CompleteThread();
	    
	
	}


	private void CompleteThread() {
		// TODO Auto-generated method stub
		Thread Complete_Thread = new Thread()
		{
			public void run()
			{
				while(true)
				{
					if(Receiver.getCompleteCount() == 10)
					{
						System.out.println("exiting the system");
                        System.out.println("Sending Finish messages to all the nodes including itself");
                        try {
							RequestSender.sendFinish();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        break;
					}
				}
			}
		};
		Complete_Thread.start();
	}


	

	private void monitorCritical() {
		// TODO Auto-generated method stub
		Thread Monitor_critical = new Thread()
	    {
	    	public void run()
	    	{
	    		try {
					criticalSection();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    };
	    Monitor_critical.start();
	}


	private void criticalSection() throws InterruptedException, IOException {
		
		while(true)
		{
			if(Controller.getNodePhase().equals("Phase1"))
			{
			if((Controller.getNodeState().equals("waiting")) && (RequestSender.getRequestCount() != 0))
			{
			if(Receiver.getReplyCount() == RequestSender.getRequestCount())
			{
				Controller.setNodeState("criticalSection");
				FileIO.write("Node " + Node.getNodeNumber() + "Entering the Critical Section for 3 time units" + " at " + System.currentTimeMillis() + " for " + (getCriticalCount()+1) + "th time");
				System.out.println("Node " + Node.getNodeNumber() + "Entering the Critical Section for 3 time units" + " at " + System.currentTimeMillis() + " for " + (getCriticalCount()+1) + "th time");
				Thread.sleep(3000);
				IncCriticalCount();
				Receiver.clearReplyCount();
				RequestSender.clearRequestCount();
				if(getCriticalCount() == 4)
				{
					
						System.out.println("setting the Phase of the node to Phase2");
						setNodePhase("Phase2");
					
				}
				setNodeState("ready");
			}
			}
			}
			else if(Controller.getNodePhase().equals("Phase2"))
			{
				if((Controller.getNodeState().equals("waiting")) && (RequestSender.getRequestCount2() != 0))
				{
					if(Receiver.getPermissionList().size() == 9)
					{
						Controller.setNodeState("criticalSection");
						FileIO.write("Node " + Node.getNodeNumber() + "Entering the Critical Section for 3 time units" + " at " + System.currentTimeMillis() + " for " + (getCriticalCount()+1) + "th time");
						Thread.sleep(3000);
						IncCriticalCount();
						if(getCriticalCount() == 8)
						{
							
								System.out.println("setting the Phase of the node to Complete");
								setNodePhase("Complete");
								break;
							
						}
						setNodeState("ready");
					}
				}
			}
		}
		
	}
	public static synchronized void IncCriticalCount()
	{
		criticalCount++;
	}

	public static synchronized int getCriticalCount()
	{
		return criticalCount;
	}
	public static synchronized void setNodeState(String state)
	{
		nodeState = state;
	}
	
	public static synchronized String getNodeState()
	{
		return nodeState;
	}
	
    public static synchronized void setNodePhase(String Phase)
    {
    	nodePhase = Phase;
    }

    public static synchronized String getNodePhase()
    {
    	return nodePhase;
    }
    
    
	public static void main(String[] args) throws IOException, InterruptedException
		{
			final int Port = 1234;
			timeOut = Integer.parseInt(args[0]);
		    System.out.println("Creating the node");
			final Node node = new Node(Port);
			System.out.println("Node creation completed");
			
			
			/* initialize a Controller object*/
		
			Controller controller = new Controller();
			
			/* creating a thread for a socket receiver which always listens to sockets and receives packets both request and reply*/
			Thread receiver_thread = new Thread()
			{
			public void run()
			{
			  try {
				System.out.println("Inside the Run block");
				Receiver receiver = new Receiver(node,4321);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			};
			receiver_thread.start();
			/*******************************************************/
			/*initialize a sender */
		
			RequestSender Sender = new RequestSender(node,1234);
			
			/* initialize a FileIO*/
			
			FileIO filewrite = new FileIO();
			
			/*Set flag to true*/
			
			setflag();
						
							
		}


	public static synchronized void setflag() {
		// TODO Auto-generated method stub
		flag = true;
	}
	
	public static synchronized boolean getflag()
	{
		return flag;
	}
	
	public static void updateNodesAvail(int nodeNum)
	{
		nodes_available.add(nodeNum);
	}


	
	}


