package compal.logic.command;

import compal.logic.command.exceptions.CommandException;
import compal.model.tasks.Task;
import compal.model.tasks.TaskList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FindFreeSlotCommand extends Command {

    private static final String MESSAGE_UNABLE_TO_EXECUTE = "Unable to execute command!";

    private Date date;
    private int hour;
    private int min;

    /**
     * Constructs FindFreeSlotCommand object.
     *
     * @param date input date
     * @param hour input hour
     * @param min input minute
     */
    public FindFreeSlotCommand(Date date, int hour, int min) {
        this.date = date;
        this.hour = hour;
        this.min = min;
    }

    /**
     * Returns a list of free time slots available on the input date with the input hour and min.
     *
     * @param taskList List of all tasks
     * @return list of free time slots
     * @throws CommandException if command cannot be executed
     */
    @Override
    public CommandResult commandExecute(TaskList taskList) throws CommandException {

        ArrayList<Task> arrayList = new ArrayList<>();
        Date startPointer;
        Date oneDayAfter;

        Calendar calendar = Calendar.getInstance();
        Date currentDateAndTime = calendar.getTime();

        for (Task task : taskList.getArrList()) {
            if (task.getDate().equals(date) && !task.getStringEndTime().equals("-")
                    && !task.getStringStartTime().equals("-") && task.getEndTime().after(currentDateAndTime)) {
                arrayList.add(task);
            }
        }

        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date currentDate = calendar.getTime();

        if (isEqualDate(date, currentDate)) {
            if (arrayList.get(0).getEndTime().after(currentDateAndTime)) {
                startPointer = arrayList.get(0).getEndTime();
            } else {
                startPointer = currentDateAndTime;
            }
            calendar.add(Calendar.DATE, 1);
            oneDayAfter = calendar.getTime();
        } else {
            startPointer = date;
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            oneDayAfter = calendar.getTime();
        }

        ArrayList<String> finalList;
        int duration = hour * 60 + min;

        finalList = getFreeSlots(arrayList, startPointer, oneDayAfter, duration);
        String result = printResult(finalList);

        return new CommandResult(result, false);
    }

    /**
     * Returns a String array of free time slots with input duration.
     *
     * @param arrayList list of tasks on input date
     * @param startPointer start time
     * @param endDay date of next day
     * @param duration duration of time slot needed
     * @return
     */
    public ArrayList<String> getFreeSlots(ArrayList<Task> arrayList, Date startPointer, Date endDay, int duration) {
        Date endPointer;
        ArrayList<String> stringArrayList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm");

        if (arrayList.isEmpty()) {
            return stringArrayList;
        }

        for (int i = 0; i < arrayList.size(); i++) {
            endPointer = arrayList.get(i).getStartTime();
            if ((endPointer.getTime() - startPointer.getTime()) >= duration) {
                String start = simpleDateFormat.format(startPointer);
                String end = simpleDateFormat.format(endPointer);
                stringArrayList.add(start + " to " + end + "\n");
            }
            startPointer = arrayList.get(i).getEndTime();
        }

        if ((endDay.getTime() - startPointer.getTime()) >= duration) {
            String start = simpleDateFormat.format(startPointer);
            stringArrayList.add(start + " to 2400\n");
        }

        return stringArrayList;
    }

    /**
     * Returns a String to be printed as the result.
     *
     * @param arrayList list of available time slots
     * @return result String
     */
    public String printResult(ArrayList<String> arrayList) {
        StringBuilder finalList = new StringBuilder();

        if (arrayList.isEmpty()) {
            return ("You are free for the entire day!");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate = simpleDateFormat.format(date);

        finalList.append("Here are the available time slots for " + stringDate + " :\n");
        for (int i = 1; i <= arrayList.size(); i++) {
            finalList.append(i + ". " + arrayList.get(i - 1));
        }

        return finalList.toString();
    }

    /**
     * Returns true if the dates are equal, returns false otherwise.
     *
     * @param date1 First date input
     * @param date2 Second date input
     * @return true of dates are equal, false otherwise
     */
    public boolean isEqualDate(Date date1, Date date2) {
        return date1.equals(date2);
    }

    /**
     * Converts milliseconds to minutes. Returns number of minutes.
     *
     * @param milliseconds Number of milliseconds
     * @return Number of minutes
     */
    public long convertMsToMin(long milliseconds) {
        return milliseconds / 60000;
    }
}
