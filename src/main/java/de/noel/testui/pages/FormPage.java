package de.noel.testui.pages;

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
 * FormPage - A page with input fields and checkboxes.
 *
 * This is the most complex example - it demonstrates:
 *   1. Reading input values from TextFields
 *   2. Reading checkbox states
 *   3. Using the @-prefix to bind input values to EventData
 *   4. Handling different button actions (Save vs Cancel)
 *
 * KEY CONCEPT: The @-prefix
 *   - In the Codec: "@PlayerName" means "this value comes from an input"
 *   - In EventData.append(): "@PlayerName", "#NameInput.Value" binds the input value
 *   - When the event fires, the current input value is automatically included
 */
public class FormPage extends InteractiveCustomUIPage<FormPage.FormEventData> {

    /**
     * FormEventData - Contains all data from the form.
     *
     * Fields:
     *   - action: Which button was clicked ("Save" or "Cancel")
     *   - playerName: Value from the text input field
     *   - notifications: State of the notifications checkbox
     *   - coordinates: State of the coordinates checkbox
     *
     * The CODEC defines how to serialize/deserialize each field.
     */
    public static class FormEventData {
        public String action;           // Button action identifier
        public String playerName;       // Text input value
        public boolean notifications;   // Checkbox state
        public boolean coordinates;     // Checkbox state

        /**
         * Codec for serializing/deserializing the event data.
         *
         * IMPORTANT: Fields that read from UI inputs use the @-prefix:
         *   - "Action" - regular field, set explicitly in EventData
         *   - "@PlayerName" - bound to an input element's value
         *   - "@Notifications" - bound to a checkbox's value
         *
         * The pattern for each field:
         *   .append(
         *       new KeyedCodec<>("FieldName", Codec.TYPE),  // Name and type
         *       (obj, val) -> obj.field = val,              // Setter
         *       obj -> obj.field                            // Getter
         *   )
         */
        public static final BuilderCodec<FormEventData> CODEC = BuilderCodec.builder(FormEventData.class, FormEventData::new)
            // Regular field - which button was clicked
            .append(new KeyedCodec<>("Action", Codec.STRING), (FormEventData o, String v) -> o.action = v, (FormEventData o) -> o.action)
            .add()
                // Input binding - text field value (note the @ prefix!)
                .append(new KeyedCodec<>("@PlayerName", Codec.STRING), (FormEventData o, String v) -> o.playerName = v, (FormEventData o) -> o.playerName)
                .add()
                // Input binding - checkbox value
                .append(new KeyedCodec<>("@Notifications", Codec.BOOLEAN), (FormEventData o, Boolean v) -> o.notifications = v, (FormEventData o) -> o.notifications)
                .add()
                // Input binding - checkbox value
                .append(new KeyedCodec<>("@Coordinates", Codec.BOOLEAN), (FormEventData o, Boolean v) -> o.coordinates = v, (FormEventData o) -> o.coordinates)
                .add()
            .build();
    }

    public FormPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, FormEventData.CODEC);
    }

    @Override
    public void build(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull UICommandBuilder commandBuilder,
        @Nonnull UIEventBuilder eventBuilder,
        @Nonnull Store<EntityStore> store
    ) {
        // Load the form UI
        commandBuilder.append("Pages/FormPage.ui");

        // Bind Save button with ALL input values
        // The EventData captures:
        //   - "Action" = "Save" (explicit value)
        //   - "@PlayerName" = current value of #NameInput TextField
        //   - "@Notifications" = current value of the notifications checkbox
        //   - "@Coordinates" = current value of the coordinates checkbox
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#SaveButton",
            new EventData()
                .append("Action", "Save")
                .append("@PlayerName", "#NameInput.Value")                    // Read TextField value
                .append("@Notifications", "#NotifyOption #CheckBox.Value")   // Read CheckBox value
                .append("@Coordinates", "#CoordsOption #CheckBox.Value")     // Read CheckBox value
        );

        // Bind Cancel button - only needs action, no input values
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#CancelButton",
            new EventData().append("Action", "Cancel")
        );
    }

    @Override
    public void handleDataEvent(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull Store<EntityStore> store,
        @Nonnull FormEventData data
    ) {
        Player player = store.getComponent(ref, Player.getComponentType());

        // Handle different actions
        if ("Save".equals(data.action)) {
            // data.playerName contains the text the user typed
            // data.notifications and data.coordinates contain checkbox states
            String name = data.playerName != null ? data.playerName : "unnamed";
            playerRef.sendMessage(Message.raw("Saved! Name: " + name +
                ", Notifications: " + data.notifications +
                ", Coordinates: " + data.coordinates));
        }
        // For "Cancel", we just close without doing anything

        // Close the page
        player.getPageManager().setPage(ref, store, Page.None);
    }
}
