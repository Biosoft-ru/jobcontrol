package ru.biosoft.jobcontrol;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides JobControl functionality for long time performed functions.
 *
 * <code>FunctionJobControl</code> is intended for control of function processing.
 *
 * Here the stub demonstrating how <code>FunctionJobControl</code> should be used:
 * <pre>
 * public void function(functionArguments, FunctionJobControl control) throws JobControlException
 * {
 *      try
 *      {
 *
 *          if(control != null)
 *              control.functionStarted();
 *          for(...)
 *          {
 *              if(control != null)
 *                  control.checkStatus();
 *              ...
 *              if(control != null)
 *                  control.setPreparedness( % );
 *          }
 *          if(control != null)
 *              control.functionFinished();
 *       }
 *       catch(Throwable t)
 *       {
 *           if(control != null)
 *               functionTerminatedByError(t);
 *       }
 * }
 * </pre>
*/
public class FunctionJobControl extends AbstractJobControl
{
    /**
     * Constructs FunctionJobControl.
     *
     * @param log
     */
    public FunctionJobControl(Logger log)
    {
        super(log);
    }

    /**
     * Constructs FunctionJobControl.
     *
     * @param log logger
     * @param l specified JobControlListener
     */
    public FunctionJobControl(Logger log, JobControlListener l)
    {
        super(log,l);
    }

    /**
     * This method should be called when Job is started.
     */
    public void functionStarted()
    {
        functionStarted(null);
    }

    /**
     * This method should be called when Job is started.
     *
     * @param msg   detailed message. This message is used for creating of JobControlEvent
     * @see JobControlEvent
     */
    public void functionStarted(String msg)
    {
        begin(msg);
    }

    /**
     * This method should be called when Job is finished successfuly.
     *
     * @param msg detailed message. This message is used for creating of JobControlEvent
     */
    public void functionFinished(String msg)
    {
        end(msg);
    }

    /**
     * This method should be called when Job is finished successfuly.
     *
     */
    public void functionFinished()
    {
        functionFinished(null) ;
    }

    /**
     * This method should be called when Job is finished by error.
     *
     * @param t exception that is occurred during Job process.
     */
    public void functionTerminatedByError(Throwable t)
    {
        if (log != null)
            log.log(Level.SEVERE, t.getMessage(), t);

        JobControlException ex = new JobControlException(t);
        exceptionOccured(ex);

        end(ex);
    }

    /**
     * Empty implementation.
     *
     * @exception JobControlException
     */
    @Override
    protected void doRun() throws JobControlException  { }
}
