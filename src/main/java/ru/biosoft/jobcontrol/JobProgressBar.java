package ru.biosoft.jobcontrol;

import javax.swing.JProgressBar;

@SuppressWarnings ( "serial" )
public class JobProgressBar extends JProgressBar implements JobControlListener
{
    public JobProgressBar()
    {
        setMinimum(0);
        setMaximum(100);
    }
    
    public void valueChanged(JobControlEvent evt)
    {
        setValue(evt.getPreparedness());
    }

    /**
     * Sets up preparedness value equal to 100.
     */
    public void resultsReady(JobControlEvent evt)
    {
    	setValue(100);
    }
    
    /**
     * Sets up preparedness value equal to 0.
     */
    public void jobTerminated( JobControlEvent evt )
    {
      	setValue(0);
    }

    /**
     * Sets up preparedness value equal to 0.
     */
    public void jobStarted( JobControlEvent evt )
    {
      	setValue(0);
    }

    /** Do nothing. */
    public void jobResumed( JobControlEvent evt ) {}
    
    /** Do nothing. */
    public void jobPaused( JobControlEvent evt ) {}
}