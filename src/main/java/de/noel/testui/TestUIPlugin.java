package de.noel.testui;

import de.noel.testui.commands.*;
import de.noel.testui.tutorial.level1.Tutorial1Command;
import de.noel.testui.tutorial.level2.Tutorial2Command;
import de.noel.testui.tutorial.level3.Tutorial3Command;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import javax.annotation.Nonnull;
import java.util.logging.Level;

public class TestUIPlugin extends JavaPlugin {

    public TestUIPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("TestUIPlugin loaded!");

        // Register all UI demo commands
        getCommandRegistry().registerCommand(new TestUICommand());
        getCommandRegistry().registerCommand(new DialogCommand());
        getCommandRegistry().registerCommand(new FormCommand());
        getCommandRegistry().registerCommand(new InfoCommand());
        getCommandRegistry().registerCommand(new HelloWorldCommand());

        // Register tutorial commands
        getCommandRegistry().registerCommand(new Tutorial1Command());
        getCommandRegistry().registerCommand(new Tutorial2Command());
        getCommandRegistry().registerCommand(new Tutorial3Command());

        getLogger().at(Level.INFO).log("Commands registered: /testui, /dialog, /form, /info, /tutorial1, /tutorial2, /tutorial3");
    }
}
