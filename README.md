[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/lebonq/modpack_updater.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/lebonq/modpack_updater/context:java)

NEED A BIG REWRITE BECAUSE THE CODE IS... WELL HORRIBLE

# modpack_updater

Ce logiciel permet de maintenir les mods [Fabric](https://fabricmc.net/) des joueurs a jour des modpacks crees avec [modpack_uploader](https://github.com/lebonq/modpack_uploader).

## Utilisation

**Necessite Java 14 au minimun**

Le programme est à exécuter avec le dossier config dans le meme repertoire.

### Configuration

Avant de le distribuer aux joueurs, veuillez configurer le fichier configApp.properties du dossier config.

### To-do

1. ~~Lancer Minecraft depuis le launcher~~
2. ~~Installer Minecraft et Fabric depuis le launcher~~
3. Inserer directement le fichier server.dat avec l'adresse du server
4. Messages d'erreurs plus explicites
5. ~~Java inclus dans la release~~
6. ~~Ajout remind me~~
7. Amelioration de la commande de lancement de MC
8. ~~Suppression du dossier bin~~
9. Afficher la console MC
10. Gestion des screens
11. ~~Netoyage du code et plusieurs ameliorations~~
12. Ajustement de la taille de la fenetre

#### Sources from part of the code

To get access token, UUID and display name from username, password inspired by <https://www.spigotmc.org/threads/how-to-get-api-mojang-minecraft-client-access-token.159019/>

SHA1 calculator from  <https://stackoverflow.com/questions/6293713/java-how-to-create-sha-1-for-a-file>

Name of librarie to path <https://github.com/ATLauncher/ATLauncher/blob/5a90f8fcc74cc8bc96dbac91d905f4ce1f5fd1e8/src/main/java/com/atlauncher/utils/Utils.java#L1446
