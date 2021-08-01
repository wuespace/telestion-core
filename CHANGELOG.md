# [0.5.0](https://github.com/wuespace/telestion-core/compare/v0.4.0...v0.5.0) (2021-08-01)


### Features

* **example:** Remove database save in RandomPositionPublisher ([89adfe8](https://github.com/wuespace/telestion-core/commit/89adfe8faeef5e5524567766e78e805018ebc249))
* **services:** Move MongoDB implementation to own extension https://github.com/wuespace/telestion-extension-mongodb ([a7fb49b](https://github.com/wuespace/telestion-core/commit/a7fb49b879cb224a72f7c5b7bdbe325280c2d213))



# [0.4.0](https://github.com/wuespace/telestion-core/compare/v0.3.0...v0.4.0) (2021-07-07)


### Features

* **api:** Add new configuration provider which enables default values ([a973cd1](https://github.com/wuespace/telestion-core/commit/a973cd1f0d30513bcfcae655db156138f74b145a))
* **connection:** Add configuration of baud rate to `SerialConn` verticle. Usages of the `de.wuespace.telestion.services.connection.rework.serial.SerialConn` now requires an additional parameter: `int baudRate`. To migrate, add this parameter to any application configuration using this verticle. The value used before is `9600`. Therefore, you can use `"baudRate": 9600` to match the old default configuration. ([ae4dad2](https://github.com/wuespace/telestion-core/commit/ae4dad2c9732047551ea74cca5b35b45bfd47f83))



# [0.3.0](https://github.com/wuespace/telestion-core/compare/v0.2.1...v0.3.0) (2021-05-08)


### Bug Fixes

* Remove bug that when TCP-Server stops, says the stopPromise is already completed ([6cdb4e3](https://github.com/wuespace/telestion-core/commit/6cdb4e30460ffe1dbe42f055d297ad5e1c9a0158))


### Features

* Delete legacy MAVLink-package ([1cd0196](https://github.com/wuespace/telestion-core/commit/1cd01968776b16325b0f825061c39f65d4b4caa2))
* Delete MAVLink Submodule ([b0b26e9](https://github.com/wuespace/telestion-core/commit/b0b26e9ff0e74f3ac5a4c348a6d72733d530a953))



## [0.2.1](https://github.com/wuespace/telestion-core/compare/v0.2.0...v0.2.1) (2021-05-05)


### Bug Fixes

* Load config for connection api from right location ([90c208d](https://github.com/wuespace/telestion-core/commit/90c208d03d19cc9772903d762c6475e7a533644c))



# [0.2.0](https://github.com/wuespace/telestion-core/compare/v0.1.2...v0.2.0) (2021-05-02)


### Bug Fixes

* Fix tests ([3a199ff](https://github.com/wuespace/telestion-core/commit/3a199ff586a5ed3f4602baebbf585618cba76c43))


### Features

* Deprecate old connection-api ([0613e7d](https://github.com/wuespace/telestion-core/commit/0613e7de4c0ae45c04cd77028c7cc902c586fde5))
* Implement new connection-api ([a90f222](https://github.com/wuespace/telestion-core/commit/a90f2221430a39c050b117f2a1b28b45e226f593))



## [0.1.2](https://github.com/wuespace/telestion-core/compare/v0.1.1...v0.1.2) (2021-04-28)


### Bug Fixes

* Remove web server from configuration ([ac8710e](https://github.com/wuespace/telestion-core/commit/ac8710e88a35060c356f34833cc17f294824db28))



## [0.1.1](https://github.com/wuespace/telestion-core/compare/v0.1.0...v0.1.1) (2021-04-28)


### Bug Fixes

* disable preview features ([#263](https://github.com/wuespace/telestion-core/issues/263)) ([d2271b1](https://github.com/wuespace/telestion-core/commit/d2271b1a14af48e224c6ac984c39d8d07e18ac07))



# [0.1.0](https://github.com/wuespace/telestion-core/compare/a50083983290ffe80825306bf13e5f932ac86c7f...v0.1.0) (2021-04-28)


### Bug Fixes

* Gradle package group ([eefb062](https://github.com/wuespace/telestion-core/commit/eefb0622531f9794b8efc94638e762be82e8cfd8))


### Features

* Add release action based on Conventional commits ([a500839](https://github.com/wuespace/telestion-core/commit/a50083983290ffe80825306bf13e5f932ac86c7f))



