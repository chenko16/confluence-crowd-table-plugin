package ut.ru.marxunion.atlassian.crowdtab.plugin;

import org.junit.Test;
import ru.marxunion.atlassian.crowdtab.plugin.api.MyPluginComponent;
import ru.marxunion.atlassian.crowdtab.plugin.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}