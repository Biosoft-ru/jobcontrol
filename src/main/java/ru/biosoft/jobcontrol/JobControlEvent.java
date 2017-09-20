package ru.biosoft.jobcontrol;

/**
* An event which indicates that a  action occurred in JobControl.
* This event is used for listeners of JobControl.
* 
* @see JobControlListener
* @see JobControl
*/

public class JobControlEvent
{
    protected JobControl jobControl = null;
    protected String message;
    protected Object[] results;
    protected JobControlException ex;

    /**
     * Constructs JobControlEvent with the specified jobControl and message.
     *
     * @param jobControl specified jobControl
     * @param message specified message
     */
    public JobControlEvent(JobControl jobControl, String message)
    {
        this.jobControl = jobControl;
        this.message = message;
    }

    /**
     * Constructs JobControlEvent with the specified jobControl and exception.
     *
     * @param jobControl specified jobControl
     * @param ex exception
     */
    public JobControlEvent(JobControl jobControl, JobControlException ex)
    {
        this.jobControl = jobControl;
        this.message = ex.getMessage();
        this.ex = ex;
    }

    /**
     * Constructs  JobControlEvent with specified jobControl and list of results.
     *
     * @param jobControl specified jobControl
     * @param resultPaths list of paths pointing to result items
     */
    public JobControlEvent(JobControl jobControl, Object[] resultPaths)
    {
        this.jobControl = jobControl;
        this.message = "results";
        this.results = resultPaths;
    }

    /**
     * Constructs  JobControlEvent with specified jobControl
     *
     * @param jobControl specified jobControl
     */
    public JobControlEvent(JobControl jobControl)
    {
        this.jobControl = jobControl;
        //this(jobControl,  null);
    }

    /**
     * Constructs  JobControlEvent with specified  message.
     *
     * @param msg specified  message
     */
    public JobControlEvent(String msg)
    {
        this(null, msg);
    }


    /**
     * Gets connected with this event JobControl.
     *
     * @return connected with this event JobControl
     */
    public JobControl getJobControl()
    {
        return jobControl;
    }

    /**
     * Returns preparedness of job.
     *
     * @return percent of preparedness
     */
    public int getPreparedness()
    {
        if( jobControl != null )
            return jobControl.getPreparedness();

        return 0;

    }

    /**
     * Gets message corresponded with this event
     *
     * @return message corresponded with this event
     */
    public String getMessage()
    {
        if( message == null && jobControl != null )
            return jobControl.getTextStatus();

        return message;
    }

    public JobControlException getException()
    {
        return ex;
    }

    /**
     * Returns array of paths pointing to results if they are ready.
     */
    public Object[] getResults()
    {
        return results;
    }

    /**
     * Gets status of job corresponded with this event.
     *
     * @return status of job corresponded with this event
     */
    public int getStatus()
    {
        if( jobControl == null )
            return -1;

        return jobControl.getStatus();
    }
}
