import java.util.Calendar;
import java.util.GregorianCalendar;

public class GetDayOfWeek {
    GregorianCalendar newCal = new GregorianCalendar( );
    int day = newCal.get( Calendar.DAY_OF_WEEK );
    public int getDay()
    {
        return day;
    }
}
