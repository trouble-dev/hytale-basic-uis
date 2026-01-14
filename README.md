# TestUIPlugin

A Hytale Server plugin demonstrating Custom UI Pages. This project serves as a learning resource for developing custom UI interfaces in the Hytale ecosystem.

## Features

- **Demo Pages**: Various UI patterns (dialogs, forms, info panels)
- **Progressive Tutorial**: Three difficulty levels (static → interactive → dynamic)
- **Comprehensive Documentation**: Step-by-step guide in [TUTORIAL.md](TUTORIAL.md)

## Requirements

- Java 21
- Gradle 8.13+
- HytaleServer.jar in the `libs/` folder

## Project Structure

```
src/main/java/de/noel/testui/
├── TestUIPlugin.java          # Main plugin class
├── commands/                   # Command implementations
├── pages/                      # UI pages
└── tutorial/                   # Tutorial levels 1-3

src/main/resources/
├── manifest.json              # Plugin metadata
└── Common/UI/Custom/Pages/    # UI definition files (.ui)
```

## Build

```bash
./gradlew build
```

The compiled plugin will be located at `build/libs/TestUIPlugin-1.0.0.jar`.

## Installation

1. Copy `TestUIPlugin-1.0.0.jar` to your Hytale server's plugins directory
2. Copy the contents of `src/main/resources/Common/` to the corresponding server directory

## Commands

| Command | Description |
|---------|-------------|
| `/testui` | Simple test page |
| `/dialog` | Styled dialog with buttons |
| `/form` | Form with input fields |
| `/info` | Info panel with dynamic values |
| `/helloworld` | Minimal example |
| `/tutorial1` | Tutorial: Static display |
| `/tutorial2` | Tutorial: Interactive elements |
| `/tutorial3` | Tutorial: Dynamic values |

## Architecture

### Page Types

**BasicCustomUIPage** - For simple, static pages:
```java
public class MyPage extends BasicCustomUIPage {
    @Override
    public void build(UICommandBuilder builder) {
        builder.loadUI("Common/UI/Custom/Pages/MyPage.ui");
    }
}
```

**InteractiveCustomUIPage** - For pages with event handling:
```java
public class MyPage extends InteractiveCustomUIPage<MyEventData> {
    @Override
    public void build(UICommandBuilder cmd, UIEventBuilder<MyEventData> event) {
        cmd.loadUI("Common/UI/Custom/Pages/MyPage.ui");
        event.bind("ButtonClick", MyEventData.CODEC);
    }

    @Override
    public void handleDataEvent(MyEventData data) {
        // Handle event
    }
}
```

### Event Data with Codec

```java
public record MyEventData(String playerName) {
    public static final Codec<MyEventData> CODEC = BuilderCodec.of(MyEventData::new)
        .with("@PlayerName", MyEventData::playerName)  // @ = UI input binding
        .build();
}
```

## License

This project is for educational purposes only.
