import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventRegistration 
{

    //Method for inserting data into the event database and gain the eventID key
    public void registerEvent(int studentID, int numOfGuests, int programID) throws SQLException
    {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventDB?user=root", "root", "milko.1-2002")) 
        {
            String query = "INSERT INTO REGISTEREVENTS (number_of_guests, student_id, program_id) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query); 
            statement.setInt(1, numOfGuests);
            statement.setInt(2, studentID);
            statement.setInt(3, programID);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) 
            {
                throw new SQLException("Event registration failed, no rows affected.");
            }
        }
    }

    //Method for adding a guest to an event
    public void addGuest(int programID, String guestName) throws SQLException 
    {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventDB?user=root", "root", "milko.1-2002")) 
        {
            String query = "INSERT INTO GUESTS (guest_name, program_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, guestName);
            statement.setInt(2, programID);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) 
            {
                System.out.println("Guest " + guestName + " added successfully.");
            } else 
            {
                System.out.println("Failed to add guest " + guestName + ".");
            }
        }
    }

    //Method to get the eventID
    public int getEventID(int studentID, int programID) throws SQLException 
    {
        int eventID = 0;
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventDB?user=root", "root", "milko.1-2002")) 
        {
            String query = "SELECT event_id FROM REGISTEREVENTS WHERE student_id = ? AND program_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, studentID);
            statement.setInt(2, programID);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) 
            {
                eventID = resultSet.getInt("event_id");
            }
        }
        
        return eventID;
    }

    //method to retrieve the name of the program
     public String getProgramName(int programID) throws SQLException 
     {
        String programName = null;
        String query = "SELECT program_name FROM universityDB.STUDY_PROGRAMS WHERE program_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/universityDB?user=root", "root", "milko.1-2002");
             PreparedStatement statement = connection.prepareStatement(query)) 
        {
            statement.setInt(1, programID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) 
            {
                programName = resultSet.getString("program_name");
            }
        }

        return programName;
    }

    //method to retrieve the name of the program responsible
     public String getProgramResponsible(int programID) throws SQLException 
     {
        String programResponsible = null;
        String query = "SELECT program_responsible FROM universityDB.STUDY_PROGRAMS WHERE program_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/universityDB?user=root", "root", "milko.1-2002");
            PreparedStatement statement = connection.prepareStatement(query)) 
        {
            statement.setInt(1, programID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) 
            {
                programResponsible = resultSet.getString("program_responsible");
            }
        }

        return programResponsible;
    }
    
    //method to retrieve the number of guests for an event
    public int getRegisteredStudents(int programID) throws SQLException 
    {
        int registeredStudents = 0;
        String query = "SELECT COUNT(*) AS num_students FROM eventDB.REGISTEREVENTS WHERE program_id = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventDB?user=root", "root", "milko.1-2002");
            PreparedStatement statement = connection.prepareStatement(query)) 
        {
            statement.setInt(1, programID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                registeredStudents = resultSet.getInt("num_students");
            }
        }

        return registeredStudents;
    }

    //method to calculate the duration of the program
    private int getProgramDuration(int programID) throws SQLException 
    {
        int programDuration = 1; // Start with 1 minute for Program Responsible speech

        int registeredStudents = getRegisteredStudents(programID);
        programDuration += registeredStudents / 5; // 1 minute for every 5 registered students

        return programDuration;
    }

    //method to check if the student that is signed in, is a registered student in that program
    private boolean isStudent(int studentID, int programID) throws SQLException 
    {
        String query = "SELECT COUNT(*) AS count FROM universityDB.STUDENTS s " +
                    "INNER JOIN eventDB.REGISTEREVENTS re ON s.student_id = re.student_id " +
                    "WHERE s.student_id = ? AND re.program_id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/universityDB?user=root", "root", "milko.1-2002");
            PreparedStatement statement = connection.prepareStatement(query)) 
        {
            statement.setInt(1, studentID);
            statement.setInt(2, programID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) 
            {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        }
        return false;
}

    //Method to adjust the number of guests for an event
    public void adjustEvent(int programID, int numOfAdditionalGuests, int studentID) throws SQLException
    {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventDB?user=root", "root", "milko.1-2002")) 
        {
            String updateQuery = "UPDATE REGISTEREVENTS SET number_of_guests = number_of_guests + ? WHERE program_id = ? AND student_id = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setInt(1, numOfAdditionalGuests);
            updateStatement.setInt(2, programID);
            updateStatement.setInt(3, studentID);
            updateStatement.executeUpdate();

            System.out.println("The event was adjusted successfully!");
        }
    }
    
    //Method to delete an event
    public void deleteEvent(int programID, int studentID) throws SQLException 
    {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventDB?user=root", "root", "milko.1-2002")) 
        {
            // Delete the associated guests first because of the foreign key constraint
            String deleteGuestsQuery = "DELETE FROM GUESTS WHERE program_id = ?";
            PreparedStatement deleteGuestsStatement = connection.prepareStatement(deleteGuestsQuery);
            deleteGuestsStatement.setInt(1, programID);
            deleteGuestsStatement.executeUpdate();
            
            // Delete the event 
            String deleteQuery = "DELETE FROM REGISTEREVENTS WHERE program_id = ? AND student_id =?";
            PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, programID);
            deleteStatement.setInt(2, studentID);
            deleteStatement.executeUpdate();

            System.out.println("Event deleted successfully.");
        }
    }

    //Method to get a list of all the participants
    public void getAllParticipants() throws SQLException 
    {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventDB?user=root", "root", "milko.1-2002")) 
        {
            String studentsQuery = "SELECT DISTINCT CONCAT(s.student_name, ' (Student)') AS participant, g.guest_name AS guest " +
                    "FROM eventDB.REGISTEREVENTS r " +
                    "JOIN universityDB.STUDENTS s ON r.student_id = s.student_id " +
                    "LEFT JOIN eventDB.GUESTS g ON r.program_id = g.program_id";

            String programResponsiblesQuery = "SELECT CONCAT(p.program_responsible, ' (Program Responsible)') AS participant, NULL AS guest " +
                    "FROM universityDB.STUDY_PROGRAMS p " +
                    "JOIN universityDB.TEACHERS t ON p.program_id = t.program_id";

            String teachersQuery = "SELECT CONCAT(t.teacher_name, ' (Teacher)') AS participant, NULL AS guest " +
                    "FROM universityDB.TEACHERS t";

            PreparedStatement studentsStatement = connection.prepareStatement(studentsQuery);
            ResultSet studentsResult = studentsStatement.executeQuery();

            PreparedStatement programResponsiblesStatement = connection.prepareStatement(programResponsiblesQuery);
            ResultSet programResponsiblesResult = programResponsiblesStatement.executeQuery();

            PreparedStatement teachersStatement = connection.prepareStatement(teachersQuery);
            ResultSet teachersResult = teachersStatement.executeQuery();

            while (studentsResult.next()) 
            {
                String participant = studentsResult.getString("participant");
                String guest = studentsResult.getString("guest");

                if (guest != null) 
                {
                    System.out.println(participant + " (Guest: " + guest + ")");
                } 
                else 
                {
                    System.out.println(participant);
                }
            }

            while (programResponsiblesResult.next()) 
            {
                String participant = programResponsiblesResult.getString("participant");
                System.out.println(participant);
            }

            while (teachersResult.next()) 
            {
                String participant = teachersResult.getString("participant");
                System.out.println(participant);
            }
        }
    }

    //Method to get a list of all the students participants
    public void getStudentsParticipants() throws SQLException 
    {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventDB?user=root", "root", "milko.1-2002")) 
        {
            String query = "SELECT s.student_id, s.student_name FROM universityDB.Students s JOIN eventDB.REGISTEREVENTS re ON s.student_id = re.student_id";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) 
            {
                int studentId = resultSet.getInt("student_id");
                String studentName = resultSet.getString("student_name");

                System.out.println("Student ID: " + studentId + ", Name: " + studentName);
            }
        } 
    }
    
    //method to get a list of all the participants of your program
    public void getParticipantsOfYourProgram(int programID) throws SQLException 
    {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/universityDB?user=root", "root", "milko.1-2002")) 
        {
            String query = "SELECT student_name FROM universityDB.Students WHERE program_id = ? AND student_id IN (SELECT student_id FROM eventDB.RegisterEvents)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, programID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) 
            {
                String studentName = resultSet.getString("student_name");
                System.out.println("Student Name: " + studentName);
            }
        }
    }
    
    //method to check if the student is in the same event registration
    public boolean checkSameEventRegistration(String studentName, int programID) throws SQLException 
    {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventDB?user=root", "root", "milko.1-2002")) 
        {
            String query = "SELECT COUNT(*) AS count FROM universityDB.Students JOIN eventDB.RegisterEvents ON Students.student_id = RegisterEvents.student_id WHERE Students.student_name = ? AND RegisterEvents.program_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, studentName);
            statement.setInt(2, programID);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) 
            {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        }
        return false;
    }

    //method to print the ceremony program
    public void printCeremonyProgram(int studentID, int programId) throws SQLException 
    {
        System.out.println("Ceremony Program:");
        System.out.println("Introduction: 30 minutes");

        String query = "SELECT program_id, program_name, program_responsible FROM universityDB.STUDY_PROGRAMS";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/universityDB?user=root", "root", "milko.1-2002");
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) 
        {
            while (resultSet.next()) 
            {
                int programID = resultSet.getInt("program_id");
                String programName = resultSet.getString("program_name");
                String programResponsible = resultSet.getString("program_responsible");

                int duration = getProgramDuration(programID);
                System.out.println("Study Program: " + programName);
                System.out.println("Program Responsible: " + programResponsible);
                System.out.println("Total Duration: " + duration + " minutes");

                // Check if the user is a student
                if (isStudent(studentID, programID))  
                {
                    System.out.println("You are a student in this program.");
                }

                System.out.println();
                
            }
        }
    }
}
