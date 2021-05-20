# RuntimeSkriptAddonPatcher
Like [SkriptAddonPatcher](https://github.com/TPGamesNL/SkriptAddonPatcher) but as a plugin.

There are two versions available:
- The plugin replacer version (recommended)
- The class transformer version (requires Java 9+ and a JDK)

## Usage
1. Choose which version to use.
    1. Choose the plugin replacer version, unless you don't want your plugin files to be modified 
       (or if they can't be modified).
2. Download the chosen version from 
   [the releases page](https://github.com/TPGamesNL/RuntimeSkriptAddonPatcher/releases).
3. Install the plugin on your server.
4. Restart.
5. If you chose the plugin replacer version, you only have to run the server once with the plugin, 
   afterwards the plugin can be removed, unless new addons (that aren't updated) are added to the server.

## How it works
Both plugins are set up to load before Skript, and therefore before any addons.

### Plugin replacer version
Since Bukkit loads the main classes of plugins very early, we can run code before 
addon plugin files have been opened (they have been opened before, to read the plugin description file, 
but they are closed after this). The code we run is very similar to SkriptAddonPatcher, with some exceptions 
for CLI and logging.

### Class transformer version
The class transformer version uses Java's Instrumentation API and Attach API to load an Agent onto the server
(although from another process, as not all JVMs like to have an Agent loaded from within). This Agent registers
a class file transformer, which passes each class file being loaded through the addon patcher.

There are some disadvantages of this system:
- It requires a JDK, as the Attach API isn't available on JRE
- From testing, it also requires Java 9+
- It is pretty hacky

## Compiling
Build with `gradlew shadowJar`
