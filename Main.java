import java.util.Scanner;

public class Main 
{
    //Menu for the student options after the sign-in process
    static int studentOptions()
    {
        Scanner in = new Scanner(System.in);
        int option = 0;

       while(option < 1 || option > 6)
       {
            System.out.println("1. Register for the event");
            System.out.println("2. See all participants");
            System.out.println("3. See all student participants");
            System.out.println("4. See participants from your program");
            System.out.println("5. Search for a participant");
            System.out.println("6. See overall program");
            System.out.println("7. Exit");

            option = in.nextInt();
       }

        return option;
    }

    static int updatedMenuOptions() 
    {
        Scanner in = new Scanner(System.in);
        int option = 0;

       while(option < 1 || option > 6)
       {
            System.out.println("Welcome, your options are below:");
            System.out.println("1. Edit registration for the event");
            System.out.println("2. See all participants");
            System.out.println("3. See all student participants");
            System.out.println("4. See participants from your program");
            System.out.println("5. Search for a participant");
            System.out.println("6. See overall program");
            System.out.println("7. Exit");

            option = in.nextInt();
       }

       return option;
    }

    public static void main(String[] args) throws Exception 
    {
        UserAuthentication auth = new UserAuthentication();
        EventRegistration event = new EventRegistration();
        
        Scanner in = new Scanner(System.in);
        
        int option =  0;
        while(option!= 1 && option!= 2 && option!= 3)
        {   
            System.out.println("Welcome! Your options are shown below:");
            System.out.println("1. Sign in");
            System.out.println("2. See overall program");
            System.out.println("3. Exit");

            option = in.nextInt();
        }

        switch(option)
        {
            case 1:
                auth.signIn(); //Calling the signIn method from the UserAuthentication class to sign in

                int option2 = studentOptions(); //handling the options of the student

                if(option2 == 1)
                {   
                    //Only 4 invites are allowed
                    int guests = 0;
                    do
                    {
                        System.out.println("Perfect! How many guests would you like to invite?(0 - 4)");
                        guests = in.nextInt();
                    }while(guests < 1 || guests > 4);

                    int studentID = auth.getStudentId();
                    int programID = auth.getProgramId();
                    event.registerEvent(studentID, guests, programID);

                    for(int i = 1; i <= guests; i++)
                    {
                        System.out.println("Give the name of the guest " + i + ":");
                        String name = in.next();
                        event.addGuest(programID, name);
                    }

                    int option3 = updatedMenuOptions();
                    
                    //options of the updated menu
                    if(option3 == 1)
                    {
                        int choice = 0;
                        do
                        {
                            System.out.println("1. Adjust event (Change number of guests)");
                            System.out.println("2. Delete event");
                            System.out.println("3. Exit");

                            choice = in.nextInt();
                        }while(choice < 1 || choice > 3);

                        //handling the choice of adjusting or deleting the event
                        if(choice == 1)
                        {
                            System.out.println("Enter the number of additional guests: ");
                            int additionalGuests = in.nextInt();
                            
                            for(int i = 1; i <= guests; i++)
                            {
                                System.out.println("Give the name of the additional guest " + i + ":");
                                String name = in.next();
                                event.addGuest(programID, name);
                            }

                            event.adjustEvent(programID, additionalGuests, studentID);
                        }
                        else if(choice == 2)
                        {
                            event.deleteEvent(programID, studentID);
                        }
                        else if(choice == 3)
                        {
                            System.out.println("Logging out...");
                            System.exit(0);
                        }
                    }
                    else if(option3 == 2)
                    {
                        event.getAllParticipants();
                    }
                    else if(option3 == 3)
                    {
                        event.getStudentsParticipants();
                    }
                    else if(option3 == 4)
                    {
                        int programId = auth.getProgramId();
                        event.getParticipantsOfYourProgram(programId);
                    }
                    else if(option3 == 5)
                    {
                        System.out.println("Give the name of the student you want to search: ");
                        String name = in.next();
                        
                        //Checking if the student is already registered for this event
                        boolean isRegistered = event.checkSameEventRegistration(name, auth.getProgramId()); 

                        if(isRegistered)
                        {
                            System.out.println("The student " + name + " is registered for the ceremony.");
                        }
                        else
                        {
                            System.out.println("The student " + name + " is not registered for the ceremony.");
                        }
                    }
                    else if(option3 == 6)
                    {
                        event.printCeremonyProgram(auth.getStudentId(), auth.getProgramId());
                    }
                    else if(option3 == 7)
                    {
                        System.out.println("Goodbye!");
                        System.exit(0);
                    }

                }
                else if(option2 == 2)
                {
                    event.getAllParticipants();
                }
                else if(option2 == 3)
                {
                    event.getStudentsParticipants();
                }
                else if(option2 == 4)
                {
                    int programID = auth.getProgramId();
                    event.getParticipantsOfYourProgram(programID);
                }
                else if(option2 == 5)
                {
                    System.out.println("Give the name of the student you want to search: ");
                    String name = in.next();
                    
                    //Checking if the student is already registered for this event
                    boolean isRegistered = event.checkSameEventRegistration(name, auth.getProgramId()); 

                    if(isRegistered)
                    {
                        System.out.println("The student " + name + " is registered for the ceremony.");
                    }
                    else
                    {
                        System.out.println("The student " + name + " is not registered for the ceremony.");
                    }
                }
                else if(option2 == 6)
                {
                    event.printCeremonyProgram(auth.getStudentId(), auth.getProgramId());
                }
                else if(option2 == 7)
                {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
            break;

            case 2:
                event.printCeremonyProgram(auth.getStudentId(), auth.getProgramId());

            break;

            case 3:
                System.out.println("Goodbye!");
                System.exit(0); //Exit the program
            
            break;
        }
    }
}
