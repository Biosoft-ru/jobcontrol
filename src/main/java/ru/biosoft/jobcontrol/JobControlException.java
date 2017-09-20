package ru.biosoft.jobcontrol;

/**
 * JobControlException is used jointly with JobControl objects.
 * 
 * This exception is occurred when job wants to terminate job process.
 *
 * @see JobControl
 */
@SuppressWarnings ( "serial" )
public class JobControlException extends Exception
{

    /**
     * Constructs JobControlException with specified status and message
     *
     * @param status specified status
     * @param msg  specified  message
     */
    public JobControlException(int status, String msg)
    {
        super(msg);
        this.status = status;
    }

    /**
     * Constructs JobControlException with specified status.
     *
     * @param status specified status
     */
    public JobControlException(int status)
    {
        this.status = status;
    }

    /**
     * Constructs JobControlException with specified exception.
     *
     * @param error exception that occurred during job process
     */
    public JobControlException(Throwable error)
    {
        status = JobControl.TERMINATED_BY_ERROR;
        this.error = error;
    }

    private int status;

    /**
     * Returns status of termination.
     *
     * @return {@link JobControl#TERMINATED_BY_ERROR} or {@link JobControl#TERMINATED_BY_REQUEST}
     */
    public int getStatus()
    {
        return status;
    }

    private Throwable error=null;

    /**
     * Returns occurred exception or <code>null</code>.
     *
     * @return occurred exception or <code>null</code>
     */
    public Throwable getError()
    {
        return error;
    }

    @Override
    public String getMessage()
    {
        if( this.error == null )
        {
            return super.getMessage();
        }
        else 
        {
            return this.error.getMessage();
        }
    }

}
