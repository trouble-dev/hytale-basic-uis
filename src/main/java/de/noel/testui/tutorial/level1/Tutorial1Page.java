package de.noel.testui.tutorial.level1;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

/**
 * Tutorial Level 1: Static Display
 *
 * The simplest possible custom UI page.
 * - Extends BasicCustomUIPage (no event handling)
 * - Just loads a .ui file and displays it
 */
public class Tutorial1Page extends BasicCustomUIPage {

    public Tutorial1Page(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss);
    }

    @Override
    public void build(@Nonnull UICommandBuilder cmd) {
        // Load the UI file
        // Path is relative to: src/main/resources/Common/UI/Custom/
        cmd.append("Pages/Tutorial1Page.ui");
    }
}
