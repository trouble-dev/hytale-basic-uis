package de.noel.testui.commands;

import de.noel.testui.pages.InfoPanelPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import java.util.Random;

public class InfoCommand extends AbstractPlayerCommand {

    private final Random random = new Random();

    public InfoCommand() {
        super("info", "Opens an info panel");
    }

    @Override
    protected void execute(
        @Nonnull CommandContext context,
        @Nonnull Store<EntityStore> store,
        @Nonnull Ref<EntityStore> ref,
        @Nonnull PlayerRef playerRef,
        @Nonnull World world
    ) {
        Player player = (Player) store.getComponent(ref, Player.getComponentType());

        // Generate some demo stats
        int players = 100 + random.nextInt(2000);
        int quests = 10 + random.nextInt(50);
        String uptime = (95 + random.nextInt(5)) + "%";

        InfoPanelPage page = new InfoPanelPage(playerRef, players, quests, uptime);
        player.getPageManager().openCustomPage(ref, store, page);
    }
}
