package de.noel.testui.pages;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

/**
 * TestPage - The simplest possible custom UI page.
 *
 * EXTENDS: BasicCustomUIPage
 *   - Use this when you don't need to handle any events (clicks, inputs, etc.)
 *   - Simpler build() signature: only UICommandBuilder, no events
 *
 * LIFETIME: CanDismiss
 *   - Player can press ESC to close the UI
 *
 * This page demonstrates:
 *   1. Loading a .ui file with cmd.append()
 *   2. Setting dynamic text with cmd.set()
 */
public class TestPage extends BasicCustomUIPage {

    // Data passed to the page - will be displayed in the UI
    private final String message;

    /**
     * Constructor.
     *
     * @param playerRef Reference to the player who will see this UI
     * @param message   The message to display
     */
    public TestPage(@Nonnull PlayerRef playerRef, String message) {
        // BasicCustomUIPage constructor takes:
        //   - playerRef: Which player sees this UI
        //   - lifetime: When can the UI be closed (CanDismiss = ESC key works)
        super(playerRef, CustomPageLifetime.CanDismiss);
        this.message = message;
    }

    /**
     * Build the UI.
     *
     * Called once when the page is opened. Use commandBuilder to:
     *   - Load .ui files (append)
     *   - Set values (set)
     *   - Clear elements (clear)
     *
     * @param commandBuilder Builder for UI commands
     */
    @Override
    public void build(@Nonnull UICommandBuilder commandBuilder) {
        // Step 1: Load the UI layout file
        // Path is relative to: src/main/resources/Common/UI/Custom/
        commandBuilder.append("Pages/TestPage.ui");

        // Step 2: Override the message text dynamically
        // Selector format: #ElementId.Property
        // This finds the element with id="Message" and sets its Text property
        commandBuilder.set("#Message.Text", message);
    }
}
