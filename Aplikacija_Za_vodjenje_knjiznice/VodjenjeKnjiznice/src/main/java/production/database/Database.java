package production.database;

import production.model.Authors;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {
    public static Boolean activeConnectionWithDatabase = Boolean.FALSE;

    public synchronized static Connection connectToDatabase() throws SQLException, IOException {
        Properties configuration = new Properties();
        configuration.load(new FileReader("dat/database.properties"));

        String databaseURL = configuration.getProperty("databaseUrl");
        String databaseUsername = configuration.getProperty("databaseUsername");
        String databasePassword = configuration.getProperty("databasePassword");


        return DriverManager
                .getConnection(databaseURL, databaseUsername, databasePassword);
    }
// naredbe za autora
    public static List<Authors> getAllAuthorsFromDatabase(Connection veza) throws SQLException{
        List<Authors> authorsList = new ArrayList<>();

        Statement sqlStatement = veza.createStatement();

        ResultSet authorResultSet = sqlStatement.executeQuery("SELECT  * FROM AUTORI");

        while(authorResultSet.next()){
            Authors newCategory = getAuthorFromResultSet(authorResultSet);
            authorsList.add(newCategory);
        }

        return(authorsList);
    }
    private static Authors getAuthorFromResultSet(ResultSet authorResultSet)throws SQLException{

        long authorId = authorResultSet.getLong("ID");
        String name = authorResultSet.getString("IME");
        String surname = authorResultSet.getString("PREZIME");
        LocalDate dateOfBirth = authorResultSet.getDate("DATUM_RODJENJA").toLocalDate();

        return new Authors(authorId, name, surname, dateOfBirth);
    }
    public static void insertNewAuthorToDatabase(Connection veza, Authors author) throws SQLException,IOException{
        PreparedStatement stmt = veza.prepareStatement(
                "INSERT INTO AUTORI (ID, IME, PREZIME, DATUM_RODJENJA) " +
                        "VALUES(?, ?, ?, ?)");
        stmt.setString(1, String.valueOf(author.getId()));
        stmt.setString(2,author.getName());
        stmt.setString(3,author.getSurname());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        stmt.setString(4,author.getDateOfBirth().format(formatter));
        stmt.executeUpdate();

    }
}
