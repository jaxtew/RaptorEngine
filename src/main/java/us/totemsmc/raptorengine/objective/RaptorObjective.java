package us.totemsmc.raptorengine.objective;

public class RaptorObjective
{
    private final String name;
    private int timesCompleted;

    protected RaptorObjective(String name)
    {
        this.name = name;
        this.timesCompleted = 0;
    }

    public String getName()
    {
        return name;
    }

    public int getTimesCompleted()
    {
        return timesCompleted;
    }

    public void addCompletion()
    {
        timesCompleted++;
    }
}
