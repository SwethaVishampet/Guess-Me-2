/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package task2;

import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author vgswetha92
 */
public class Task2 {

    
     long twoMinute = 15*1000;
     long sixMinute = 1*60*1000;
     boolean twoUp = false;
     boolean sixUp = false;
        
     Timer timer1 = new Timer();
     Timer timer2 = new Timer();
     
     ServerSocket serverSock;
     Socket s ;
     
     FileInputStream fis;
     String filename= "words.txt";
     String HTMLString;
     String TimeUP = "";
     String fullword="";
     String wordToSend="";
     String Result="";
     String message;
     int score = 0;
     String guess= "";
     String puzzle="";
     BufferedReader br;
     List<String> words;
    
     int firstPuzzle =0;
     int startGame=0;
     boolean valid = true;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        Task2 server = new Task2();	
        server.start();
    }
    
   
    
    public void start()  throws IOException
  {  
        serverSock  = new ServerSocket(7000);
        System.out.println("SERVER IS WAITING FOR HTTP REQUEST at PORT 7000...");
        //Read from file and store in a list
        ReadFile();            
      	while (true) 
	{
            //Listen & Accept Connection and Create new CONNECTION SOCKET
            s = serverSock.accept();
            System.out.println("connection established from " + s.getInetAddress());
            System.out.println("Connection Definition " + s.toString());


            // The next 3 lines create a buffer reader that
            // reads from the socket s.
            InputStream is = s.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            // The next 2 lines create a output stream we can
            // write to.
            OutputStream os = s.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);

            // Read HTTP request (empty line signal end of request)
            String input = br.readLine();
            System.out.println(input);
            valid = true;
            if(input==null)
            {
                 valid = false;
            }
            if(valid)
            {   
                if(startGame==0)
                {
                     timer1.schedule(new sixUp(), sixMinute);
                     startGame++;
                }
          
           
                StringTokenizer st = new StringTokenizer(input);
                 if (st.nextToken().equals("GET"))
                {
                    System.out.println("It is a GET request");
                    String next = st.nextToken();

                    int index2= next.indexOf("/");
                    if(index2 == -1)
                    {
                        valid = false;
                    }
                    int index1= next.indexOf("=");
                    //when game is over
                    if(sixUp) 
                    {
                         HTMLString = "<html>\n" +
                                        "<body>\n" +
                                        "\n" +
                                        "<h1>Welcome to GuessMe!</h1>\n" +
                                        "<h2> GAME OVER!!!  </h2>\n" +
                                        "<p>Your score is:" + Integer.toString(score) +" </p>\n" +
                                        "<p> Thanks for playing </p> \n" +
                                        "</body>\n" +
                                        "</html>" ; 
                    }
                    //game is running withing six minutes
                    else
                    {
                        // if its a guess from the client
                        if(index1 != -1)
                        {
                         guess =next.substring(index1+1,(next.length())); 
                         System.out.println("Client's guess is: "+ guess);
                        
                         //two mintues not up yet
                        if(twoUp== false)
                        {
                            System.out.println("two mintues not up yet");
                            TimeUP = "Two minutes to guess the word starts now";

                            //correct guess
                            if(guess.toLowerCase().compareTo(fullword.toLowerCase())==0)
                               {  
                                    System.out.println("Correct guess");
                                   Result= "<p>Congratulations! Correct guess </p>\n";
                                    score++;
                                }
                             //incorrect guess
                            else
                               {   
                                   System.out.println("Incorrect guess");
                                   Result= "<p> Incorrect guess </p> \n <p> Correct answer was:"+ fullword + "</p> \n";
                                   score-=2;

                               }
                           }
                                 
                            //two minutes is up
                            else
                            {   
                                System.out.println("two minutes up");
                               Result= "";
                               TimeUP = "TIME-UP!!!\n Next puzzle:- ";

                           }
                        }// end of client's guess page
                      MakePuzzle();
                             
                    puzzle = "<p>Your puzzle is:" + wordToSend +  "</p>\n";

                      HTMLString = "<html>\n" +
                                    "<body>\n" +
                                    "\n" +
                                    "<h1>Welcome to GuessMe!</h1>\n" +
                                    Result +
                                    "<p>Your score is:" + Integer.toString(score) +" </p>\n" +
                                    "<p>"+ TimeUP + "</p>\n"+
                                    puzzle +
                                    "\n" +
                                    "<form name=\"input\" method=\"get\">\n" +
                                    "Enter your guess: <input type=\"text\" name=\"guess\" value=\"\"><br>\n" +
                                    "<input type=\"submit\" value=\"Submit\">\n" +
                                    "</form> \n" +
                                    "\n" +
                                    "\n" +
                                    "</body>\n" +
                                    "</html>" ;  
                     } //end of running inside game. six minutes not over yet
                           
		} //end of get request
                        
                // read and throw away the rest of the HTTP request
                while (input.compareTo("") != 0) 
                {
                        input = br.readLine();  //Just read and ignore
                }
                try{
                    if(valid)
                    {// Now, write buffer to client
                        // (but, send HTTP response header first)
                        dos.writeBytes("HTTP/1.0 200 Okie \r\n");
                        dos.writeBytes("Content-type: text/html\r\n");
                        dos.writeBytes("\r\n");
                        dos.writeBytes(HTMLString);
                        System.out.println("Displaying page");
                    }
                    else
                    {
			//Reply with 404 error.
                        dos.writeBytes("HTTP/1.0 404 Not Found\r\n");
                        dos.writeBytes("\r\n");
                        dos.writeBytes("Cannot find page");
                     }
       
                 }
                catch (Exception ex){
			}

                }
        }
  }
    
    
     class sixUp extends TimerTask
  {
        public void run() 
        {
              System.out.println("\nSix minutes up");
             sixUp = true;
             timer1.cancel();
        }
    }
     
     class twoUp extends TimerTask
  {
        public void run() 
        {   
            System.out.println("\nTwo mintues up");
             twoUp = true;   
        }
        
    }
     
     //choose random word from list and make puzzle
      public void MakePuzzle()
     {
         firstPuzzle++;
         System.out.println("Choosing random word");
         // Choose a random one from the list
        Random r = new Random();
        String randomString = words.get(r.nextInt(words.size()));
        fullword= randomString;
        
        if(fullword.compareTo("")==0)
                fullword = "Computer";
         System.out.println("Fullword is" + fullword);
         
          System.out.println("\nMaking puzzle");
         // logic to send only 20% of the word
         Integer num= fullword.length();
         System.out.println("length of fullword: "+ num.toString());
         Double d = 0.8*num;
         Integer N = d.intValue();
         System.out.println("going to cut:" +N.toString());
        
         Random rand = new Random();
        List<Integer> S = new ArrayList<Integer>(num);
         for (int i = 0; i < num; i++) 
            S.add(i);
         
         List<Integer> combination = new ArrayList<Integer>(N);
         Collections.shuffle(S, rand);
         combination.addAll(S.subList(0, N));
         
         char[] charArray = fullword.toCharArray();
         
         for(int i = 0; i < N; i++)
         {  int pos = combination.get(i);
             charArray[pos] = '_';
         }
         
         String str = String.valueOf(charArray);
         wordToSend = str.toLowerCase();
         wordToSend = wordToSend.replace("_", "_ ");
         System.out.println("Word to send=" + wordToSend);
         
         if(firstPuzzle==1)
         {timer2.schedule(new twoUp(), twoMinute);
         System.out.println("Starting timer");
            }      
         else
         {
               timer2.cancel();
              System.out.println("Cancelling timer2");
                for(int i=0;i<50;i++)
            {

            }
                
          timer2= new Timer();
          timer2 .schedule(new twoUp(), twoMinute);
          twoUp = false; 
         System.out.println("Starting timer");
         }
     
     }
      
      //read words from file and store in a list
 public void ReadFile()
 {
     System.out.println("Reading file");
      BufferedReader brf;
      
      URL url = getClass().getResource(filename);
      filename= url.getPath();
       
     	try{
            
            // Open and read the file into buffer
            File f = new File(filename);

            if (f.canRead())
            {       System.out.println("Can read file");
              
            //Create a File InputStrem to read the File
                    FileInputStream fis = new FileInputStream(filename);
                    brf = new BufferedReader(new InputStreamReader(fis));

                    // Read in the file into a list of strings
                    words = new ArrayList<String>();

                    String line = brf.readLine();

                    while( line != null ) {
                        words.add(line);
                        System.out.println(line);
                        line = brf.readLine();
                        
                    }
            }

         }
         catch (Exception ex){
             System.out.println("Cannot read file");
         }
 }

}
