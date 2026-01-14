package de.noel.testui.commands;

import de.noel.testui.pages.StyledDialogPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class DialogCommand extends AbstractPlayerCommand {

    public DialogCommand() {
        super("dialog", "Opens a styled dialog");
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
        StyledDialogPage page = new StyledDialogPage(
            playerRef,
            "Plugin Demo",
            "This dialog uses Common.ui styling with decorated containers!"
        );
        player.getPageManager().openCustomPage(ref, store, page);
    }
}
