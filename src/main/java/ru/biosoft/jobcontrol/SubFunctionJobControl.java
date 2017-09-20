package ru.biosoft.jobcontrol;

/**
 */
public class SubFunctionJobControl extends FunctionJobControl
{
    private JobControl mainJob;
    private int from, to;
    private Throwable exception;
    
    public SubFunctionJobControl(JobControl jobControl, int from, int to)
    {
        super(jobControl instanceof AbstractJobControl ? ((AbstractJobControl)jobControl).log : null);
        this.mainJob = jobControl;
        this.from = from;
        this.to = to;
    }
    
    public SubFunctionJobControl(JobControl mainJob)
    {
        this(mainJob, 0, 100);
    }

    @Override
    public void functionTerminatedByError(Throwable t)
    {
        super.functionTerminatedByError(t);
        exception = t;
    }
    
    @Override
    public void setPreparedness(int percent)
    {
        mainJob.setPreparedness(percent*(to-from)/100+from);
        if(mainJob.getStatus() == TERMINATED_BY_REQUEST)
            terminate();
        super.setPreparedness(percent);
    }
    
    public void validate() throws Exception
    {
        if(exception instanceof Exception) throw (Exception)exception;
    }

    @Override
    public int getStatus()
    {
        if(mainJob.getStatus() == TERMINATED_BY_REQUEST && super.getStatus() == RUNNING)
            terminate();
        return super.getStatus();
    }
}
