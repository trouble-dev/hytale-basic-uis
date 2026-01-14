package de.noel.testui.tutorial.level3;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Command to open the Tutorial Level 3 page.
 * Usage: /tutorial3
 */
public class Tutorial3Command extends AbstractPlayerCommand {

    public Tutorial3Command() {
        super("tutorial3", "Opens Tutorial Level 3 - Dynamic Values", false);
    }

    @Override
    protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());

        // Pass dynamic data to the page
        // In a real plugin, these would come from your server/game state
        Tutorial3Page page = new Tutorial3Page(
                playerRef,
                42,           // playersOnline
                7,            // questCount
                "3h 24m"      // uptime
        );

        player.getPageManager().openCustomPage(ref, store, page);
    }
}
