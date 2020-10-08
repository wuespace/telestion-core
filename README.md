# Telestion

This repository contains the project files which belong to the Telestion Group.
The project extensions are in an own repository (eg. telestion-d2).

## Contributing

Always create a new branch and a pull request to implement a new feature.
Because of the free plan we can't protect the master branch for now.
But we will revert all direct changes to the master branch.

## Project Overview

The project consists of multiple submodules.

### Core

* [telestion-api](https://github.com/TelestionGroup/telestion/issues/2) The public api which is visible to other partners.

* [telestion-core](https://github.com/TelestionGroup/telestion/issues/1) The core classes like utils, factories, common messages, ...

* [telestion-launcher](https://github.com/TelestionGroup/telestion/issues/3) The launcher of the telestion software.

* [telestion-updater](https://github.com/TelestionGroup/telestion/issues/4) The updater of the telestion software.
  
* [telestion-tcp-adapter](https://github.com/TelestionGroup/telestion/issues/5)

* [telestion-widget-adapter](https://github.com/TelestionGroup/telestion/issues/14) Contains the bridge to the gui widgets

### Extensions

* [telestion-mavlink-adapter](https://github.com/TelestionGroup/telestion/issues/8) Encodes and decodes MavLink-Messages.

* [telestion-iridium-adapter](https://github.com/TelestionGroup/telestion/issues/9) Decodes Iridium-Messages.

* [telestion-lora-adapter]() Decodes LoRa-Messages.

* [telestion-message-dumper](https://github.com/TelestionGroup/telestion/issues/13) Writes messages of a certain kind to a file.

### Extensions - REXUS

* [telestion-rx-wind-importer](https://github.com/TelestionGroup/telestion/issues/12) Imports wind data provided by SNSC.


### Connectors

Connectors are small applications, which are executed on an edge computer. They provide connections to other programms or devices.
The received data is forwarded via a TCP connection to the core application.

* [telestion-uart-connector](https://github.com/TelestionGroup/telestion/issues/6) This connector reads and writes from/to a uart devices which is connected to the edge computer. The data is send to the core application via a tcp stream.

* [telestion-matlab-connector](https://github.com/TelestionGroup/telestion/issues/7) This connector reads and writes Matlab structs to the tcp connection of the core application.

### Test-Dummies

* [telestion-mavlink-dummy](https://github.com/TelestionGroup/telestion/issues/10) A dummy providing MavLink-Packages to a TCP-Connection
* [telestion-iridium-dummy](https://github.com/TelestionGroup/telestion/issues/11) A dummy providing Iridium-Packages to a TCP-Connection
