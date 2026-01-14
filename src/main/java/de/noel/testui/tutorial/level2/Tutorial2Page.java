package de.noel.testui.tutorial.level2;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Tutorial Level 2: Events
 *
 * Interactive UI with:
 * - A TextField for user input
 * - A Button that triggers an event
 * - Event handling that reads the input value
 */
public class Tutorial2Page extends InteractiveCustomUIPage<Tutorial2Page.GreetEventData> {

    /**
     * EventData class - holds the data we receive when the button is clicked.
     *
     * The CODEC tells Hytale:
     * 1. How to create a new GreetEventData instance
     * 2. How to fill in the playerName field from the UI
     */
    public static class GreetEventData {
        public String playerName;

        public static final BuilderCodec<GreetEventData> CODEC =
                BuilderCodec.builder(GreetEventData.class, GreetEventData::new)
                        .append(
                                // "@PlayerName" = read from UI input (the @ prefix is important!)
                                new KeyedCodec<>("@PlayerName", Codec.STRING),
                                // Setter: put the value into obj.playerName
                                (GreetEventData obj, String val) -> obj.playerName = val,
                                // Getter: read the value from obj.playerName
                                (GreetEventData obj) -> obj.playerName
                        )
                        .add()
                        .build();
    }

    public Tutorial2Page(@Nonnull PlayerRef playerRef) {
        // Pass the CODEC to the parent class
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, GreetEventData.CODEC);
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder cmd,
            @Nonnull UIEventBuilder evt,
            @Nonnull Store<EntityStore> store
    ) {
        // Load the UI file
        cmd.append("Pages/Tutorial2Page.ui");

        // Bind the button click event
        // When #GreetButton is clicked:
        // - Read the value from #NameInput.Value
        // - Put it in "@PlayerName" (which maps to GreetEventData.playerName)
        evt.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#GreetButton",
                new EventData().append("@PlayerName", "#NameInput.Value")
        );
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull GreetEventData data
    ) {
        // Get the player component
        Player player = store.getComponent(ref, Player.getComponentType());

        // Use the data from the form
        String name = data.playerName != null && !data.playerName.isEmpty()
                ? data.playerName
                : "Stranger";

        // Send a greeting message
        playerRef.sendMessage(Message.raw("Hello, " + name + "!"));

        // Close the UI
        player.getPageManager().setPage(ref, store, Page.None);
    }
}
