# TestUIPlugin

Ein Hytale Server Plugin zur Demonstration von Custom UI Pages. Das Projekt dient als Lernressource für die Entwicklung eigener UI-Oberflächen im Hytale-Ökosystem.

## Features

- **Demo-Seiten**: Verschiedene UI-Muster (Dialoge, Formulare, Info-Panels)
- **Progressives Tutorial**: Drei Schwierigkeitsstufen (statisch → interaktiv → dynamisch)
- **Ausführliche Dokumentation**: Schritt-für-Schritt-Anleitung in [TUTORIAL.md](TUTORIAL.md)

## Voraussetzungen

- Java 21
- Gradle 8.13+
- HytaleServer.jar im `libs/` Ordner

## Projektstruktur

```
src/main/java/de/noel/testui/
├── TestUIPlugin.java          # Hauptklasse
├── commands/                   # Command-Implementierungen
├── pages/                      # UI-Seiten
└── tutorial/                   # Tutorial-Level 1-3

src/main/resources/
├── manifest.json              # Plugin-Metadaten
└── Common/UI/Custom/Pages/    # UI-Definitionsdateien (.ui)
```

## Build

```bash
./gradlew build
```

Das fertige Plugin liegt unter `build/libs/TestUIPlugin-1.0.0.jar`.

## Installation

1. `TestUIPlugin-1.0.0.jar` in das Plugins-Verzeichnis des Hytale-Servers kopieren
2. Den Inhalt von `src/main/resources/Common/` in das entsprechende Server-Verzeichnis kopieren

## Commands

| Command | Beschreibung |
|---------|--------------|
| `/testui` | Einfache Test-Seite |
| `/dialog` | Gestylter Dialog mit Buttons |
| `/form` | Formular mit Eingabefeldern |
| `/info` | Info-Panel mit dynamischen Werten |
| `/helloworld` | Minimales Beispiel |
| `/tutorial1` | Tutorial: Statische Anzeige |
| `/tutorial2` | Tutorial: Interaktive Elemente |
| `/tutorial3` | Tutorial: Dynamische Werte |

## Architektur

### Page-Typen

**BasicCustomUIPage** - Für einfache, statische Seiten:
```java
public class MyPage extends BasicCustomUIPage {
    @Override
    public void build(UICommandBuilder builder) {
        builder.loadUI("Common/UI/Custom/Pages/MyPage.ui");
    }
}
```

**InteractiveCustomUIPage** - Für Seiten mit Event-Handling:
```java
public class MyPage extends InteractiveCustomUIPage<MyEventData> {
    @Override
    public void build(UICommandBuilder cmd, UIEventBuilder<MyEventData> event) {
        cmd.loadUI("Common/UI/Custom/Pages/MyPage.ui");
        event.bind("ButtonClick", MyEventData.CODEC);
    }

    @Override
    public void handleDataEvent(MyEventData data) {
        // Event verarbeiten
    }
}
```

### Event-Daten mit Codec

```java
public record MyEventData(String playerName) {
    public static final Codec<MyEventData> CODEC = BuilderCodec.of(MyEventData::new)
        .with("@PlayerName", MyEventData::playerName)  // @ = UI-Input-Binding
        .build();
}
```

## Lizenz

Dieses Projekt dient ausschließlich zu Lernzwecken.
