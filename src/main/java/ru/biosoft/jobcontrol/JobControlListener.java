package ru.biosoft.jobcontrol;

/**
 * The listener interface for receiving job control events
 * (start, stop, pause,resume,changing of preparedness and terminate).
*/
public interface JobControlListener
{
    /**
     * Invoked when preparedness of job is changed.
     * 
     * Use  <code>event.getPreparedness()</code> for getting percent of job preparedness.
     *
     * @see JobControlEvent#getPreparedness()
     * @param event corresponding event
     */
    void valueChanged(JobControlEvent event);

    /**
     * Invoked when job is started for the first time.
     *
     * @param event corresponding event
     */
    void jobStarted (JobControlEvent event);

    /**
     * Invoked when job is terminated.
     * 
     * Use <code>event.getStatus()</code> for getting cause of termination.
     *
     * @see JobControlEvent#getStatus()
     * @param event corresponding event
     */
    void jobTerminated(JobControlEvent event);

    /**
     * Invoked when job process is paused.
     *
     * @param event corresponding event
     */
    void jobPaused(JobControlEvent event);

    /**
     * Invoked when job process is resumed after termination.
     * 
     * @param event corresponding event
     */
    void jobResumed(JobControlEvent event);

    /**
     * Invoked when part of results is ready
     * @param event corresponding event
     */
    void resultsReady (JobControlEvent event);
}
