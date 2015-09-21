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
	public static String nodeState; 
	public Controller()
	{
		nodeState = "unlocked";
		
		/*Initialize a thread to complete the whole Process*/
		FinishThread();
	}


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


	public static synchronized void setNodeState(String state)
	{
		nodeState = state;
	}
	
	public static synchronized String getNodeState()
	{
		return nodeState;
	}
	

	public static void main(String[] args) throws IOException, InterruptedException
		{
			final int Port = 1234;
			
		    System.out.println("Creating the Server");
			final Node node = new Node(Port);
			System.out.println("Server " + Node.getNodeNumber() +  " creation completed and State is" + Controller.getNodeState());
			
			
			/* initialize a Controller object*/
		
			Controller controller = new Controller();
	//		System.out.println("Server " + Node.getNodeNumber() +  " creation completed and State is" + Controller.getNodeState());
			
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
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			};
			receiver_thread.start();
			/*******************************************************/
			
			
			
			/* initialize a FileIO*/
			
		//	FileIO filewrite = new FileIO();
									
		}

}

