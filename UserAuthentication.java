import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserAuthentication 
{
    private String currentUser;
    private int studentID;
    private int programID;

    //method to sign in 
    public void signIn() 
    {
        Scanner scanner = new Scanner(System.in);
        boolean isAuthenticated = false;

        while(!isAuthenticated) 
        {
            System.out.print("Name: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            // Establish a connection to the database
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/universitydb?user=root", "root", "milko.1-2002"))
            {
                // Prepare the SQL query
                String query = "SELECT * FROM students WHERE student_name = ? AND student_password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);

                // Execute the query
                ResultSet resultSet = statement.executeQuery();

                // Check if a matching user is found
                if (resultSet.next()) 
                {
                    currentUser = username;
                    isAuthenticated = true;
                    studentID = resultSet.getInt("student_id"); // Retrieve the student ID
                    programID = resultSet.getInt("program_id"); // Retrieve the program ID
                    System.out.println("Signed in successfully!");
                } 
                else 
                {
                    System.out.println("Invalid username or password. Please try again.");
                }
            } 
            catch (SQLException e) 
            {
                System.out.println("An error occurred while connecting to the database: " + e.getMessage());
            }
        }
    }

    //method to sing out
    public void signOut() 
    {
        currentUser = null; //reset the current user
        studentID = 0; //reset the student ID
        programID = 0; //reset the program ID

        System.out.println("You signed out successfully!");
    }

    //getters
    public String getCurrentUser() 
    {
        return currentUser;
    }

    public int getStudentId()
    {
        return studentID;
    }

    public int getProgramId()
    {
        return programID;
    }
}
