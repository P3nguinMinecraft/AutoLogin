# AutoLogin
Automatically logs in for minecraft servers using /login or a customizable command

**Fabric 1.19.2-1.21.11+**

<a href="https://modrinth.com/mod/auto-login"><img alt="modrinth" height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg"></a>

## Usage
Automatically sends the login command when joining a server, if a password is set and the mod is toggled on.

#### Base Command
##### `/autologin`

#### Subcommands

##### `/autologin password set <password>`
Sets your login password.

##### `/autologin password get`
Displays your currently saved password.

##### `/autologin commands add <command>`
Adds a command to the list of login commands the mod will attempt (e.g. `login`, `l`).

##### `/autologin commands remove <command>`
Removes a command from the login command list.

##### `/autologin commands list`
Lists all currently configured login commands.

##### `/autologin toggle`
Toggles the mod on or off. Requires a password to be set before enabling.

## Dependencies
Requires [Fabric API](https://modrinth.com/mod/fabric-api)

Requires Java 21

## Versions
| Minecraft | AutoLogin |
|-|-|
| 1.19.2-1.21.11 | [1.1.0+1.21.11](https://cdn.modrinth.com/data/qbvK7wHQ/versions/VxEv4to7/autologin-1.1.0%2B1.21.11.jar) |

## Credits
P3nguinMinecraft

