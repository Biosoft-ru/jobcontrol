package ru.biosoft.jobcontrol;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AbstractJobControl is default implementation of JobControl interface.
 * 
 * It provides basic functionality of all methods of JobControl.
 */
abstract public class AbstractJobControl implements JobControl
{
    /** elapsed time when percent is 0 */
    private final static long MAXIMAL_TIME = 60 * 60 * 60 * 1000;

    /** The logger to print logs. */
    protected Logger log;

    protected Date startedDate;

    /**
     * Current status of JobControl
     */
    protected int status = CREATED;
    
    private volatile CountDownLatch latch;

    /**
     * Constructs JobControl with specified logger.
     *
     * @param log logger
     */
    public AbstractJobControl(Logger log)
    {
        this.log = log;
        createdDate = Calendar.getInstance().getTime();
    }

    /**
     * Constructs JobControl with specified Logger and JobControlListener.
     *
     * @param log logger
     * @param listener specified JobControlListener
     */
    public AbstractJobControl(Logger log, JobControlListener listener)
    {
        this(log);

        if( listener != null )
            addListener(listener);
        else
            log.log(Level.WARNING, "Listener is null");

    }

    /**
     * This method should implement derived classes for real controlled process running.
     *
     * @exception JobControlException If the process wants to stop the task,or error is occurred.
     */
    abstract protected void doRun() throws JobControlException;

    /**
     * Derived classes should call this method when they want to start controlled process.
     */
    public void begin()
    {
        begin(null);
    }

    /**
     *  Derived classes should call this method when they want to start controlled process
     *  with any message.
     *
     * @param msg is used for listeners notification
     */
    protected void begin(String msg)
    {
        if( status == RUNNING )
            return;

        if( msg != null )
        {
        }

        reset();
        startedDate = Calendar.getInstance().getTime();
        isTerminated = false;
        status = RUNNING;

        fireJobStarted(msg);
        runStatus = COMPLETED;
    }

    /**
     * Sets variable runStatus to the current status of job control.
     * Call this method when changing of state is needed.
     * 
     * @param ex JobControlException sent exception
     */
    public void exceptionOccured(JobControlException ex)
    {
        if( log != null )
            log.log(Level.FINE, "exceptionOccurred() ", ex);

        runStatus = ex.getStatus();
    }

    /**
     * Returns whether status is terminated.
     * 
     * @return true if current status is one of
     *                   <ul>
     *                       <li>{@link #CREATED }                 </li>
     *                       <li>{@link #COMPLETED}                </li>
     *                       <li>{@link #TERMINATED_BY_REQUEST}    </li>
     *                       <li>{@link #TERMINATED_BY_ERROR }     </li>
     *                   </ul>
     *
     *
     */
    protected boolean isStatusTerminated()
    {
        return status == CREATED || status == COMPLETED || status == TERMINATED_BY_REQUEST || status == TERMINATED_BY_ERROR;
    }

    /**
     * Sets state defined by runStatus variable.
     * 
     * Notifies all listeners about state changing
     */
    public void end()
    {
        end((String)null);
    }
    
    /**
     * Sets state defined by runStatus variable.
     * Notifies all listeners about state changing using specified message.
     *
     * @param msg specified message
     */
    protected void end(String msg)
    {
        if( !isStatusTerminated() )
        {
            setTerminated(runStatus);
        }

        fireJobTerminated(msg, null);
        resetFlags();
    }

    public void end(JobControlException ex)
    {
        if( !isStatusTerminated() )
        {
            setTerminated(runStatus);
        }

        fireJobTerminated(null, ex);
        resetFlags();
    }

    // ////////////////////////////////////////////////////////////////////////
    
    /** Deferred current status of job control. */
    protected int runStatus;


    /** 
     * Starts the job control process. 
     */
    @Override
    public void run()
    {
        begin();
        JobControlException jcex = null;
        
        try
        {
            doRun();
        }
        catch( JobControlException ex )
        {
            exceptionOccured(ex);
            jcex = ex;
        }
        finally
        {
            end(jcex);
            
            if( log != null )
            	log.log(Level.FINE, "end called");
        }
    }

    /**
     * Sets status of job control to the {@link #PAUSED} state.
     */
    @Override
    public void pause()
    {
        if( log != null )
        	log.log(Level.FINE, "pause()");

        if( status == RUNNING )
        {
            latch = new CountDownLatch( 1 );
            isPaused = true;
            status = PAUSED;
            fireJobPaused();
        }
    }

    /**
     * Sets status of job control to the {@link #RUNNING} state.
     */
    @Override
    public void resume()
    {
        if( log != null )
        	log.log(Level.FINE, "resume()");

        if( status == PAUSED )
        {
            isPaused = false;
            status = RUNNING;
            fireJobResumed();
            CountDownLatch latch = this.latch;
            if(latch != null)
            {
                latch.countDown();
            }
            this.latch = null;
        }
    }

    /**
     * Sets status of job control to the {@link #TERMINATED_BY_REQUEST} state
     */
    @Override
    public void terminate()
    {
        if( log != null )
        	log.log(Level.FINE, "terminate()");

        if( status == RUNNING || status == PAUSED )
        {
            setTerminated(TERMINATED_BY_REQUEST);

            CountDownLatch latch = this.latch;
            if(latch != null)
            {
                latch.countDown();
            }
            this.latch = null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Info methods
    //

    @Override
    public int getStatus()
    {
        return status;
    }

    public static String getTextStatus(int status)
    {
        switch( status )
        {
            case CREATED:
                return "Created";
            case RUNNING:
                return "Running";
            case PAUSED:
                return "Paused";
            case COMPLETED:
                return "Completed";
            case TERMINATED_BY_REQUEST:
                return "Terminated by request";
            case TERMINATED_BY_ERROR:
                return "Terminated by error";
            default:
                return "Error";
        }
    }

    @Override
    public String getTextStatus()
    {
        return getTextStatus(getStatus());
    }

    protected int preparedness = 0;

    @Override
    public int getPreparedness()
    {
        return preparedness;
    }

    private final Date createdDate;

    @Override
    public long getCreatedTime()
    {
        return createdDate == null ? -1 : createdDate.getTime();
    }

    @Override
    public long getStartedTime()
    {
        if( startedDate == null )
            return -1;
        return startedDate.getTime();
    }

    private Date endedDate;

    @Override
    public long getEndedTime()
    {
        return endedDate == null ? -1 : endedDate.getTime();
    }

    @Override
    public long getElapsedTime()
    {
        long start = getStartedTime();
        long curTime = Calendar.getInstance().getTime().getTime();

        if( start == -1 )
            return 0;

        return curTime - start;
    }

    private long curTime = 0;

    @Override
    public long getRemainedTime()
    {
        int percent = getPreparedness();
        long start = getStartedTime();

        if( start == -1 )
            return getCreatedTime() + MAXIMAL_TIME;

        if( percent == 0 )
            return getStartedTime() + MAXIMAL_TIME;

        return start + 100 * ( curTime - start ) / percent - curTime;
    }

    ////////////////////////////////////////
    // Listener issues
    //

    private final Vector<JobControlListener> listeners = new Vector<>();

    @Override
    public void addListener(JobControlListener listener)
    {
        if( listener != null )
            listeners.addElement(listener);
    }

    @Override
    public void removeListener(JobControlListener listener)
    {
        if( listener != null )
            listeners.removeElement(listener);
    }

    /**
     * Notifies all listeners that job is started.
     *
     * @param msg notified message
     */
    protected void fireJobStarted(String msg)
    {
        JobControlEvent event = new JobControlEvent(this, msg);
        for( JobControlListener listener : listeners.toArray(new JobControlListener[listeners.size()]) )
        {
            listener.jobStarted(event);
        }
    }


    /**
     * Notifies all listeners that job is paused.
     *
     */
    protected void fireJobPaused()
    {
        JobControlEvent event = new JobControlEvent(this);
        for( JobControlListener listener : listeners.toArray(new JobControlListener[listeners.size()]) )
        {
            listener.jobPaused(event);
        }
    }


    /**
     * Notifies all listeners that job is resumed
     *
     */
    protected void fireJobResumed()
    {
        JobControlEvent event = new JobControlEvent(this);
        for( JobControlListener listener : listeners.toArray(new JobControlListener[listeners.size()]) )
        {
            listener.jobResumed(event);
        }
    }

    /**
     * Notifies all listeners that value returned by {@link #getPreparedness()} is changed
     *
     */
    public void fireValueChanged()
    {
        JobControlEvent event = new JobControlEvent(this);
        for( JobControlListener listener : listeners.toArray(new JobControlListener[listeners.size()]) )
        {
            listener.valueChanged(event);
        }
    }

    /**
     * Notifies all listeners that job is terminated.
     *
     * @param msg notified message
     */
    protected void fireJobTerminated(String msg)
    {
        fireJobTerminated(msg, null);
    }

    /**
     * Notifies all listeners that job is terminated.
     *
     * @param msg notified message
     * @param ex exception occurred if any
     */
    protected void fireJobTerminated(String msg, JobControlException ex)
    {
        JobControlEvent event = ex == null ? new JobControlEvent(this, msg) : new JobControlEvent(this, ex);

        for( JobControlListener listener : listeners.toArray(new JobControlListener[listeners.size()]) )
        {
            listener.jobTerminated(event);
        }
    }

    ////////////////////////////////////////
    // PENDING:
    //

    private boolean isPaused = false;

    /**
     * Returns <code>isPaused</code> private variable
     * @return <code>isPaused</code> variable
     */
    final private boolean isPaused()
    {
        return isPaused;
    }

    protected boolean isTerminated = false;

    /**
     * Returns <code>isTerminated</code> private variable
     * @return <code>isTerminated</code> private variable
     */
    final private boolean isTerminated()
    {
        return isTerminated;
    }

    /**
     * Sets status of JobControl
     *
     * @param status terminated status <br>
     *  {@link #TERMINATED_BY_ERROR},<br>{@link #TERMINATED_BY_REQUEST}
     */
    protected void setTerminated(int status)
    {
        this.status = status;
        isTerminated = true;
        endedDate = new Date();
    }

    /**
     * Sets value of job Preparedness
     *
     * @param percent percent of job Preparedness
     */
    @Override
    public void setPreparedness(int percent)
    {
        if( preparedness != percent )
        {
            curTime = Calendar.getInstance().getTime().getTime();

            preparedness = percent;

            fireValueChanged();
        }
    }

    public void resultsAreReady()
    {
        JobControlEvent event = new JobControlEvent(this, "results");
        for( JobControlListener listener : listeners.toArray(new JobControlListener[listeners.size()]) )
        {
            listener.resultsReady(event);
        }
        setCompleted();
    }

    public void resultsAreReady(Object[] results)
    {
        JobControlEvent event = new JobControlEvent(this, results);
        for( JobControlListener listener : listeners.toArray(new JobControlListener[listeners.size()]) )
        {
            listener.resultsReady(event);
        }
        if(status != TERMINATED_BY_REQUEST)
            setCompleted();
    }

    /**
     * Sets {@link #COMPLETED} state  of job status
     */
    protected void setCompleted()
    {
        status = COMPLETED;
        isTerminated = true;
    }


    /**
     * Sets status of job in {@link #CREATED} state
     */
    protected void reset()
    {
        status = CREATED;
        resetFlags();
    }


    /**
     * Sets isTerminated,isPaused variables to false
     */
    protected void resetFlags()
    {
        isTerminated = false;
        isPaused = false;
    }

    /**
     * Tests status of job control. If controlled process is terminated throws JobControlException with status TERMINATED_BY_REQUEST
     * Waits of resuming action, if isPaused variable is set.
     *
     * @exception JobControlException
     */
    public void checkStatus() throws JobControlException
    {
        // throw Exception if terminated
        if( isTerminated() )
        {
            fireJobTerminated("" + getTextStatus(TERMINATED_BY_REQUEST), null);
            throw new JobControlException(TERMINATED_BY_REQUEST);
        }

        if( isPaused() )
        {
            CountDownLatch latch = this.latch;
            if(latch != null)
            {
                while(true)
                {
                    try
                    {
                        latch.await();
                        break;
                    }
                    catch( InterruptedException e )
                    {
                        continue;
                    }
                }
            }
        }
    }


    /**
     * Converts time to the string
     * @param time Unix time
     * @return converted string
     */
    static public String format(long time)
    {
        return format(time, "HH:mm:ss");
    }

    /**
     * Converts time to the specified format string
     *
     * @param time  Unix time
     * @param format convert format
     * @return converted string
     * @see SimpleDateFormat
     * @todo low function should standart formatter
     */
    static public String format(long time, String format)
    {

        //SimpleDateFormat formatter = new SimpleDateFormat(format);
        //Date remainingDate = new Date(time);// + 1000);
        String chaos = "";
        String s;
        time /= 10;
        //ms
        //chaos = ( time % 100 ) + ":" + chaos;
        time /= 100;
        //s
        s = "" + ( time % 60 );
        if( s.length() == 1 )
            s = "0" + s;
        chaos = s + chaos;
        time /= 60;
        //m
        s = "" + ( time % 60 );
        if( s.length() == 1 )
            s = "0" + s;
        chaos = s + ":" + chaos;
        time /= 60;
        //h
        s = "" + ( time );
        if( s.length() == 1 )
            s = "0" + s;
        chaos = s + ":" + chaos;
        if( time > 100 )
            return "?";
        return chaos;
        //formatter.format(remainingDate);
    }

}
