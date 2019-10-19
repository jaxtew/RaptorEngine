package us.totemsmc.raptorengine.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import us.totemsmc.raptorengine.game.RaptorGame;
import us.totemsmc.raptorengine.objective.block.BlockObjective;
import us.totemsmc.raptorengine.objective.block.BreakBlockObjective;
import us.totemsmc.raptorengine.objective.block.PlaceBlockObjective;
import us.totemsmc.raptorengine.objective.block.Spawnpoint;

import java.util.HashMap;
import java.util.Map;

public class ObjectiveParser
{
    private static final char INDICATOR = '%';

    public static boolean isObjective(String[] signText)
    {
        String raw = formatSignText(signText);
        // check for indicator
        if (raw.length() > 0 && raw.charAt(0) == INDICATOR)
        {
            String[] props = raw.substring(1).split(" ");
            //check props for validity
            boolean hasGame = false;
            boolean hasName = false;
            for (String prop : props)
            {
                // check for game
                if (prop.startsWith("g:") && prop.length() > 2) hasGame = true;
                else if (prop.startsWith("n:")) // check for name
                {
                    if (!Character.isWhitespace(prop.charAt(2))) hasName = true;
                }
            }
            return hasGame && hasName;
        }
        return false;
    }

    public static boolean isObjectiveFor(String[] signText, RaptorGame game)
    {
        String raw = formatSignText(signText);
        // check for indicator
        if (raw.length() > 0 && raw.charAt(0) == INDICATOR)
        {
            String[] props = raw.substring(1).split(" ");
            //check props for validity
            boolean hasGame = false;
            boolean hasName = false;
            for (String prop : props)
            {
                // check for game
                if (prop.toLowerCase().startsWith("g:" + game.getName().toLowerCase())) hasGame = true;
                else if (prop.startsWith("n:")) // check for name
                {
                    if (!Character.isWhitespace(prop.charAt(2))) hasName = true;
                }
            }
            return hasGame && hasName;
        }
        return false;
    }

    public static Map<String, String> parseSign(String[] signText)
    {
        Map<String, String> parsed = new HashMap<>();
        String raw = formatSignText(signText);
        if (raw.length() == 0 || raw.charAt(0) != INDICATOR) return null;
        String[] props = raw.split(" ");
        for (String prop : props)
        {
            if (prop.length() > 2)
            {
                char type = prop.charAt(0);
                String value = prop.substring(2);
                String argument = null;
                if (value.contains(";"))
                {
                    String[] split = value.split(";");
                    value = split[0];
                    argument = split[1];
                    parsed.put("argument", argument);
                }
                switch (type)
                {
                    case 'g':
                        parsed.put("game", value);
                        break;
                    case 'n':
                        parsed.put("name", value);
                        break;
                    case 't':
                        parsed.put("team", value);
                        break;
                    case 'o':
                        parsed.put("objective", value);
                }
            } else if (prop.equalsIgnoreCase("-c")) parsed.put("chain", "true"); // just check for presence of key
            else if(prop.equalsIgnoreCase("-r")) parsed.put("repeatable", "true");
        }
        return parsed;
    }

    public static BlockObjective parseObjective(Block block, Map<String, String> properties)
    {
        switch (properties.get("objective").charAt(0))
        {
            case 'b':
                Material tool = properties.containsKey("argument") ? Material.matchMaterial(properties.get("argument")) : null;
                return new BreakBlockObjective(properties.get("name"),
                        block,
                        properties.containsKey("chain"),
                        properties.get("team"),
                        tool,
                        properties.containsKey("repeatable"));
            case 'p':
                Material blockType = properties.containsKey("argument") ? Material.matchMaterial(properties.get("argument")) : null;
                return new PlaceBlockObjective(properties.get("name"),
                        block,
                        properties.containsKey("chain"),
                        properties.get("team"),
                        blockType,
                        properties.containsKey("repeatable"));
            case 'r':
                // ReachBlockObjective
                break;
            case 'i':
                // InteractBlockObjective
                break;
            case 's':
                return new Spawnpoint(properties.get("name"),
                        block,
                        properties.get("team"),
                        properties.containsKey("chain"),
                        fromCardinal(properties.getOrDefault("argument", "s")));
            default:
                // generic block objective (custom objective)
                return null;
        }
        return null;
    }

    private static float fromCardinal(String direction)
    {
        switch (direction)
        {
            case "s":
                return 0;
            case "sw":
                return 45;
            case "w":
                return 90;
            case "nw":
                return 135;
            case "n":
                return 180;
            case "ne":
                return 225;
            case "e":
                return 270;
            case "se":
                return 315;
            default:
                return 0;
        }
    }

    private static String formatSignText(String[] signText)
    {
        StringBuilder builder = new StringBuilder();
        if(signText.length == 0) return "";
        boolean combine = false;
        for(int i = 0; i < signText.length; i++)
        {
            boolean willCombine = false;
            String line = signText[i];
            if(line.endsWith("\\"))
            {
                willCombine = true;
                line = line.substring(0, line.length()-1);
            }
            if(combine)
            {
                builder.append(line);
                combine = false;
            }else
            {
                builder.append(" ").append(line);
            }
            if(willCombine) combine = true;
        }
        return builder.toString().trim();
    }
}
