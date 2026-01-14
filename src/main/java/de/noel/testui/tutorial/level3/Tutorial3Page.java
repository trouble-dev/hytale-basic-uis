package de.noel.testui.tutorial.level3;

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
 * Tutorial Level 3: Dynamic Values
 *
 * Demonstrates:
 * - Passing data to the page via constructor
 * - Using cmd.set() to update UI elements dynamically
 * - Styled two-panel layout with FlexWeight
 * - Custom button styles
 * - Empty EventData for close-only buttons
 */
public class Tutorial3Page extends InteractiveCustomUIPage<Tutorial3Page.CloseEventData> {

    // Data passed via constructor - will be displayed in UI
    private final int playersOnline;
    private final int questCount;
    private final String uptime;

    /**
     * Empty EventData - we only need to handle the close button.
     * No fields, just an empty codec.
     */
    public static class CloseEventData {
        public static final BuilderCodec<CloseEventData> CODEC =
                BuilderCodec.builder(CloseEventData.class, CloseEventData::new).build();
    }

    public Tutorial3Page(
            @Nonnull PlayerRef playerRef,
            int playersOnline,
            int questCount,
            String uptime
    ) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, CloseEventData.CODEC);
        this.playersOnline = playersOnline;
        this.questCount = questCount;
        this.uptime = uptime;
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        // Load the UI layout
        cmd.append("Pages/Tutorial3Page.ui");

        // Set dynamic values using cmd.set()
        // Pattern: cmd.set("#ElementId.Property", stringValue)
        // Numbers must be converted with String.valueOf()
        cmd.set("#Stat1Value.Text", String.valueOf(playersOnline));
        cmd.set("#Stat2Value.Text", String.valueOf(questCount));
        cmd.set("#Stat3Value.Text", uptime);

        // Bind close button - no EventData needed, just triggers handleDataEvent
        evt.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton");
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull CloseEventData data
    ) {
        // Close the page
        Player player = store.getComponent(ref, Player.getComponentType());
        player.getPageManager().setPage(ref, store, Page.None);
    }
}
