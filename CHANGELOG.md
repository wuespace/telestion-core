## [0.6.1](https://github.com/wuespace/telestion-core/compare/v0.6.0...v0.6.1) (2021-12-23)


### Bug Fixes

* **deps:** bump logback-classic from 1.2.7 to 1.2.10 ([dcaa0dd](https://github.com/wuespace/telestion-core/commit/dcaa0dd04cc2dd76716bda97cac33136048b6979))



# [0.6.0](https://github.com/wuespace/telestion-core/compare/v0.5.0...v0.6.0) (2021-12-23)


### Bug Fixes

* **api:** Fix various `TelestionVerticle` issues ([83a730e](https://github.com/wuespace/telestion-core/commit/83a730e396cffb3ba10ed7640fee15808aa40f20))


### Features

* **api:** Add `GenericConfiguration` as default type for `TelestionVerticle` configuration ([6493cbb](https://github.com/wuespace/telestion-core/commit/6493cbbd60daf143f1a2b1ff2fafe09bd4633a0f))
* **api:** Add more overloads for `request` method in Event Bus trait to handle `JsonMessage` messages ([c00f720](https://github.com/wuespace/telestion-core/commit/c00f7208c1da54c71d7ce1a4b5a73bd4976868ee))
* **api:** Add Telestion Configuration interface ([71a3682](https://github.com/wuespace/telestion-core/commit/71a36821f6f62e93f4ecaaf264b60a416aa7b60a))
* **api:** Add Telestion Verticle ([9cb472f](https://github.com/wuespace/telestion-core/commit/9cb472ff72dc0b7521383266c797e062173160d9))
* **api:** Add verticle deployer class to conveniently deploy verticles with or without configuration ([26c4be0](https://github.com/wuespace/telestion-core/commit/26c4be0da3bffdd23b88598f7e13347d69c7718a))
* **api:** Add WithEventBus trait for Telestion Verticles ([4171434](https://github.com/wuespace/telestion-core/commit/41714341b20e0f68a5741239d43740fac8f3acfa))
* **api:** Add WithSharedData trait for Telestion Verticles ([3206873](https://github.com/wuespace/telestion-core/commit/3206873a21290efb8777d691624043ab38900659))
* **api:** Deprecate `Config` utility class ([6f74ffa](https://github.com/wuespace/telestion-core/commit/6f74ffa1fec5d04beede61eab6d1edd86d8904dc))
* **api:** Remove verticle deployer ([c1f3a7d](https://github.com/wuespace/telestion-core/commit/c1f3a7db85937fbf24b51dfd9a278b32e61bf8a4))
* **api:** Use `DecodedMessage` record instead of generic verticle ([2f029b0](https://github.com/wuespace/telestion-core/commit/2f029b0daba19f0ba239328bc050dab5f7a0633e))
* **example:** Add `GenericConfiguration` in examples which don't have any configuration ([702c0f3](https://github.com/wuespace/telestion-core/commit/702c0f389ac73ea57b0c36bdba4740a418608356))
* **example:** Add ping pong verticles ([c09f2a1](https://github.com/wuespace/telestion-core/commit/c09f2a11ac79db0e0f732571c9d36b90f9479576))
* **example:** Remove `getConfigType` overrides in example verticles because it is not necessary anymore ([e00799a](https://github.com/wuespace/telestion-core/commit/e00799a2d4c064b1edfd0eee56a5debffd9c76fc))
* **example:** Update verticle examples to use Telestion Verticle as base class ([557b82d](https://github.com/wuespace/telestion-core/commit/557b82d68ec4894ba8d367669424d5678975e2e2))



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



