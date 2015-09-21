/*
 *The Class Controller is the main Class in the Server side Java Code that was Written.it contains the main function()
 *for where the threads for Receiver and Controller are Initialized in the classes Node.java and Receiver.java
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


public class Controller extends Thread{
	
	public static String nodeState;  //The variable that holds the state of the Controller
	public static String[] ObjectState = {"unlocked","unlocked","unlocked","unlocked","unlocked","unlocked","unlocked","unlocked"};


	//Default Constructor that Initializes the nodestate as Unlocked
	public Controller()
	{
		nodeState = "unlocked";
		
		/*for(int i=0;i<=7;i++)
		{
			ObjectState[i] = "unlocked";
		} */
		
		/*Initialize a thread to complete the whole Process*/
		FinishThread();
	}

	//Thread that Concludes the total Computation in all CLients and Servers
	private void FinishThread() {

		Thread Complete_Thread = new Thread()
		{
			public void run()
			{
				while(true)
				{
					if(Receiver.getCompleteCount() == 5)
					{
					   	System.out.println("Sending Finish messages to all the Clients and Servers including itself");
						try
						{
						Receiver.sendFinish();
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


/*	public static synchronized void setNodeState(String state)
	{
		nodeState = state;
	}  
	
	public static synchronized String getNodeState()
	{
		return nodeState;
	} */

	public static void main(String[] args) throws IOException, InterruptedException
		{
			final int Port = 1234;
			
		    System.out.println("Creating the Server");
			final Node node = new Node(Port);
	
			System.out.println("Server " + Node.getNodeNumber() +  " creation completed");
			
			
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
			}catch (InterruptedException e) {
 				e.printStackTrace();
			}
			}
			};
			receiver_thread.start();
			/*******************************************************/
									
		}

	public static synchronized String getObjectState(int objectNumber) {
		// TODO Auto-generated method stub
		return ObjectState[objectNumber];
	}
	
	public static synchronized void setObjectState(int ObjectNumber,String State)
	{
		ObjectState[ObjectNumber] = State;
		System.out.println();
	}
}

