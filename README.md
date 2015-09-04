TeamCity Build Agent extensions for detecting Microsoft SDKs
==============================================================

This plugin extends TeamCity Build Agent with [Configuration Parameters](http://confluence.jetbrains.net/display/TCD6/Configuration+and+Build+Parameters)
based on the Microsoft SDKs installed on computer running the Build Agent.

Supported Microsoft SDKs:

* Windows Azure SDK (1.0 - 1.8, 2.0 - 2.7)
* Windows Phone SDK (7.0, 7.1, 8.0. 8.1)
* ASP.NET MVC (1.0 - 4)
* Visual F# Tools SDK (3.1.2)


## Contributing

### 1. Implement

This project contains 3 modules:

* microsoftsdks-server
* microsoftsdks-agent
* microsoftsdks-common


They contain code for server and agent parts of your plugin and a common part, available for both (agent and server). When implementing components for server and agent parts, do not forget to update spring context files under `main/resources/META-INF`. Otherwise your component may be not loaded. See TeamCity documentation for details on plugin development.

### 2. Build
Issue `mvn package` command from the root project to build your plugin. Resulting package **teamcity-microsoftsdks.zip** will be placed in **target** directory. 

### 3. Install
To install the plugin, put zip archive to **plugins** directory under TeamCity data directory. If you only changed agent-side code of your plugin, the upgrade will be perfomed 'on the fly' (agents will upgrade when idle). If common or server-side code has changed, restart the server.


## Archived version

This is old version of the plugin built for TeamCity 7.1.5: [Microsoft SDKs v1.3.3 plugin](http://code.izsak.net/github/teamcity-microsoftsdks/teamcity-microsoftsdks-v1.3.3.zip).


## License

Copyright (c) 2012-2015 Jozef Izso under [MIT License](LICENSE)