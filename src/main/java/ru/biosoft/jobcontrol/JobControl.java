package ru.biosoft.jobcontrol;

/**
 * JobControl is the special interface for providing of any control.
 */
public interface JobControl extends Runnable
{
    /** Instance has been created but not yet executed. */
    public static final int CREATED = 0;

    /** The analysis instance is run. */
    public static final int RUNNING = 1;

    /** The analysis instance is paused. */
    public static final int PAUSED = 2;

    /** The instance has completed execution. */
    public static final int COMPLETED = 3;

    /** The instance was terminated by user request. */
    int TERMINATED_BY_REQUEST = 4;

    /** The instance terminated due to an error. */
    public static final int TERMINATED_BY_ERROR = 5;

    ///////////////////////////////////////////////////////////////////////////
    // Control methods
    //

    /**
     * Pauses JobControl process.
     */
    public void pause();
    
    /**
     * Resumes JobControl process.
     */
    
    public void resume();
    
    /**
     * Terminates JobControl process.
     */
    public void terminate();

    ///////////////////////////////////////////////////////////////////////////
    // Info methods
    //

    /**
     * Returns status of the job.
     *
     * @return one of following values:
     *            <ul>
     *              <li> {@link #CREATED}               </li>
     *              <li> {@link #RUNNING}               </li>
     *              <li> {@link #PAUSED}                </li>
     *              <li> {@link #COMPLETED}             </li>
     *              <li> {@link #TERMINATED_BY_REQUEST} </li>
     *              <li> {@link #TERMINATED_BY_ERROR}   </li>
     *            </ul>
     */
    public int getStatus();

    /**
     * Returns string representation of the job status.
     */
    public String getTextStatus();

    /**
     * Returns preparedness of the job in percent.
     *
     * @return  percent value
     */
    public int getPreparedness();

    /**
     * Returns JobControl created time.
     *
     * @return unix format time
     */
    public long getCreatedTime();

    /**
     * Returns estimated remained time.
     *
     * @return unix format time
     */
    public long getRemainedTime();
    
    /**
     * Returns elapsed time of Job.
     * 
     * @return unix format time
     */
    public long getElapsedTime();

    /**
     * Returns the job start time.
     * 
     * @return unix format time
     */
    public long getStartedTime();

    /**
     * Returns the job finished time.
     * 
     * @return unix format time
     */
    public long getEndedTime();

    /**
     * Adds JobControlListener object.
     *
     * @param listener added listener
     */
    public void addListener(JobControlListener listener);

    /**
     * Adds JobControlListener object
     *
     * @param listener added listener
     */
    public void removeListener(JobControlListener listener);

    /**
     * Sets value of job Preparedness
     * @param percent
     */
    public void setPreparedness(int percent);

}
