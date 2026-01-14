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
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.Message;
import javax.annotation.Nonnull;

/**
 * StyledDialogPage - A dialog with buttons that can be clicked.
 *
 * EXTENDS: InteractiveCustomUIPage<DialogEventData>
 *   - Use this when you need to handle events (button clicks, inputs, etc.)
 *   - Generic parameter <DialogEventData> defines what data we receive from events
 *
 * LIFETIME: CanDismissOrCloseThroughInteraction
 *   - Player can press ESC to close
 *   - Or the page closes when certain interactions happen
 *
 * This page demonstrates:
 *   1. Event binding with eventBuilder.addEventBinding()
 *   2. Handling events in handleDataEvent()
 *   3. Closing the page programmatically
 */
public class StyledDialogPage extends InteractiveCustomUIPage<StyledDialogPage.DialogEventData> {

    private final String headline;
    private final String message;

    /**
     * EventData class - defines what data we receive when events fire.
     *
     * In this simple case, we don't need any data - we just want to know
     * that a button was clicked. So the class is empty.
     *
     * The CODEC is required to serialize/deserialize the data.
     * For empty data, just use an empty builder.
     */
    public static class DialogEventData {
        public static final BuilderCodec<DialogEventData> CODEC =
            BuilderCodec.builder(DialogEventData.class, DialogEventData::new).build();
    }

    /**
     * Constructor.
     *
     * @param playerRef Reference to the player
     * @param headline  The headline text
     * @param message   The message body text
     */
    public StyledDialogPage(@Nonnull PlayerRef playerRef, String headline, String message) {
        // InteractiveCustomUIPage constructor takes:
        //   - playerRef: Which player sees this UI
        //   - lifetime: When can the UI be closed
        //   - codec: How to deserialize event data (DialogEventData.CODEC)
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, DialogEventData.CODEC);
        this.headline = headline;
        this.message = message;
    }

    /**
     * Build the UI and bind events.
     *
     * This is called once when the page opens.
     *
     * @param ref            Entity reference (for accessing components)
     * @param commandBuilder Builder for UI commands (append, set, clear)
     * @param eventBuilder   Builder for event bindings (button clicks, etc.)
     * @param store          Entity store (for accessing components)
     */
    @Override
    public void build(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull UICommandBuilder commandBuilder,
        @Nonnull UIEventBuilder eventBuilder,
        @Nonnull Store<EntityStore> store
    ) {
        // Load the UI layout
        commandBuilder.append("Pages/StyledDialog.ui");

        // Set dynamic text values
        commandBuilder.set("#Headline.Text", headline);
        commandBuilder.set("#Message.Text", message);

        // Bind button click events
        // CustomUIEventBindingType.Activating = "when this element is clicked"
        // The selector "#ActionButton" finds the button with that ID
        //
        // When clicked, handleDataEvent() will be called with an empty DialogEventData
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ActionButton");
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CloseButton");
    }

    /**
     * Handle events from the UI.
     *
     * Called when any bound event fires (button click, etc.)
     *
     * @param ref   Entity reference
     * @param store Entity store
     * @param data  The event data (empty in this case)
     */
    @Override
    public void handleDataEvent(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull Store<EntityStore> store,
        @Nonnull DialogEventData data
    ) {
        // Get the Player component to access PageManager
        Player player = (Player) store.getComponent(ref, Player.getComponentType());

        // Close the UI by setting the page to Page.None
        player.getPageManager().setPage(ref, store, Page.None);
    }
}
