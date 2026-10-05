# Lenzi addon (Meteor Client, Minecraft 1.21.11)

Module (Kategorie "Lenzi addon" im Meteor-GUI):
lenzi-flight, lenzi-boat-fly, lenzi-speed, lenzi-no-fall, lenzi-fullbright, lenzi-esp, lenzi-ore-esp
und **lenzi-menu**, das neonrote Menü (lege dir im Meteor-GUI eine Taste darauf).

## .jar bauen (ohne lokale Java-Installation)
1. Neues GitHub-Repository anlegen und den kompletten Inhalt dieses Ordners hochladen
   (inklusive des versteckten Ordners `.github`).
2. Tab **Actions** öffnen. Der Workflow "Build Lenzi addon" läuft automatisch.
3. Nach dem Lauf unter **Artifacts** `lenzi-addon-jar` herunterladen. Darin liegt `lenzi-addon-1.0.0.jar`.

## .jar lokal bauen
JDK 21 und Gradle installieren, dann im Ordner: `gradle build` -> `build/libs/lenzi-addon-1.0.0.jar`

## Installieren
Fabric Loader 1.21.11 + Meteor Client (1.21.11) + `lenzi-addon-1.0.0.jar` in den `mods`-Ordner.

## Falls der Build fehlschlägt
Die Versionen stehen in `gradle/libs.versions.toml`. Meteor hat inzwischen auf neuere Minecraft-Versionen
umgestellt; falls `1.21.11-SNAPSHOT` oder die Yarn-Version nicht mehr passt, dort anpassen.
