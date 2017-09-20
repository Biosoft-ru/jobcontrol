package ru.biosoft.jobcontrol;

/**
 * JobControlListenerAdapter is stub implementation of {@link JobControlListener} interface.
 */
public class JobControlListenerAdapter implements JobControlListener
{
    /**
     * Invoked when preparedness of job is changed.
     * 
     * Use  <code>event.getPreparedness()</code> for getting percent of job preparedness.
     * 
     * @see JobControlEvent#getPreparedness()
     * @param event corresponding event
     */
    @Override
    public void valueChanged(JobControlEvent event){}
    
    /**
     * Invoked when job is started for the first time.
     * 
     * @param event corresponding event
     */
    @Override
    public void jobStarted(JobControlEvent event){}
                                              
    /**
     * Invoked when job is terminated.
     * 
     * Use <code>event.getStatus()</code> for getting cause of termination.
     * 
     * @see JobControlEvent#getStatus()
     * @param event corresponding event
     */
    @Override
    public void jobTerminated(JobControlEvent event){}
    
    /**
     * Invoked when job process is paused.
     * 
     * @param event corresponding event
     */
    @Override
    public void jobPaused(JobControlEvent event){}
    
    /**
     * Invoked when job process is resumed after termination.
     * 
     * @param event corresponding event
     */
    @Override
    public void jobResumed(JobControlEvent event){}

    @Override
    public void resultsReady(JobControlEvent event){}
}
