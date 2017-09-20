package ru.biosoft.jobcontrol;

import java.util.logging.Logger;

abstract public class ClassJobControl extends AbstractJobControl
{
    public ClassJobControl(Logger log)
    {
        super(log);
    }

    @Override
    public void run()
    {
        super.run();
        
        if(getStatus() != TERMINATED_BY_REQUEST && getStatus() != TERMINATED_BY_ERROR)
            resultsAreReady();
    }
}
