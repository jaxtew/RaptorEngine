package us.totemsmc.raptorengine;

import us.totemsmc.raptorengine.api.JSONConfiguration;

public class RaptorConfiguration extends JSONConfiguration
{
    public Boolean DEBUG;

    @Override
    public void checkFields()
    {
        if(DEBUG == null) DEBUG = true;
    }
}
