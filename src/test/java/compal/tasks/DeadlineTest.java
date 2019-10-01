package compal.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static compal.tasks.Task.Priority.high;
import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class DeadlineTest {
    private String description = "Test content";
    private String date = "01/10/2019";
    private Deadline deadline;
    private Task.Priority priority = high;
    private String endTime = "1230";


    @BeforeEach
    public void setup() {
        deadline = new Deadline(description, priority, date, endTime);
    }

    @Test
    void getPriority() {
        assertEquals(priority, deadline.getPriority());
    }

    @Test
    void getStatusIcon() {
        assertEquals("\u2718", deadline.getStatusIcon());
    }

    @Test
    void getIsDone() {
        assertEquals("false", deadline.getisDone());
    }

    @Test
    void getSymbol() {
        assertEquals("D", deadline.getSymbol());
    }

    @Test
    void getDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals(d, deadline.getDate());
    }

    @Test
    void setDateTest() {
        Date d = deadline.getDate();
        deadline.setDate(date);
        assertEquals(d, deadline.getDate());
    }

    @Test
    void getStringdate() {
        assertEquals(date, deadline.getStringDate());
    }

    @Test
    void getStartTime() {
        assertNull(deadline.getStartTime());
    }

    @Test
    void getStringStartTime() {
        assertEquals("-", deadline.getStringStartTime());
    }

    @Test
    void getEndTime() {
        SimpleDateFormat format = new SimpleDateFormat("HHmm");
        Date d = null;
        try {
            d = format.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assertEquals(d, deadline.getEndTime());
    }

    @Test
    void setEndTimeTest() {
        Date d = deadline.getEndTime();
        deadline.setEndTime(endTime);
        assertEquals(d, deadline.getEndTime());
    }

    @Test
    void getStringEndTime() {
        assertEquals(endTime, deadline.getStringEndTime());
    }

    @Test
    void sethasReminder() {
        deadline.setHasReminder();
        assertEquals(true, deadline.hasReminder());
    }

    @Test
    void getDescription() {
        assertEquals(description, deadline.getDescription());
    }

    @Test
    void markAsDoneTest() {
        deadline.markAsDone();
        assertEquals(true, deadline.isDone);
    }

    @Test
    void toStringTest() {
        assertEquals("\n" + "[D]" + "[\u2718] " + description
                + " \nDate: " + date + " \nEnd Time: " + endTime
                + " \nPriority: " + priority + "\n***************", deadline.toString());
    }

    @Test
    void getAllDetailsTest() {
        StringBuilder list = new StringBuilder();
        list.append("D");
        list.append("_");
        list.append(description);
        list.append("_");
        list.append("false");
        list.append("_");
        list.append(priority.toString());
        list.append("_");
        list.append(date);
        list.append("_");
        list.append("-_");
        list.append(endTime);
        list.append("_");
        list.append(deadline.gethasReminder());
        assertEquals(list.toString(), deadline.getAllDetailsAsString());
    }
}
