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
			// the thread will sleep for n milli seconds   
			fromUser = stdIn.readLine();
			userName = fromUser; //FIXME In case we need to save username?
			socket_out.println(fromUser); //OUT - Transmit name over the interwebs
			System.out.println(socket_in.readLine()); //IN - Accept Welcome
			socket_out.println(); //OUT - tennis
			//System.out.println(socket_in.readLine()); //IN - Accept start game
			//try { Thread.sleep(2000); } catch (InterruptedException e) { System.out.println(e); }    
			//System.out.print("\033[H\033[2J");//WIPE SCREEN

			//System.out.println(socket_in.readLine()); //IN 

			socket_out.println(stdIn.readLine());
			//socket_out.println("I don't think this will appear");
			while ((fromServer = socket_in.readLine()) != null) { 
				System.out.println(socket_in.readLine()); //IN 
			System.out.println("I'm going bonker");
				System.out.println("A: " + socket_in.readLine());
				if (fromServer.equals("QUIT")) break;

				fromUser = stdIn.readLine(); //IN - Read line from the keyboard
				
				if (fromUser != null) {
					System.out.println("Client " + userName + ": " + fromUser); //OUT
					socket_out.println(fromUser);
				}
			}
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " +
					hostName);
			System.exit(1);
		}
	}
}

