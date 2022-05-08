//You: Put your and your parter's names here
//You: Describe which bullet points you implemented
//You: If you did anything worthy of extra credit, put them here

//Point Yi) Have the server automatically begin the game when three (exactly three) clients
//connect. Before the game begins, the only thing that should happen is that the
//clients should enter their name for the scoreboard.

//Point Er) Once three people are in, then the game begins and should ask one
//question at a time to the clients. Everyone needs to be on the same question
//at the same time. In other words, don't just move on to the next question when
//the first client responds - ALL of them have to give a response to move on to
//the next question. (Hint: Use a ConcurrentHashMap to track client responses.)

//Point San) After each question, send to the clients the current score for
//every player, so people can see how they're doing. They should win points for
//getting questions right and lose points for getting questions wrong. As the
//quiz progresses, the point value of the questions should go up.

//Point Yon) You need to have some sort of data structure holding all of the
//quiz questions and responses. Don't just cheap out with a bunch of if
//statements or something. The topic for the quiz can be anything you like, and
//the answers can either be multiple choice or free response or whatever.
//TODO Tim
//So far, we are reading in from a file that contains the questions, answers, and answer key
//They can have any number of questions, T/F, multiple choice etc
//Located in "questions.txt" and delimited by "_"

//Point Cinco) When the game is over, it needs to tell the winner (the person(s)
//with the high score) they won, and to tell everyone else they're a loser. The
//server should then reset itself back to a starting state so another three
//people can connect. Some of the code right now will absolutely not work a
//second time through unless you fix this up.
import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.Map; // import the Map class
import java.util.HashMap; // import the HashMap class
//import java.util.Vector; // import the HashMap class
//import java.util.TreeMap; // A sorted map
import java.util.LinkedHashMap; // A sorted map

public class Server {
	//Note: Static variables are tied to a class, not to objects of that class.
	// Here we use static variables because that makes all the threads able to 
	// access them equally well. You just have to be careful to be "thread safe".
	//This variable is only used by main() so we just make a normal Integer
	static Integer thread_count = 0; //How many clients are connected

	//Note: Atomics are a safe way to share an int between threads
	//Using an AtomicInteger must be used if multiple threads are going to read and write to a shared variable
	//This just tracks how many lines total have been read from the clients
	static AtomicInteger chat_count = new AtomicInteger(); 

	//Note: A ConcurrentHashMap is a thread-safe hash table you can share between threads
	// You can use .get() to get data from it and .put() to put data into it
	// If you do a get() and there's nothing there, it will throw an exception
	static ConcurrentHashMap<Integer,Integer> scoreboard = new ConcurrentHashMap<Integer,Integer>(); //Holds Scores
	static ConcurrentHashMap<Integer,String> names = new ConcurrentHashMap<Integer,String>(); //Client Names

	//YOU: You may need to make another ConcurrentHashMap to track, for example, what question each thread is on

	//YOU: You need to add all the logic for doing a quiz, including maybe another static nested class or something
	// You might also might want to make a class so as to consolidate the ConcurrentHashMaps into one

	//This is a "nested class" - a class defined within another class
	static public class ServerThread extends Thread {
		private Socket socket = null;
		private Integer thread_id = -1;

		public ServerThread(Socket socket, int thread_id) {
			super("ServerThread");
			this.socket = socket;
			this.thread_id = thread_id; //Note: Each thread has its own unique thread_id
		}

		public void run() {
			try (
					PrintWriter socket_out = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			    ) {
				String inputLine, outputLine;
				inputLine = socket_in.readLine(); //Get their name from the network connection
				outputLine = "Welcome " + inputLine;
				socket_out.println(outputLine); //Write a welcome message to the network connection

				//Save their name into the ConcurrentHashMap
				names.put(thread_id,inputLine);

				//Set our score to 0 in the ConcurrentHashMap to begin with
				scoreboard.put(thread_id,0);

				//YOU: Remove this demo code and write Jeopardy
				while ((inputLine = socket_in.readLine()) != null) {
					System.out.println("Thread " + thread_id + " read: " + inputLine);
					if (inputLine.equals("QUIT"))
						break;
					//Note: If you want to use atomics, it works like this
					//Does atomic increment, and returns the value into x
					int x = chat_count.incrementAndGet();

					//Access our score from the shared scoreboard
					int score = scoreboard.get(thread_id);

					//Sample example of getting a question right
					//YOU: Replace this with actual quiz show logic that checks to see if they got the answer right
					if (Math.random() < 0.5) { //50/50 chance of getting a question right
						score += 100;
						socket_out.println("Correct! Your score is now " + score);
					}
					else { //Wrong answer!
						score -= 100;
						socket_out.println("Wrong!!!! Score: " + score);
					}
					//Set our new score
					scoreboard.put(thread_id,score);

					//Note: This prints the current scoreboard, delete it if it gets too spammy
					System.out.println("====== Scoreboard ======");
					//Note that this assumes we only have one game, it won't work with a second game, etc.
					//But it should show you the way, so I am leaving it here
					for (int i = 0; i < thread_count; i++) {
						System.out.println(names.get(i) + ": " + scoreboard.get(i));
					}

					//YOU: Wait for the other clients to answer before moving on to the next question

				}
				System.out.println("Thread closing");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		//Usage vetting
		if (args.length != 1) {
			System.err.println("Usage: java Server <port number>");
			System.exit(1);
		}

		//Importing the question list
		LinkedHashMap<String, LinkedHashMap<String, Boolean>> questions = new LinkedHashMap<String, LinkedHashMap<String, Boolean>>(); //Data set that holds pairs of pairs
		try {
			File myObj = new File("questions.txt"); //This file holds all ouf our questions, answers, and solutions
			Scanner cin = new Scanner(myObj); //I want to go back to C++
			while (cin.hasNextLine()) {
				String question = cin.nextLine();
				if (question.equals("_")) { continue; } //If we reach a delimiter, look for another question
				LinkedHashMap<String, Boolean> answers = new LinkedHashMap <String, Boolean>();
				while (cin.hasNextLine()) {
					String answer = cin.nextLine();
					if (answer.equals("_")) { break; }  //If we reach a delimiter, look for another question
					String b = cin.nextLine();
					if (b.equals("0")) { answers.put(answer, false); }
					else if (b.equals("1")) { answers.put(answer, true); }
					else continue;
				}
				questions.put(question, answers);
			}
			cin.close();
		} 
		catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		//Iterate over our questions list
		//TODO: Leaving this in to prove my worth that my data structure works. Will remove later
		for (String s : questions.keySet()) {
			int i = 1;
			System.out.println(s);
			for (String r : questions.get(s).keySet()) {
				System.out.print("[" + i + "] ");
				i = i + 1;
				System.out.println(r);
			}
		}

		int portNumber = Integer.parseInt(args[0]);
		//YOU: Uncomment this line to get the code to work
		boolean listening = true; //FIXME Kerney bug?

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) { //FIXME KERNEY BUG
			while (listening) {
				ServerThread new_thread = new ServerThread(serverSocket.accept(),thread_count); 
				new_thread.start();
				System.out.println("Client " + Integer.toString(thread_count) + " connected");
				thread_count++;
			}
		} 
		catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}
	}
}
//hello this is a test
//another small comment