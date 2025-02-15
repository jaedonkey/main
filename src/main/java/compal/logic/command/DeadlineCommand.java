package compal.logic.command;

import compal.commons.CompalUtils;
import compal.commons.LogUtils;
import compal.logic.command.exceptions.CommandException;
import compal.model.tasks.Deadline;
import compal.model.tasks.Task;
import compal.model.tasks.TaskList;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

//@@author LTPZ
//@@author yueyeah

/**
 * Add a deadline type task.
 */
public class DeadlineCommand extends Command {

    public static final String MESSAGE_USAGE = "deadline\n\t"
            + "Format: deadline <description> /date <dd/mm/yyyy>... /end <hhhh> [/interval <num>] "
            + "[/priority <low|medium|high>] [/final-date <dd/mm/yyyy>]\n\n\t"
            + "Note: content in \"[]\": optional\n\t"
            + "You can switch the order of any two blocks (a block starts with \"/\" and ends by the next block)\n\t"
            + "content in \"<>\": need to be fulfilled by the user\n\t"
            + "content separated by \"|\": must choose exactly one from them\n\t"
            + "\"...\" means you can add multiple. e.g. dd/mm/yyyy... means you can add 01/01/2019 02/01/2019\n\t"
            + "dd/mm/yyyy is the date format. e.g. 01/01/2000\n\t"
            + "hhhh is the time format. e.g. 1740\n\n"
            + "This command will add a task which has a deadline date and time\n"
            + "Examples:\n\t"
            + "deadline cs2106as /date 01/01/2019 /end 1000\n\t\t"
            + "add a task which ends at 01/01/2019 10:00am with default priority low\n\t"
            + "deadline dinner /date 01/01/2019 02/01/2019 /end 1800 /final-date 10/01/2019\n\t\t"
            + "add a task which ends on 01/01/2019 and 02/01/2019 6pm and repeat weekly(default) until 10/01/2019\n\t"
            + "deadline diary /date 01/01/2019 /end 2359 /final-date 10/01/2019 /interval 1\n\t\t"
            + "add a task which ends on 01/01/2019 23:59pm and repeat daily until 10/01/2019\n\t"
            + "deadline cs2106as /date 01/01/2019 /end 1000 /priority high\n\t\t"
            + "dd a task which ends at 01/01/2019 10:00am with priority high";

    private static final String MESSAGE_SUCCESSFULLY_ADDED = "\nThe following deadline were added: \n";
    private static final String underscoreErrorMsg = "Descriptions should not have underscores!";
    private static final String MESSAGE_REPEATED_DEADLINE = "\nLooks like you already added the task before! \n"
            + "Use the edit command on the task ID given below!";
    private String description;
    private ArrayList<String> startDateList;
    private Task.Priority priority;
    private String endTime;
    private String finalDateString;
    private int interval;

    private static final Logger logger = LogUtils.getLogger(DeadlineCommand.class);

    /**
     * This is the constructor.
     *
     * @param description   description of deadline.
     * @param priority      priority of deadline.
     * @param startDateList date of deadline.
     * @param endTime       end time of deadline.
     */
    public DeadlineCommand(String description, Task.Priority priority, ArrayList<String> startDateList,
                           String endTime, String finalDateString, int interval) {
        this.description = description;
        this.priority = priority;
        this.startDateList = startDateList;
        this.endTime = endTime;
        this.finalDateString = finalDateString;
        this.interval = interval;
    }

    @Override
    public CommandResult commandExecute(TaskList taskList) throws CommandException {
        if (description.contains("_")) {
            throw new CommandException(underscoreErrorMsg);
        }
        logger.info("Executing deadline command");
        Date finalDate = CompalUtils.stringToDate(finalDateString);
        StringBuilder finalList = new StringBuilder();
        for (String startDateString : startDateList) {
            Date startDate = CompalUtils.stringToDate(startDateString);
            while (!startDate.after(finalDate)) {
                startDateString = CompalUtils.dateToString(startDate);
                boolean doesNotExist = true;
                for (Task task : taskList.getArrList()) {
                    if (task.getSymbol().equals("D")
                            && task.getStringMainDate().equals(startDateString)
                            && task.getStringEndTime().equals(endTime)
                            && task.getDescription().equalsIgnoreCase(description)) {
                        finalList.append(MESSAGE_REPEATED_DEADLINE.concat(task.toString() + "\n"));
                        doesNotExist = false;
                        break;
                    }
                }

                if (doesNotExist) {
                    Deadline indivDeadline = new Deadline(description, priority, startDateString, endTime);
                    taskList.addTask(indivDeadline);
                    finalList.append(MESSAGE_SUCCESSFULLY_ADDED.concat(indivDeadline.toString() + "\n"));
                }

                startDate = CompalUtils.incrementDateByDays(startDate, interval);
            }
        }

        return new CommandResult(finalList.toString(), true);
    }
}
