package ru.biosoft.jobcontrol;

import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Progress bar with stack which maps subprocesses progress to subranges of total process.
 */
public abstract class StackProgressJobControl extends ClassJobControl
{
    private static class Range
    {
        double from, to;

        public Range(double from, double to)
        {
            super();
            this.from = from;
            this.to = to;
        }
        
        public double translateValue(double value)
        {
            return (this.to-this.from)*value/100+this.from;
        }
        
        public Range getSubrange(double from, double to)
        {
            return new Range(translateValue(from), translateValue(to));
        }
    }

    private Stack<Range> stack = new Stack<>();
    
    public StackProgressJobControl(Logger log)
    {
        super(log);
        stack.push(new Range(0,100));
    }

    /**
     * Pushes new progress range into stack mapping it to the current range.
     * 
     * Don't forget to call popProgress after subprocess will finish.
     * 
     * @param from lower bound of new range
     * @param to upper bound of new range
     */
    public void pushProgress(int from, int to)
    {
        stack.push(stack.peek().getSubrange(from, to));
    }
    
    /**
     * Pops progress range from stack which was put by previous pushProgress call.
     */
    public void popProgress()
    {
        setPreparedness(100);
        stack.pop();
    }
    
    /**
     * Pops progress range from stack and shrinks the rest range.
     */
    public void popAndShrink()
    {
        setPreparedness(100);
        Range range1 = stack.pop();
        Range range2 = stack.pop();
        stack.push(new Range(range1.to, range2.to));
    }

    /**
     * Iterates over collection calling iteration.
     * 
     * Run for each iteration and updating progress uniformly based on collection size.
     * 
     * @param collection collection to iterate over
     * @param iteration handler to handle each iteration
     * @return true if was finished, false if break was done (either by user or by iteration)
     */
    public <T> boolean forCollection(Collection<T> collection, Iteration<T> iteration)
    {
        int size = collection.size();
        Iterator<T> iterator = collection.iterator();
        int n = 0;

        while(iterator.hasNext())
        {
            pushProgress((int) ( (long)100 * n / size ), (int) ( (long)100 * ( n + 1 ) / size ));
            if( !iteration.run(iterator.next()) )
            {
                popProgress();
                return false;
            }
            popProgress();
            if(getStatus() == TERMINATED_BY_REQUEST) return false;
            n++;
        }

        return true;
    }
    
    @Override
    public void setPreparedness(int percent)
    {
        super.setPreparedness((int)stack.peek().translateValue(percent));
    }
}
