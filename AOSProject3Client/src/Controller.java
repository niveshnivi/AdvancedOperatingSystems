/*
 *The Class Controller is the main Class in the Client side Java Code that was Written.it contains the main function()
 *for where the threads for Receiver,Request Sender  and Controller are Initialized in the classes Node.java and Receiver.java
 *It also contains special thread to finish the total Computation and bring all the Servers and Clients to Conclude and exit.
 */

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;

// Class Extends a Thread functionality
public class Controller extends Thread{
	
	//public boolean criticalSection = true;
	public static String nodeState; 
	public static int criticalCount = 0;
	public static String nodePhase; 
	static boolean flag = false;
	static long timeOut;
	
	// Constructor to initialize node state and Phase and thread to monitor node entering Critical Section	
	public Controller()
	{
		this.nodeState = "notready";
		this.nodePhase = "Phase1";
		/*Initialize a thread to enter and exit critical Section*/
		monitorCritical();
		/*Initialize a thread to complete the whole Process*/
		FinishThread();
	}

	// Thread to send the Complete Notification
	private void FinishThread() {
		// TODO Auto-generated method stub
		Thread Complete_Thread = new Thread()
		{
			public void run()
			{
				while(true)
				{
					if(Receiver.getCompleteCount() == 5)
					{
						//System.out.println("exiting the system");
						System.out.println("Sending Finish messages to all the nodes including itself");
						try
						{
						RequestSender.sendFinish();
						}
						catch(IOException e)
						{
						e.printStackTrace();
						}
						break;
					}
				}
			}
		};
		Complete_Thread.start();
	}


	
	// Thread that Monitors the Critical Section based on the Quorum Received
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
			if(Controller.getNodeState().equals("waiting")) 
			{
			if(QuorumReceived())
			{
				Long timeStamp = System.currentTimeMillis();
				Long selfTime = RequestSender.getSelfTime();
				String message = "** Node " + Node.getNodeNumber() + " Entering the Critical Section for 3 time units at " + System.currentTimeMillis() + " for " + (getCriticalCount()+1) + " th time ** ";
				System.out.println("******************************************************************************");
				System.out.println("** The time elapsed in reaching critical section is " + (timeStamp - selfTime));
//				System.out.println(timeStamp - selfTime);
				System.out.println("** The number of exchanged messages is " + RequestSender.getExchanged());
				Controller.setNodeState("criticalSection");
				RequestSender.sendCommitRequests();
				while(true)
				{
					if(!Receiver.abortReceived())
					{
					 if(Receiver.getAckCount() == 3)
					 {
					  RequestSender.sendCommit();
					  break;
					 }
					}
					else
					{
						System.out.println("aborting the Write");
						break;
					}
				}
				FileIO.write(message,RequestSender.getObjectRequested());
				System.out.println("** Node " + Node.getNodeNumber() + " Entering the Critical Section for 3 time units at " + System.currentTimeMillis() + " for " + (getCriticalCount()+1) + " th time ** ");
			//	Thread.sleep(300);
				System.out.println("Leaving Critical Section");
				System.out.println("*******************************************************************************");
				IncCriticalCount();
				RequestSender.clearExchanged();
				if(getCriticalCount() == 20)
				{
					
//						System.out.println("setting the Phase of the node to Complete");
						setNodeState("Complete");
						setNodePhase("Complete");
					
				}
//				System.out.println("setting the phase to release");
				setNodeState("release");
				Receiver.clearQuorum();
			}
			}
		}
		
	}

	// Checks if necessary votes are received and returns true when the quorum is received
	private boolean QuorumReceived() {
		boolean[] S = Receiver.getQuorum();
		if((S[1]&&S[2]&&S[4]) || (S[1]&&S[2]&&S[5]) || (S[1]&&S[4]&&S[5]) || (S[1]&&S[3]&&S[6]) || (S[1]&&S[3]&&S[7]) || (S[1]&&S[6]&&S[7])
			|| (S[2]&&S[4]&&S[3]&&S[6])	|| (S[2]&&S[4]&&S[3]&&S[7]) || (S[2]&&S[4]&&S[6]&&S[7])	|| (S[2]&&S[5]&&S[3]&&S[6]) || (S[2]&&S[5]&&S[3]&&S[7])
			|| (S[2]&&S[5]&&S[6]&&S[7]) || (S[4]&&S[5]&&S[3]&&S[6]) || (S[4]&&S[5]&&S[3]&&S[7]) || (S[4]&&S[5]&&S[6]&&S[7]))
		{
			return true;
		}
		return false;
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
			
		    System.out.println("Creating the node");
			final Node node = new Node(Port);
			System.out.println("Creation of Client " + Node.getNodeNumber() + "is Completed");
			
			
			/* initialize a Controller object*/
		
			Controller controller = new Controller();
			
			/* creating a thread for a socket receiver which always listens to sockets and receives packets both request and reply*/
			Thread receiver_thread = new Thread()
			{
			public void run()
			{
			  try {
				System.out.println("Inside the Run block");
				Receiver receiver = new Receiver(node,Port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			};
			receiver_thread.start();
			/*******************************************************/
			/*initialize a sender */
		
			RequestSender Sender = new RequestSender(node,Port);
			
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
	}


