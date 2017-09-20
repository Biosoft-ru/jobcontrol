package ru.biosoft.jobcontrol;

public interface Iteration<T>
{
    /**
     * Called on each iteration of the cycle.
     * 
     * @param element current element of iterated collection
     * @return true if cycle should continue; false if cycle should break
     */
    public boolean run(T element);
}