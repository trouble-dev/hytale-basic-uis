package de.noel.testui.pages;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * InfoPanelPage - Displays multiple dynamic values in a panel.
 *
 * This page demonstrates:
 *   1. Passing multiple values to a page via constructor
 *   2. Setting multiple UI elements with different values
 *   3. Converting non-string values to strings for display
 *
 * Use case: Server info panel, player stats, inventory summary, etc.
 */
public class InfoPanelPage extends InteractiveCustomUIPage<InfoPanelPage.InfoEventData> {

    // Data to display - passed via constructor
    private final int playersOnline;
    private final int activeQuests;
    private final String uptime;

    /**
     * Empty EventData - we only need to handle the close button.
     */
    public static class InfoEventData {
        public static final BuilderCodec<InfoEventData> CODEC =
            BuilderCodec.builder(InfoEventData.class, InfoEventData::new).build();
    }

    /**
     * Constructor with multiple data values.
     *
     * @param playerRef     Reference to the player
     * @param playersOnline Number of online players
     * @param activeQuests  Number of active quests
     * @param uptime        Server uptime string
     */
    public InfoPanelPage(@Nonnull PlayerRef playerRef, int playersOnline, int activeQuests, String uptime) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, InfoEventData.CODEC);
        this.playersOnline = playersOnline;
        this.activeQuests = activeQuests;
        this.uptime = uptime;
    }

    @Override
    public void build(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull UICommandBuilder commandBuilder,
        @Nonnull UIEventBuilder eventBuilder,
        @Nonnull Store<EntityStore> store
    ) {
        // Load the UI layout
        commandBuilder.append("Pages/InfoPanel.ui");

        // Set each stat value
        // Note: Numbers must be converted to String with String.valueOf()
        // The selector pattern is: #ElementId.Property
        commandBuilder.set("#Stat1Value.Text", String.valueOf(playersOnline));
        commandBuilder.set("#Stat2Value.Text", String.valueOf(activeQuests));
        commandBuilder.set("#Stat3Value.Text", uptime);  // Already a string

        // Bind close button
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton");
    }

    @Override
    public void handleDataEvent(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull Store<EntityStore> store,
        @Nonnull InfoEventData data
    ) {
        // Close the page
        Player player = (Player) store.getComponent(ref, Player.getComponentType());
        player.getPageManager().setPage(ref, store, Page.None);
    }
}
