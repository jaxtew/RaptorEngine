package us.totemsmc.raptorengine.api;

import us.totemsmc.raptorengine.objective.Objective;

import java.util.List;

public abstract class RaptorGame
{
    private GameState state;
    private List<Objective> objectives;

    public RaptorGame()
    {
        state = GameState.INITIALIZING;
        // initialize
        // open for players to join map
    }

    public final void start()
    {
        state = GameState.RUNNING;
        begin();
    }

    public abstract void begin();

    public abstract void tick();

    public final void stop()
    {
        state = GameState.FINISHING;
        end();
    }

    public abstract void end(); // TODO: Add winner

    public abstract List<Objective> getObjectives();

    public final void objectivesReady()
    {
        state = GameState.WAITING;
    }

    public GameState getState()
    {
        return state;
    }
}
