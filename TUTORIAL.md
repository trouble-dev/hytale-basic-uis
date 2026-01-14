# Hytale Custom UI Tutorial

A beginner-friendly, step-by-step guide to creating interactive custom UIs for Hytale plugins.

---

## Introduction

### What We Will Build

In this tutorial, you will learn how to create a **Settings Form** - a custom UI with:

- A text input field for entering a player name
- A number input field for setting a speed value
- Checkboxes for toggling options (notifications, coordinates)
- Save and Cancel buttons that respond to clicks
- A clean, styled appearance with colors and spacing

When complete, typing `/form` in the game will open your custom UI, and clicking the buttons will send the form data back to your Java code.

### What the End Result Looks Like

```
+------------------------------------------+
|              Settings                     |
|------------------------------------------|
|                                          |
|  Player Name    [Enter name...        ]  |
|                                          |
|  Speed          [100    ]                |
|                                          |
|------------------------------------------|
|  OPTIONS                                 |
|  [x] Enable notifications                |
|  [ ] Show coordinates                    |
|                                          |
|        [  SAVE  ]    [  CANCEL  ]        |
+------------------------------------------+
```

---

## Prerequisites

Before starting, make sure you have:

1. **Java 21** - Hytale requires Java 21 or newer
2. **Gradle** - Build tool for compiling the plugin
3. **IntelliJ IDEA** (recommended) - Or any Java IDE
4. **HytaleServer.jar** - The Hytale server JAR file (needed as a dependency)

---

## Step 1: Project Setup

### 1.1 Create the Folder Structure

Create a new folder for your plugin with this structure:

```
test-ui-plugin/
├── libs/
│   └── HytaleServer.jar          <-- Copy the server JAR here
├── src/
│   └── main/
│       ├── java/
│       │   └── de/
│       │       └── noel/
│       │           └── testui/
│       │               ├── TestUIPlugin.java
│       │               ├── commands/
│       │               │   └── FormCommand.java
│       │               └── pages/
│       │                   └── FormPage.java
│       └── resources/
│           ├── manifest.json
│           └── Common/
│               └── UI/
│                   └── Custom/
│                       └── Pages/
│                           └── FormPage.ui
├── build.gradle.kts
└── settings.gradle.kts
```

**Important:** The `resources/Common/UI/Custom/Pages/` path is required by Hytale! Your `.ui` files must be in this exact location.

### 1.2 build.gradle.kts Explained

Create `build.gradle.kts` in your project root:

```kotlin
// Enable the Java plugin - this tells Gradle we're building a Java project
plugins {
    java
}

// Configure Java version - Hytale requires Java 21
java {
    sourceCompatibility = JavaVersion.VERSION_21  // Source code uses Java 21 features
    targetCompatibility = JavaVersion.VERSION_21  // Compiled code runs on Java 21
}

// Where to download dependencies from
repositories {
    mavenCentral()  // Standard Maven repository for common libraries
}

// Project dependencies
dependencies {
    // The HytaleServer.jar provides all Hytale APIs
    // "compileOnly" means: use for compiling, but don't include in our JAR
    // (because the server already has these classes at runtime)
    compileOnly(files("libs/HytaleServer.jar"))
}

// Configure how the JAR file is built
tasks.jar {
    // If duplicate files exist, exclude them (avoids build errors)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // Name of the output JAR file
    archiveBaseName.set("TestUIPlugin")
    archiveVersion.set("1.0.0")

    // IMPORTANT: Include resource files (manifest.json and .ui files) in the JAR
    from("src/main/resources")
}
```

Also create a simple `settings.gradle.kts`:

```kotlin
rootProject.name = "test-ui-plugin"
```

### 1.3 manifest.json Explained

Create `src/main/resources/manifest.json`:

```json
{
  "Group": "TestUI",
  "Name": "TestUIPlugin",
  "Version": "1.0.0",
  "Main": "de.noel.testui.TestUIPlugin",
  "IncludesAssetPack": true
}
```

Let's understand each field:

| Field | Purpose |
|-------|---------|
| `Group` | Organization/category name for your plugin |
| `Name` | Unique identifier for your plugin |
| `Version` | Semantic version number (major.minor.patch) |
| `Main` | Full class path to your plugin's main class |
| `IncludesAssetPack` | **CRITICAL!** Set to `true` if your plugin has UI files. This tells Hytale to load the `Common/UI/` folder from your JAR as an asset pack. Without this, your UI files will not be found! |

---

## Step 2: Create the Plugin Main Class

Create `src/main/java/de/noel/testui/TestUIPlugin.java`:

```java
package de.noel.testui;

// Import the command class we'll create later
import de.noel.testui.commands.FormCommand;

// Hytale plugin system imports
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

// Annotation for null-safety
import javax.annotation.Nonnull;

// Standard Java logging
import java.util.logging.Level;

/**
 * Main plugin class.
 *
 * Every Hytale plugin must:
 * 1. Extend JavaPlugin
 * 2. Have a constructor that takes JavaPluginInit
 * 3. Override the setup() method
 */
public class TestUIPlugin extends JavaPlugin {

    /**
     * Constructor - called when Hytale loads your plugin.
     *
     * @param init Contains initialization data from the server.
     *             You MUST pass this to the super constructor.
     */
    public TestUIPlugin(@Nonnull JavaPluginInit init) {
        super(init);  // Always call super(init) first!
    }

    /**
     * Setup method - called after the constructor.
     *
     * This is where you:
     * - Register commands
     * - Set up event listeners
     * - Initialize your plugin's features
     */
    @Override
    protected void setup() {
        // Log a message so we know the plugin loaded
        getLogger().at(Level.INFO).log("TestUIPlugin loaded!");

        // Register the /form command
        // getCommandRegistry() provides access to Hytale's command system
        getCommandRegistry().registerCommand(new FormCommand());

        getLogger().at(Level.INFO).log("Command registered: /form");
    }
}
```

### Key Points

1. **Extend `JavaPlugin`** - This is the base class for all Hytale plugins
2. **Constructor with `JavaPluginInit`** - The server passes initialization data; always forward it to `super(init)`
3. **Override `setup()`** - This is your main entry point for plugin initialization
4. **Use `getCommandRegistry()`** - This is how you register commands that players can type
5. **Use `getLogger()`** - For logging messages to the server console

---

## Step 3: Create the UI File (.ui)

### 3.1 Where to Put It

UI files must be placed in:
```
src/main/resources/Common/UI/Custom/Pages/
```

This path is required by Hytale. The `Common/UI/Custom/Pages/` structure is how Hytale organizes custom UI files.

### 3.2 Understanding the UI DSL

Hytale uses a custom DSL (Domain-Specific Language) for defining UIs. It's similar to CSS/HTML but with its own syntax.

Create `src/main/resources/Common/UI/Custom/Pages/FormPage.ui`:

```
// ============================================================
// PART 1: External References
// ============================================================

// This line imports components from Common.ui (a built-in Hytale UI library)
// $C becomes a shorthand - we can use $C.@TextField instead of writing the full path
// The ".." means "go up one directory" (from Pages/ to Custom/)
$C = "../Common.ui";


// ============================================================
// PART 2: Custom Styles
// ============================================================

// Define a reusable button style for the Save button
// TextButtonStyle has three states: Default, Hovered, and Pressed
@PrimaryButtonStyle = TextButtonStyle(
  // Normal state - blue background
  Default: (
    Background: #3a7bd5,                    // Blue color (hex)
    LabelStyle: (
      FontSize: 14,
      TextColor: #ffffff,                   // White text
      RenderBold: true,
      HorizontalAlignment: Center,
      VerticalAlignment: Center
    )
  ),
  // When mouse hovers over button - lighter blue
  Hovered: (
    Background: #4a8be5,
    LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)
  ),
  // When button is clicked - darker blue
  Pressed: (
    Background: #2a6bc5,
    LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)
  )
);

// Cancel button style - red tones
@CancelButtonStyle = TextButtonStyle(
  Default: (Background: #8b3a3a, LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
  Hovered: (Background: #9b4a4a, LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center)),
  Pressed: (Background: #7b2a2a, LabelStyle: (FontSize: 14, TextColor: #ffffff, RenderBold: true, HorizontalAlignment: Center, VerticalAlignment: Center))
);


// ============================================================
// PART 3: The Main UI Structure
// ============================================================

// The root element - a Group that contains everything
Group {
  // Set the size of the entire panel
  Anchor: (Width: 480, Height: 380);

  // Dark semi-transparent background
  // Format: #RRGGBB(opacity) where opacity is 0.0 to 1.0
  Background: #141c26(0.98);

  // LayoutMode: Top means children stack vertically from top to bottom
  LayoutMode: Top;

  // Add 20 pixels of padding on all sides
  Padding: (Full: 20);


  // ----------------------------------------------------------
  // Title Label
  // ----------------------------------------------------------
  Label {
    Text: "Settings";                       // The text to display
    Anchor: (Height: 45);                   // Height of 45 pixels
    Style: (
      FontSize: 26,
      TextColor: #ffffff,
      HorizontalAlignment: Center,          // Center the text
      RenderBold: true
    );
  }


  // ----------------------------------------------------------
  // Horizontal Divider Line
  // ----------------------------------------------------------
  // A Group with height 1 and a background color = a line
  Group {
    Anchor: (Height: 1);
    Background: #2b3542;
  }


  // ----------------------------------------------------------
  // Spacer (empty space)
  // ----------------------------------------------------------
  // An empty Group just to add vertical spacing
  Group { Anchor: (Height: 16); }


  // ----------------------------------------------------------
  // Player Name Input Row
  // ----------------------------------------------------------
  Group {
    LayoutMode: Left;                       // Children stack horizontally
    Anchor: (Height: 44);

    // Label on the left
    Label {
      Text: "Player Name";
      Anchor: (Width: 130);
      Style: (FontSize: 14, TextColor: #96a9be, VerticalAlignment: Center);
    }

    // Text field from Common.ui
    // #NameInput is the ID - we'll use this in Java to read the value
    $C.@TextField #NameInput {
      FlexWeight: 1;                        // Take up remaining horizontal space
      PlaceholderText: "Enter name...";     // Gray text shown when empty
    }
  }


  // Spacer
  Group { Anchor: (Height: 12); }


  // ----------------------------------------------------------
  // Speed Number Input Row
  // ----------------------------------------------------------
  Group {
    LayoutMode: Left;
    Anchor: (Height: 44);

    Label {
      Text: "Speed";
      Anchor: (Width: 130);
      Style: (FontSize: 14, TextColor: #96a9be, VerticalAlignment: Center);
    }

    // Number field from Common.ui
    $C.@NumberField #SpeedInput {
      Anchor: (Width: 100);
      Value: 100;                           // Default value
    }
  }


  // Spacer
  Group { Anchor: (Height: 20); }


  // ----------------------------------------------------------
  // Another Divider (lighter)
  // ----------------------------------------------------------
  Group {
    Anchor: (Height: 1);
    Background: #2b3542(0.5);               // 50% opacity
  }


  // Spacer
  Group { Anchor: (Height: 16); }


  // ----------------------------------------------------------
  // Options Section Header
  // ----------------------------------------------------------
  Label {
    Text: "OPTIONS";
    Anchor: (Height: 20);
    Style: (
      FontSize: 11,
      TextColor: #4a5568,                   // Dim gray color
      LetterSpacing: 2                      // Space between letters
    );
  }


  // Spacer
  Group { Anchor: (Height: 8); }


  // ----------------------------------------------------------
  // Checkbox: Enable Notifications
  // ----------------------------------------------------------
  // CheckBoxWithLabel is a component from Common.ui
  // #NotifyOption is the ID for this checkbox row
  $C.@CheckBoxWithLabel #NotifyOption {
    @Text = "Enable notifications";         // The label text
    @Checked = true;                        // Default: checked
    Anchor: (Height: 28);
  }


  // Spacer
  Group { Anchor: (Height: 6); }


  // ----------------------------------------------------------
  // Checkbox: Show Coordinates
  // ----------------------------------------------------------
  $C.@CheckBoxWithLabel #CoordsOption {
    @Text = "Show coordinates";
    @Checked = false;                       // Default: unchecked
    Anchor: (Height: 28);
  }


  // ----------------------------------------------------------
  // Flexible Spacer
  // ----------------------------------------------------------
  // FlexWeight: 1 means this Group will expand to fill remaining space
  // This pushes the buttons to the bottom of the panel
  Group { FlexWeight: 1; }


  // ----------------------------------------------------------
  // Button Row
  // ----------------------------------------------------------
  Group {
    LayoutMode: Center;                     // Center children horizontally
    Anchor: (Height: 44);

    // Save Button
    // #SaveButton is the ID - we'll bind click events to this in Java
    TextButton #SaveButton {
      Text: "SAVE";
      Anchor: (Width: 110, Height: 40);
      Style: @PrimaryButtonStyle;           // Use our custom style
    }

    // Spacer between buttons
    Group { Anchor: (Width: 16); }

    // Cancel Button
    TextButton #CancelButton {
      Text: "CANCEL";
      Anchor: (Width: 110, Height: 40);
      Style: @CancelButtonStyle;
    }
  }
}
```

### 3.3 What is $C = "../Common.ui"?

This line is an **import statement** that lets you use pre-built UI components.

- `$C` is a variable name (you can call it anything, but `$C` is conventional)
- `"../Common.ui"` is the path to Hytale's built-in Common.ui file
- The `..` means "go up one directory" (from `Pages/` to `Custom/`)

After this import, you can use components like:
- `$C.@TextField` - A styled text input field
- `$C.@NumberField` - A number input field
- `$C.@CheckBoxWithLabel` - A checkbox with a text label
- `$C.@TextButton` - A styled button

### 3.4 UI DSL Quick Reference

| Concept | Syntax | Description |
|---------|--------|-------------|
| Import | `$C = "../Common.ui";` | Import external UI file |
| Use imported component | `$C.@TextField` | Use component from import |
| Element ID | `#MyId` | Assign an ID to reference in Java |
| Style variable | `@MyStyle = ...;` | Define a reusable style |
| Color | `#RRGGBB` or `#RRGGBB(opacity)` | Hex color with optional opacity |
| Layout modes | `Top`, `Left`, `Center` | How children are arranged |
| Size | `Anchor: (Width: X, Height: Y)` | Fixed dimensions |
| Flexible size | `FlexWeight: 1` | Take remaining space |

---

## Step 4: Create the Page Class (Java)

This is the most complex part. The Page class connects your UI file to your Java code.

Create `src/main/java/de/noel/testui/pages/FormPage.java`:

```java
package de.noel.testui.pages;

// Codec system - for converting between Java objects and BSON/JSON
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

// ECS (Entity Component System) types
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

// UI-related imports
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;

// Server utilities
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
 * FormPage - A custom UI page that shows a settings form.
 *
 * This class extends InteractiveCustomUIPage<T> where T is our event data class.
 * The generic parameter tells Hytale what type of data to expect when events occur.
 */
public class FormPage extends InteractiveCustomUIPage<FormPage.FormEventData> {

    // ================================================================
    // PART 1: Event Data Class
    // ================================================================

    /**
     * FormEventData holds the data sent from the UI when an event occurs.
     *
     * When the player clicks a button, Hytale collects values from the UI
     * and sends them to the server. This class defines WHAT values to collect.
     */
    public static class FormEventData {
        // These fields will be filled with data from the UI
        public String action;           // "Save" or "Cancel"
        public String playerName;       // Value from the text input
        public boolean notifications;   // Value from the notifications checkbox
        public boolean coordinates;     // Value from the coordinates checkbox

        /**
         * The CODEC tells Hytale how to convert between BSON data and this Java class.
         *
         * Think of it as a "recipe" for:
         * - Reading: BSON data -> FormEventData object
         * - Writing: FormEventData object -> BSON data
         */
        public static final BuilderCodec<FormEventData> CODEC =
            // Start building a codec for FormEventData
            // FormEventData::new tells it how to create a new instance
            ((BuilderCodec.Builder<FormEventData>) ((BuilderCodec.Builder<FormEventData>)
            ((BuilderCodec.Builder<FormEventData>) ((BuilderCodec.Builder<FormEventData>)

            BuilderCodec.builder(FormEventData.class, FormEventData::new)

                // Map "Action" in BSON to the 'action' field
                // Codec.STRING means it's a string value
                // The setter: (object, value) -> object.action = value
                // The getter: (object) -> object.action
                .append(
                    new KeyedCodec<>("Action", Codec.STRING),
                    (FormEventData o, String v) -> o.action = v,
                    (FormEventData o) -> o.action
                )
                .add())

                // Map "@PlayerName" to 'playerName' field
                // The "@" prefix is important! It tells Hytale this is a
                // dynamically bound value that will be read from the UI
                .append(
                    new KeyedCodec<>("@PlayerName", Codec.STRING),
                    (FormEventData o, String v) -> o.playerName = v,
                    (FormEventData o) -> o.playerName
                )
                .add())

                // Map "@Notifications" to 'notifications' field
                // Codec.BOOLEAN for true/false values
                .append(
                    new KeyedCodec<>("@Notifications", Codec.BOOLEAN),
                    (FormEventData o, Boolean v) -> o.notifications = v,
                    (FormEventData o) -> o.notifications
                )
                .add())

                // Map "@Coordinates" to 'coordinates' field
                .append(
                    new KeyedCodec<>("@Coordinates", Codec.BOOLEAN),
                    (FormEventData o, Boolean v) -> o.coordinates = v,
                    (FormEventData o) -> o.coordinates
                )
                .add())

            .build();  // Finalize the codec
    }


    // ================================================================
    // PART 2: Constructor
    // ================================================================

    /**
     * Constructor for FormPage.
     *
     * @param playerRef Reference to the player who will see this UI
     */
    public FormPage(@Nonnull PlayerRef playerRef) {
        super(
            playerRef,
            // CustomPageLifetime controls how the page can be closed:
            // - CanDismissOrCloseThroughInteraction: Player can press ESC or click outside
            // - RequireInteraction: Player MUST click a button to close
            CustomPageLifetime.CanDismissOrCloseThroughInteraction,
            // Pass our codec so Hytale knows how to decode event data
            FormEventData.CODEC
        );
    }


    // ================================================================
    // PART 3: Build Method - Connect UI to Java
    // ================================================================

    /**
     * The build() method is called when the page is opened.
     *
     * Here you:
     * 1. Tell Hytale which .ui file to display
     * 2. Bind events (like button clicks) to data collectors
     */
    @Override
    public void build(
        @Nonnull Ref<EntityStore> ref,           // Reference to entity storage
        @Nonnull UICommandBuilder commandBuilder, // For loading UI files
        @Nonnull UIEventBuilder eventBuilder,     // For binding events
        @Nonnull Store<EntityStore> store         // Entity component storage
    ) {
        // Load our UI file
        // The path is relative to Common/UI/Custom/
        commandBuilder.append("Pages/FormPage.ui");

        // ============================================================
        // Bind the Save button click event
        // ============================================================
        eventBuilder.addEventBinding(
            // Event type: Activating = when the button is clicked/activated
            CustomUIEventBindingType.Activating,

            // Element selector: #SaveButton matches the ID in our .ui file
            "#SaveButton",

            // EventData: What values to collect when this event fires
            new EventData()
                // Static value: always "Save" when this button is clicked
                .append("Action", "Save")

                // Dynamic values: read from UI elements using selectors
                // "#NameInput.Value" means: find element with ID "NameInput",
                // then get its "Value" property
                .append("@PlayerName", "#NameInput.Value")

                // For checkboxes inside a container, use space-separated selectors:
                // "#NotifyOption #CheckBox.Value" means:
                // 1. Find element with ID "NotifyOption"
                // 2. Inside it, find element with ID "CheckBox"
                // 3. Get its "Value" property
                .append("@Notifications", "#NotifyOption #CheckBox.Value")
                .append("@Coordinates", "#CoordsOption #CheckBox.Value")
        );

        // ============================================================
        // Bind the Cancel button click event
        // ============================================================
        eventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#CancelButton",
            // Cancel just needs the action, no form data
            new EventData().append("Action", "Cancel")
        );
    }


    // ================================================================
    // PART 4: Handle Events - Process User Input
    // ================================================================

    /**
     * Called when an event occurs (button clicked, etc.)
     *
     * The 'data' parameter contains all the values we specified in EventData,
     * automatically decoded using our CODEC.
     */
    @Override
    public void handleDataEvent(
        @Nonnull Ref<EntityStore> ref,
        @Nonnull Store<EntityStore> store,
        @Nonnull FormEventData data        // Our decoded event data!
    ) {
        // Get the Player entity so we can interact with them
        Player player = (Player) store.getComponent(ref, Player.getComponentType());

        // Check which action was triggered
        if ("Save".equals(data.action)) {
            // Handle the Save button click

            // Get the player name (with a fallback if empty)
            String name = data.playerName != null ? data.playerName : "unnamed";

            // Send a message to the player showing what was saved
            playerRef.sendMessage(Message.raw(
                "Saved! Name: " + name +
                ", Notifications: " + data.notifications +
                ", Coordinates: " + data.coordinates
            ));
        }
        // If action is "Cancel", we just close the page (below)

        // Close the UI page
        // Page.None means "no page" = close the current page
        player.getPageManager().setPage(ref, store, Page.None);
    }
}
```

### Understanding the Key Parts

#### What is `InteractiveCustomUIPage`?

This is the base class for custom UIs that can send data back to the server. The generic parameter `<FormEventData>` tells Hytale what type of data to expect.

#### What is the EventData Inner Class?

When a player clicks a button, you want to receive data from the UI (text input values, checkbox states, etc.). The `FormEventData` class defines the structure of this data. Each field corresponds to a piece of information you want to collect.

#### What is BuilderCodec?

Hytale uses BSON (Binary JSON) to send data between client and server. The `BuilderCodec` is a "translator" that converts between:
- BSON data (what the network sends)
- Java objects (what your code uses)

The pattern is:
1. Create a builder for your class: `BuilderCodec.builder(FormEventData.class, FormEventData::new)`
2. Append each field with a key, setter, and getter
3. Build the final codec

**Important:** The `@` prefix (like `@PlayerName`) indicates a dynamically-bound value that gets filled in from the UI at runtime.

#### The build() Method

This method sets up the connection between your UI file and your Java code:
1. `commandBuilder.append()` - Loads your .ui file
2. `eventBuilder.addEventBinding()` - Connects UI events (clicks) to data collection

#### The handleDataEvent() Method

This is your "callback" that runs when something happens in the UI. The `data` parameter contains all the values you specified in your event bindings, automatically converted to your `FormEventData` class.

---

## Step 5: Create the Command

Now we need a way for players to open the UI. We create a simple command.

Create `src/main/java/de/noel/testui/commands/FormCommand.java`:

```java
package de.noel.testui.commands;

// Import our FormPage
import de.noel.testui.pages.FormPage;

// Hytale imports
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Command that opens the settings form UI.
 *
 * Usage: /form
 *
 * Extends AbstractPlayerCommand which:
 * - Ensures only players (not console) can run this command
 * - Provides convenient access to player data
 */
public class FormCommand extends AbstractPlayerCommand {

    /**
     * Constructor - defines the command name and description.
     */
    public FormCommand() {
        // First parameter: command name (what players type after /)
        // Second parameter: description (shown in help)
        super("form", "Opens a settings form");
    }

    /**
     * Called when a player runs the /form command.
     */
    @Override
    protected void execute(
        @Nonnull CommandContext context,     // Information about the command execution
        @Nonnull Store<EntityStore> store,   // Entity component storage
        @Nonnull Ref<EntityStore> ref,       // Reference to the entity storage
        @Nonnull PlayerRef playerRef,        // Reference to the player
        @Nonnull World world                 // The world the player is in
    ) {
        // Get the Player entity from the component system
        Player player = (Player) store.getComponent(ref, Player.getComponentType());

        // Create a new instance of our FormPage for this player
        FormPage page = new FormPage(playerRef);

        // Open the page using the player's PageManager
        // PageManager handles all UI pages for a player
        player.getPageManager().openCustomPage(ref, store, page);
    }
}
```

### How PageManager Works

Every `Player` has a `PageManager` that controls what UI they see. The key methods are:

| Method | Purpose |
|--------|---------|
| `openCustomPage(ref, store, page)` | Opens a custom UI page |
| `setPage(ref, store, Page.None)` | Closes the current page |
| `setPage(ref, store, Page.SomeBuiltIn)` | Opens a built-in page |

---

## Step 6: Build and Test

### 6.1 Build the Plugin

Open a terminal in your project folder and run:

```bash
# On macOS/Linux
./gradlew jar

# On Windows
gradlew.bat jar
```

If successful, you'll find your plugin JAR at:
```
build/libs/TestUIPlugin-1.0.0.jar
```

### 6.2 Install the Plugin

1. Copy `TestUIPlugin-1.0.0.jar` to your Hytale server's `mods/` folder
2. Start (or restart) the Hytale server

### 6.3 Test In-Game

1. Join the server
2. Type `/form` in chat
3. The settings form should appear
4. Try:
   - Typing in the text field
   - Toggling the checkboxes
   - Clicking Save (should show a message with your values)
   - Clicking Cancel (should just close the form)

---

## How It All Connects (Summary)

Here's a diagram showing how everything works together:

```
+------------------+     +------------------+     +------------------+
|                  |     |                  |     |                  |
|  Player types    |---->|  FormCommand     |---->|  FormPage        |
|  /form           |     |  .execute()      |     |  .build()        |
|                  |     |                  |     |                  |
+------------------+     +------------------+     +------------------+
                                                          |
                                                          | loads
                                                          v
                                                  +------------------+
                                                  |                  |
                                                  |  FormPage.ui     |
                                                  |  (displayed to   |
                                                  |  the player)     |
                                                  |                  |
                                                  +------------------+
                                                          |
                                                          | user interacts
                                                          v
+------------------+     +------------------+     +------------------+
|                  |     |                  |     |                  |
|  Your code       |<----|  FormEventData   |<----|  EventData       |
|  in handleData   |     |  (decoded by     |     |  (values from    |
|  Event()         |     |  CODEC)          |     |  UI elements)    |
+------------------+     +------------------+     +------------------+
```

**Step-by-step flow:**

1. Player types `/form` in chat
2. `FormCommand.execute()` is called
3. A new `FormPage` instance is created
4. `player.getPageManager().openCustomPage()` sends the page to the client
5. `FormPage.build()` is called, which:
   - Loads `FormPage.ui` via `commandBuilder.append()`
   - Sets up event bindings via `eventBuilder.addEventBinding()`
6. Player sees the UI and can interact with it
7. Player clicks a button (Save or Cancel)
8. Hytale collects values specified in `EventData` from the UI elements
9. Values are sent to the server as BSON
10. `FormEventData.CODEC` decodes the BSON into a `FormEventData` object
11. `handleDataEvent()` is called with the decoded data
12. Your code processes the data and closes the page

---

## Common Mistakes and Solutions

### 1. "UI File Not Found" or Black Screen

**Symptoms:**
- UI doesn't appear at all
- Black screen when command is run

**Solutions:**

**Check manifest.json:**
```json
{
  "IncludesAssetPack": true   // <-- This MUST be true!
}
```

**Check file path:**
- The `.ui` file must be at: `src/main/resources/Common/UI/Custom/Pages/FormPage.ui`
- In Java, use: `commandBuilder.append("Pages/FormPage.ui")`
- The path is relative to `Common/UI/Custom/`

**Check build.gradle.kts:**
```kotlin
tasks.jar {
    from("src/main/resources")  // <-- This includes resources in JAR
}
```

### 2. Button Clicks Do Nothing

**Symptoms:**
- UI appears but clicking buttons has no effect
- `handleDataEvent()` is never called

**Solutions:**

**Check element IDs match:**
```
// In .ui file:
TextButton #SaveButton { ... }

// In Java (must match exactly!):
eventBuilder.addEventBinding(
    CustomUIEventBindingType.Activating,
    "#SaveButton",    // <-- Same ID with # prefix
    ...
);
```

**Check event type:**
```java
// For button clicks, use Activating:
CustomUIEventBindingType.Activating
```

### 3. Data Values Are Always Null

**Symptoms:**
- `handleDataEvent()` is called but `data.playerName` is null
- Form values aren't being received

**Solutions:**

**Check selector paths:**
```java
// Direct element:
.append("@PlayerName", "#NameInput.Value")

// Nested element (checkbox inside a container):
.append("@Notifications", "#NotifyOption #CheckBox.Value")
//                         ^-- parent    ^-- child inside parent
```

**Check CODEC keys match EventData keys:**
```java
// In EventData:
new EventData()
    .append("@PlayerName", "#NameInput.Value")   // Key is "@PlayerName"

// In CODEC (must match!):
new KeyedCodec<>("@PlayerName", Codec.STRING)    // Same key!
```

### 4. UI Cannot Be Closed (ESC Doesn't Work)

**Symptoms:**
- Player is stuck in the UI
- ESC key does nothing

**Solution:**

Check the `CustomPageLifetime` in your constructor:
```java
public FormPage(@Nonnull PlayerRef playerRef) {
    super(
        playerRef,
        // Use this to allow ESC to close:
        CustomPageLifetime.CanDismissOrCloseThroughInteraction,
        FormEventData.CODEC
    );
}
```

Options:
- `CanDismissOrCloseThroughInteraction` - Player can press ESC or click buttons
- `CanDismiss` - Player can press ESC
- `CantClose` - Only your code can close the page

### 5. Codec Compile Errors

**Symptoms:**
- Java compile errors with `BuilderCodec`
- Type casting errors

**Solution:**

The codec syntax is verbose due to Java generics. Copy this pattern exactly:

```java
public static final BuilderCodec<FormEventData> CODEC =
    ((BuilderCodec.Builder<FormEventData>) ((BuilderCodec.Builder<FormEventData>)
    // ... nested casts for each field ...

    BuilderCodec.builder(FormEventData.class, FormEventData::new)
        .append(new KeyedCodec<>("Key", Codec.TYPE), setter, getter)
        .add())  // <-- .add() after each .append()
        // ... more fields ...
    .build();  // <-- .build() at the end
```

### 6. HytaleServer.jar Not Found

**Symptoms:**
- Gradle sync fails
- "Could not resolve" error

**Solution:**

1. Create a `libs/` folder in your project root
2. Copy `HytaleServer.jar` into that folder
3. Make sure the filename matches exactly (case-sensitive)

Your `build.gradle.kts` should have:
```kotlin
dependencies {
    compileOnly(files("libs/HytaleServer.jar"))
}
```

---

## Next Steps

Now that you have a working form, here are some ideas to extend it:

### Add More Input Types

You can use other components from Common.ui:

```
// Dropdown menu
$C.@DropdownBox #DifficultySelect {
  // Configure options in Java
}

// Slider
$C.@Slider #VolumeSlider {
  // Configure min/max in Java
}
```

### Add Input Validation

Before saving, check if the input is valid:

```java
@Override
public void handleDataEvent(..., FormEventData data) {
    if ("Save".equals(data.action)) {
        // Validate the player name
        if (data.playerName == null || data.playerName.trim().isEmpty()) {
            playerRef.sendMessage(Message.raw("Error: Please enter a name!"));
            return; // Don't close - let them fix it
        }

        // Name is valid, proceed with saving
        // ...
    }

    player.getPageManager().setPage(ref, store, Page.None);
}
```

### Save Settings Persistently

Instead of just showing a message, save the settings to a file or database:

```java
if ("Save".equals(data.action)) {
    // Save to your config system
    YourConfigManager.setPlayerSetting(
        playerRef.getUUID(),
        "notifications",
        data.notifications
    );
    YourConfigManager.save();

    playerRef.sendMessage(Message.raw("Settings saved!"));
}
```

### Create Multi-Page Wizards

You can open another page instead of closing:

```java
if ("Next".equals(data.action)) {
    // Open page 2 instead of closing
    player.getPageManager().openCustomPage(
        ref, store,
        new SettingsPage2(playerRef, data.playerName)
    );
}
```

### Update UI Dynamically

You can update the UI while it's open using `sendUpdate()`:

```java
// Inside your page class
public void updateCounter(int newValue) {
    UICommandBuilder cmd = new UICommandBuilder();
    cmd.set("#CounterLabel.Text", String.valueOf(newValue));
    sendUpdate(cmd, null, false);
}
```

---

## Quick Reference Card

### File Locations

| File Type | Location |
|-----------|----------|
| Java code | `src/main/java/your/package/` |
| UI files | `src/main/resources/Common/UI/Custom/Pages/` |
| Manifest | `src/main/resources/manifest.json` |
| Server JAR | `libs/HytaleServer.jar` |

### Key Classes

| Class | Purpose |
|-------|---------|
| `JavaPlugin` | Base class for plugins |
| `InteractiveCustomUIPage<T>` | Base class for interactive UIs |
| `BuilderCodec<T>` | Converts between BSON and Java objects |
| `AbstractPlayerCommand` | Base class for player commands |
| `PageManager` | Controls UI pages for a player |

### UI DSL Basics

| Element | Example |
|---------|---------|
| Group | `Group { LayoutMode: Top; }` |
| Label | `Label { Text: "Hello"; }` |
| TextButton | `TextButton #MyBtn { Text: "Click"; }` |
| ID | `#MyElementId` |
| Style variable | `@MyStyle = ...;` |
| Import | `$C = "../Common.ui";` |
| Use import | `$C.@TextField` |

### EventData Selectors

| Pattern | Meaning |
|---------|---------|
| `#Id.Value` | Get Value property of element with ID |
| `#Parent #Child.Value` | Get Value from Child inside Parent |
| `"StaticString"` | A fixed string value |

### Common Event Types

| Type | When It Fires |
|------|---------------|
| `Activating` | Button click, Enter in text field |
| `ValueChanged` | Input value changed |
| `MouseEntered` | Mouse enters element |
| `MouseExited` | Mouse leaves element |
| `FocusGained` | Element gains focus |
| `FocusLost` | Element loses focus |

---

Happy coding! With these foundations, you can create any custom UI for your Hytale plugin.
