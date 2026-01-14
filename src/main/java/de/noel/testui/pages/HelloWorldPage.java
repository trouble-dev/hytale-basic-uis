package de.noel.testui.pages;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class HelloWorldPage extends BasicCustomUIPage {

    private final String message;

    public HelloWorldPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime, String message) {
        super(playerRef, lifetime);
        this.message = message;
    }

    @Override
    public void build(UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Pages/HelloWorldPage.ui");
    }

}
