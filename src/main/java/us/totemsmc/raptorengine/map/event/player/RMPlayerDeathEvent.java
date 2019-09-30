package us.totemsmc.raptorengine.map.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import us.totemsmc.raptorengine.map.RaptorMap;

public class RMPlayerDeathEvent extends RMPlayerEvent
{
    private final EntityDamageEvent.DamageCause cause;

    public RMPlayerDeathEvent(RaptorMap map, Player player, EntityDamageEvent.DamageCause cause)
    {
        super(map, player);
        this.cause = cause;
    }

    public EntityDamageEvent.DamageCause getCause()
    {
        return cause;
    }
}
