# Telestion

This repository contains the porject files which belong to the Telestion Group.
The project extensions are located in an own repository (eg. telestion-d2).

## Structure

The project consists of multiple submodules.

### Core

* telestion-api

  The public api which is visible to other partners.

* telestion-core

  The core classes like utils, factories, common messages, ...

* telestion-launcher

  The launcher of the telestion software.

* telestion-updater

  The updater of the telestion software.
  
* telection-tcp-adapter

### Connectors

Connectors are small applications, which are executed on an edge computer. They provide connections to other programms or devices.
The received data is forwarded via a TCP connection to the core application.

* telestion-uart-connector

  This connector reads and writes from/to a uart devices which is connected to the edge computer. The data is send to the core application via a tcp stream.

* telestion-matlab-connector

  This connector reads and writes Matlab structs to the tcp connection of the core application.

