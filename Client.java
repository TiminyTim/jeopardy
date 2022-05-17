import java.io.*;
import java.net.*;

public class Client {
	public static void main(String[] args) throws IOException {

		if (args.length != 2) {
			System.err.println(
					"Usage: java Client <host name> <port number>");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		try (
				Socket mySocket = new Socket(hostName, portNumber);
				PrintWriter socket_out = new PrintWriter(mySocket.getOutputStream(), true);
				BufferedReader socket_in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
		    ) {
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			String fromServer;
			String fromUser;
			String userName;
			System.out.println("Please enter your name:");
			fromUser = stdIn.readLine();
			userName = fromUser; //FIXME In case we need to save username?
			socket_out.println(fromUser); //OUT - Transmit name over the interwebs

			fromServer = socket_in.readLine();//IN - Welcome message
			System.out.println(fromServer);
			socket_out.println(""); //OUT - tennis?? TODO

			fromServer = socket_in.readLine(); //IN - Accept start game?
			System.out.println(fromServer);
			socket_out.println(""); //EMPTY OUT?

			//HIT ENTER to start game
			//fromUser = stdIn.readLine();
			//socket_out.println(stdIn.readLine());

			//System.out.print("\033[H\033[2J");//WIPE SCREEN

			while (true) { 
				//while ((fromServer = socket_in.readLine()) != null) { 
				//Get Question
				fromServer = socket_in.readLine(); //IN
				System.out.println(fromServer);  
				if (fromServer.equals("QUIT")) break;

				//Get answer
				System.out.print("Answer: ");
				while (true) {
					fromUser = stdIn.readLine(); //LOCAL
					if (fromUser != null) {
						System.out.println("Client " + userName + ": " + fromUser); //OUT
						socket_out.println(fromUser);
						break;
					}
				}

				//Get Right/Wrong?
				fromServer = socket_in.readLine(); //IN
				System.out.println(fromServer);  
				//The thread will sleep for n milli seconds   
				//try { Thread.sleep(2000); } catch (InterruptedException e) { System.out.println(e); }    
				if (fromServer.equals("QUIT")) break;
				socket_out.println(""); //EMPTY OUT
				}
			} 
			catch (UnknownHostException e) {
				System.err.println("Don't know about host " + hostName);
				System.exit(1);
			} 
			catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to " +
						hostName);
				System.exit(1);

			}
		    }
	}

