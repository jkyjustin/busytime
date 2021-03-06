package MainApp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SQLParser {

    // For each row of the SQL database that meets the criteria given by input,
    // create Entry for the row and add it to an array list
    public static List<Entry> parseSQL(int startYear, int endYear, int startMonth, int endMonth,
                                       int startDay, int endDay, int startHour, int endHour) {
        List<Entry> entryList = null;
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/postgres",
                            "postgres", "adfadf12");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM ARRIVALS;" );

            entryList = new ArrayList<>();
            while ( rs.next() ) {
                int datey = rs.getInt("datey");
                int datem = rs.getInt("datem");
                int dated = rs.getInt("dated");
                int timeh = rs.getInt("timeh");
                int timem = rs.getInt("timem");
                int serviceTime = rs.getInt("servicetime");
                if (startYear!=0&&endYear!=0&&startMonth!=0&&endMonth!=0&&startHour!=0&&endHour!=0){
                    if (datey >= startYear && datey <= endYear
                            && datem >= startMonth && datem <= endMonth
                            && dated >= startDay && dated <= endDay
                            && timeh >= startHour && timeh <= endHour) {
                        entryList.add(new Entry(datey, datem, dated, timeh, timem, serviceTime));
                    }
                }
            }
            Collections.sort(entryList);  // sort our generated List<Entry> based on Entry.compareTo()
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        return entryList;
    }

    // Bin Lists of entries by specific date
    public List<List<Entry>> binEntryList(List<Entry> entryList) {
        List<Entry> entryListAcc = new ArrayList<>();
        List<List<Entry>> binList = new ArrayList<>();
        int dayInt = 0;

        for (Entry e: entryList) {
            if (e.getDated()!=dayInt) {
                if (entryListAcc.size()!=0) {
                    binList.add(entryListAcc);
                }
                entryListAcc = new ArrayList<>();
                dayInt = e.getDated();
                entryListAcc.add(e);
            }
            else if (e.getDated()==dayInt) {
                entryListAcc.add(e);
            }
            if (e.equals(entryList.get(entryList.size()-1))) {
                binList.add(entryListAcc);
            }

        }
        return binList;
    }
}