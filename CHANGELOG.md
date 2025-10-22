# Changelog

## 0.9.0 (2025-10-22)


### ⚠ BREAKING CHANGES

* **api:** `DecodedMessage` was renamed to `ResponseMessageWrapper`
* **api:** Remove the second constructor from `TelestionVerticle` that allowed to skip loading loading the default config (`public TelestionVerticle(boolean skipDefaultConfigLoading)`)
* **api:** Improve the method naming: In the `TelestionVerticle`, `getGenericConfig()` is now called `getUntypedConfig()`
* **api:** Improve the method naming In the `TelestionVerticle`, `getGenericDefaultConfig()` is now called `getUntypedDefaultConfig()`
* **api:** Move `Class<T> getConfigType()` in `TelestionVerticle` to `Class<T> VerticleConfigStrategy.getConfigType(Class<X extends TelestionVerticle<T>> clazz)`
* **api:** Remove `defaultStorageKey` method in `WithSharedData` interface.
* **api:** Rename method `get` to `getString` in `HeaderInformation` class.
* **api:** Rename `JsonMessage` interface to `JsonRecord`.
* **api:** Remove `GenericConfiguration` class. Use `UntypedConfiguration` to indicate generic, untyped configuration or `NoConfiguration` to indicate no configuration options instead. To build your own typed configuration options, implement `TelestionConfiguration`.
* **api:** Remove `Config` class in favor of `TelestionVerticle`
* Remove `Configuration` and `VerticleConfig` from services module and add them to the application module.
* **services:** Remove `ConfigurableApplication` because it implements the same behaviour as the `Telestion` class in the application module, and is not used at the moment.
* **application:** Remove the `Application` class from the application module because it is empty
* **config:** The deprecated `config.json` is no longer available
* **api:** The encoded messages don't longer contain the `"classname"` property that stores the classname of the encoded message.
* **api:** The encoded messages don't longer contain the `"classname"` property that stores the classname of the encoded message.

### Features

* **api:** Add `GenericConfiguration` as default type for `TelestionVerticle` configuration ([6493cbb](https://github.com/wuespace/telestion-core/commit/6493cbbd60daf143f1a2b1ff2fafe09bd4633a0f))
* **api:** Add a default local and remote map for every verticle in `WithSharedData` ([0ecaa9b](https://github.com/wuespace/telestion-core/commit/0ecaa9b9e83e982a99497c55a2b4d92046ba3220))
* **api:** Add a Vert.x `MultiMap` wrapper which provides better basic type support and some conversion and extraction features for a better coding experience with Vert.x headers. ([efc3886](https://github.com/wuespace/telestion-core/commit/efc38864db7f2a5d29227d9b3fbe2c212a410787))
* **api:** Add automatic loading of the default configuration in `TelestionVerticle` ([a62e37f](https://github.com/wuespace/telestion-core/commit/a62e37f5786c62d152470c74ea3f449934a5ae3f))
* **api:** Add compare methods to `HeaderInformation` ([f32de29](https://github.com/wuespace/telestion-core/commit/f32de29f21525aa2fa2b687b864823d8aa660ad7))
* **api:** Add generic types to request and register methods in `WithEventBus` trait ([6a9d344](https://github.com/wuespace/telestion-core/commit/6a9d34475a2c284b8e7270ca25f61367dd3bf491))
* **api:** Add more overloads for `request` method in Event Bus trait to handle `JsonMessage` messages ([c00f720](https://github.com/wuespace/telestion-core/commit/c00f7208c1da54c71d7ce1a4b5a73bd4976868ee))
* **api:** Add support for `HeaderInformation` in the `WithEventBus` verticle trait with many new overloaded methods for a better coding experience. ([9bc61d3](https://github.com/wuespace/telestion-core/commit/9bc61d32f450159ed9e5617749448702889c0fe1))
* **api:** Add Telestion Configuration interface ([71a3682](https://github.com/wuespace/telestion-core/commit/71a36821f6f62e93f4ecaaf264b60a416aa7b60a))
* **api:** Add Telestion Verticle ([9cb472f](https://github.com/wuespace/telestion-core/commit/9cb472ff72dc0b7521383266c797e062173160d9))
* **api:** Add utility methods for the Vert.x `MultiMap`. ([17c24a7](https://github.com/wuespace/telestion-core/commit/17c24a7e15ff52511114a22a27f7f5548a84bd4d))
* **api:** Add verticle deployer class to conveniently deploy verticles with or without configuration ([26c4be0](https://github.com/wuespace/telestion-core/commit/26c4be0da3bffdd23b88598f7e13347d69c7718a))
* **api:** Add Verticle trait that simplifies the access to the timing functions of Vert.x ([c98c6c1](https://github.com/wuespace/telestion-core/commit/c98c6c1f0c405c250949cd69e6eaa3985dcd7aee))
* **api:** Add warnings to `HeaderInformation` add and set methods if there are already values assigned to the key. ([347bad1](https://github.com/wuespace/telestion-core/commit/347bad1bf15f154e52301fb3f86de19fc3e0ce9c))
* **api:** Add WithEventBus trait for Telestion Verticles ([4171434](https://github.com/wuespace/telestion-core/commit/41714341b20e0f68a5741239d43740fac8f3acfa))
* **api:** Add WithSharedData trait for Telestion Verticles ([3206873](https://github.com/wuespace/telestion-core/commit/3206873a21290efb8777d691624043ab38900659))
* **api:** Deprecate `Config` utility class ([6f74ffa](https://github.com/wuespace/telestion-core/commit/6f74ffa1fec5d04beede61eab6d1edd86d8904dc))
* **api:** Deprecate `GenericConfiguration` type ([88ab9a1](https://github.com/wuespace/telestion-core/commit/88ab9a1304f5001f229ad9bab811c6a4d18e0f18))
* **api:** Improve `JsonMessage` to decode and encode more data types synchronously and asynchronously ([77896c2](https://github.com/wuespace/telestion-core/commit/77896c243a7138702829bfbfe2dda64e42f64b8a))
* **api:** Improve `JsonMessage` to decode and encode more data types synchronously and asynchronously ([dc000b4](https://github.com/wuespace/telestion-core/commit/dc000b44c13ca511c0af986037085e05dbf4890a))
* **api:** Improve the method naming In the `TelestionVerticle`, `getGenericDefaultConfig()` is now called `getUntypedDefaultConfig()` ([b78e9a0](https://github.com/wuespace/telestion-core/commit/b78e9a05a4e041394eb683a2555dcfde8c4b1d33))
* **api:** Improve the method naming: In the `TelestionVerticle`, `getGenericConfig()` is now called `getUntypedConfig()` ([b78e9a0](https://github.com/wuespace/telestion-core/commit/b78e9a05a4e041394eb683a2555dcfde8c4b1d33))
* **api:** Introduce `NoConfiguration` to indicate that a verticle doesn't accept any configuration ([fc322ec](https://github.com/wuespace/telestion-core/commit/fc322ec39f47562e99a92a736c7ba94095f5cd59))
* **api:** Introduce `UntypedConfiguration` to indicate that a verticle doesn't have a strictly typed configuration ([9970457](https://github.com/wuespace/telestion-core/commit/99704571112930b18e2101d99a707c522a267638))
* **api:** Make `HeaderInformation` `MultiMap` complete and rename method `get` to `getString`. ([1a6a51c](https://github.com/wuespace/telestion-core/commit/1a6a51c029a7d719e99271ea10e3121dfb923dc2))
* **api:** Make warnings on add and set in `HeaderInformation` more specific ([a7c018d](https://github.com/wuespace/telestion-core/commit/a7c018d7c0a6295430a14d3e9e473a133d3da7aa))
* **api:** Move `Class&lt;T&gt; getConfigType()` in `TelestionVerticle` to `Class<T> VerticleConfigStrategy.getConfigType(Class<X extends TelestionVerticle<T>> clazz)` ([b78e9a0](https://github.com/wuespace/telestion-core/commit/b78e9a05a4e041394eb683a2555dcfde8c4b1d33))
* **api:** Move `TelestionVerticle` configuration logic into a dedicated strategy class and improve overall stability ([b78e9a0](https://github.com/wuespace/telestion-core/commit/b78e9a05a4e041394eb683a2555dcfde8c4b1d33))
* **api:** Move from `.json()` to `.toJsonObject()` in `WithEventBus` trait ([81c4d09](https://github.com/wuespace/telestion-core/commit/81c4d092229d8853f208f1f889b718791282b1fb))
* **api:** Move from `.json()` to `.toJsonObject()` in `WithEventBus` trait ([88b49dc](https://github.com/wuespace/telestion-core/commit/88b49dc1b45f0e9077b90e0ab17ee44507ca4ce2))
* **api:** Provide an additional flavor of the `JsonMessage` method, which maps a `JsonObject` to a `JsonMessage`. It returns a `Future` that can resolve with the value or reject. ([d36c9f8](https://github.com/wuespace/telestion-core/commit/d36c9f847db9c699e071928b1dbea43afa2fc0a8))
* **api:** Remove `Config` class ([9ea9374](https://github.com/wuespace/telestion-core/commit/9ea9374bb8536b133a6816258e1b7b3b850b4e9d))
* **api:** Remove `defaultStorageKey` method in `WithSharedData` interface ([b6c5e59](https://github.com/wuespace/telestion-core/commit/b6c5e59476e62253a56dc0fa599571a293642ebd))
* **api:** Remove `GenericConfiguration` class ([ba64cfb](https://github.com/wuespace/telestion-core/commit/ba64cfb8a1c2b661558d2ce8b5d6cefb93058a3d))
* **api:** Remove the second constructor from `TelestionVerticle` that allowed to skip loading loading the default config (`public TelestionVerticle(boolean skipDefaultConfigLoading)`) ([b78e9a0](https://github.com/wuespace/telestion-core/commit/b78e9a05a4e041394eb683a2555dcfde8c4b1d33))
* **api:** Remove verticle deployer ([c1f3a7d](https://github.com/wuespace/telestion-core/commit/c1f3a7db85937fbf24b51dfd9a278b32e61bf8a4))
* **api:** Rename `DecodedMessage` to `ResponseMessageWrapper` to be more specific ([1462dc1](https://github.com/wuespace/telestion-core/commit/1462dc100b5427c7ad8c02e155f165226761fcd7)), closes [#485](https://github.com/wuespace/telestion-core/issues/485)
* **api:** Rename `JsonMessage` interface to `JsonRecord` ([80cb961](https://github.com/wuespace/telestion-core/commit/80cb961e3229a58e2a3cb3cfe87cbe4ab7e3623b))
* **api:** Update `JsonMessage` unit test ([8f78d07](https://github.com/wuespace/telestion-core/commit/8f78d07094307e5617da90b1de6e4bc938fcabb5))
* **api:** Update `JsonMessage` unit test ([8cd53da](https://github.com/wuespace/telestion-core/commit/8cd53da4ce3b0e9979da0b5dbfb72729cb9161a7))
* **api:** Update log message for no default configuration in `TelestionVerticle` to pass the AWESA principle ([731572e](https://github.com/wuespace/telestion-core/commit/731572e3aa460c6cf38240ce4fd3d9dd6bff259f))
* **api:** Update request methods in `WithEventBus` trait ([ddb8bda](https://github.com/wuespace/telestion-core/commit/ddb8bda9041950e675e871ad789ed25ab497045c))
* **api:** Use `DecodedMessage` record instead of generic verticle ([2f029b0](https://github.com/wuespace/telestion-core/commit/2f029b0daba19f0ba239328bc050dab5f7a0633e))
* **application:** Remove empty `Application` class ([6885c58](https://github.com/wuespace/telestion-core/commit/6885c58b31567217f8498ac43fa0a73cf274de9e))
* **config:** Remove unneeded global `config.json` ([439aa8a](https://github.com/wuespace/telestion-core/commit/439aa8a336ca7248b56c1b007ae01deb466df94a))
* **example:** Add `GenericConfiguration` in examples which don't have any configuration ([702c0f3](https://github.com/wuespace/telestion-core/commit/702c0f389ac73ea57b0c36bdba4740a418608356))
* **example:** Add ping pong verticles ([c09f2a1](https://github.com/wuespace/telestion-core/commit/c09f2a11ac79db0e0f732571c9d36b90f9479576))
* **example:** Remove `getConfigType` overrides in example verticles because it is not necessary anymore ([e00799a](https://github.com/wuespace/telestion-core/commit/e00799a2d4c064b1edfd0eee56a5debffd9c76fc))
* **examples:** Add example for automatic default configuration loading in `TelestionVerticle` ([40f2d01](https://github.com/wuespace/telestion-core/commit/40f2d012806d98594c94d535f591b9cc4186b3c1))
* **examples:** Add examples which show the usage of the `HeaderInformation` API. ([636def2](https://github.com/wuespace/telestion-core/commit/636def23ebb530168164596a256308e43a867aca))
* **examples:** Add sample which shows the usage of the `WithTiming` trait ([2d28b89](https://github.com/wuespace/telestion-core/commit/2d28b89cb512ebbf73508dffa507ffa2c524eb50))
* **examples:** Add simple example in `TestVerticle` to show the usage of the default local map ([126e4cd](https://github.com/wuespace/telestion-core/commit/126e4cdd1171507069d671b9d7b1339daf332f84))
* **examples:** Update all verticles that use the deprecated `GenericConfiguration` type ([82ba9a9](https://github.com/wuespace/telestion-core/commit/82ba9a97c65eb5279d1fa817502eba68d5053295))
* **example:** Update verticle examples to use Telestion Verticle as base class ([557b82d](https://github.com/wuespace/telestion-core/commit/557b82d68ec4894ba8d367669424d5678975e2e2))
* Move `Configuration` and `VerticleConfig` data types to application module ([93bae37](https://github.com/wuespace/telestion-core/commit/93bae37626bdefab7ce5dc1d9b6e45dd8161ce22))
* Reduce log level in `add` and `set` methods ([6e48342](https://github.com/wuespace/telestion-core/commit/6e4834228984bda3666eea9d67c9d9cb7917dddd))
* **services:** Remove `ConfigurableApplication` ([a40cdb2](https://github.com/wuespace/telestion-core/commit/a40cdb21387191810443914c88bd46e47f0bf1fe))


### Bug Fixes

* **api:** Add ignore properties annotation to `GenericConfiguration` to ignore unknown properties and not throw instead ([0acd655](https://github.com/wuespace/telestion-core/commit/0acd655b984365e6c4c591c86a5f6ba03fdfc167))
* **api:** Fix `JsonMessage` asynchronous methods don't catch all possible thrown exceptions during decoding ([d40074e](https://github.com/wuespace/telestion-core/commit/d40074ea27cf84cede90087c78a7e8c36d3fae88))
* **api:** Fix `JsonMessage` asynchronous methods don't catch all possible thrown exceptions during decoding ([d754a88](https://github.com/wuespace/telestion-core/commit/d754a88a77a2b61d29cf0b85937d89dba4b1d100))
* **api:** Fix various `TelestionVerticle` issues ([83a730e](https://github.com/wuespace/telestion-core/commit/83a730e396cffb3ba10ed7640fee15808aa40f20))
* **deps:** bump logback-classic from 1.2.7 to 1.2.10 ([dcaa0dd](https://github.com/wuespace/telestion-core/commit/dcaa0dd04cc2dd76716bda97cac33136048b6979))
* **deps:** bump vertx-core from 4.2.1 to 4.2.3 to harden against CVE-2021-45105 ([99ba24b](https://github.com/wuespace/telestion-core/commit/99ba24b6c440b64fa61580c358792fc5c4fc4dc9))
* **deps:** bump vertx-rx-java2 from 4.2.1 to 4.2.3 to harden against CVE-2021-45105 ([fe81442](https://github.com/wuespace/telestion-core/commit/fe81442743bf3e642abd4deba3db02ae10568ebb))
* **deps:** Remove unused dependency ([e66bb84](https://github.com/wuespace/telestion-core/commit/e66bb843c95a967af55f942c68856bc5977dd040))
* Fix Gradle accidentally publishing the implicitly created "modules" module ([1cf9d41](https://github.com/wuespace/telestion-core/commit/1cf9d41af09f6771c0afaad72e7e22cc3babf28e))


### Miscellaneous Chores

* release 0.9.0 ([1d8be86](https://github.com/wuespace/telestion-core/commit/1d8be868f778b10757c6356840eae55ff1872434))

## [0.9.0](https://github.com/wuespace/telestion-core/compare/v0.8.1...v0.9.0) (2022-03-23)


### ⚠ BREAKING CHANGES

* **api:** The encoded messages don't longer contain the `"classname"` property that stores the classname of the encoded message.
* **api:** The encoded messages don't longer contain the `"classname"` property that stores the classname of the encoded message.

### Features

* **api:** Add compare methods to `HeaderInformation` ([f32de29](https://github.com/wuespace/telestion-core/commit/f32de29f21525aa2fa2b687b864823d8aa660ad7))
* **api:** Improve `JsonMessage` to decode and encode more data types synchronously and asynchronously ([77896c2](https://github.com/wuespace/telestion-core/commit/77896c243a7138702829bfbfe2dda64e42f64b8a))
* **api:** Improve `JsonMessage` to decode and encode more data types synchronously and asynchronously ([dc000b4](https://github.com/wuespace/telestion-core/commit/dc000b44c13ca511c0af986037085e05dbf4890a))
* **api:** Move from `.json()` to `.toJsonObject()` in `WithEventBus` trait ([81c4d09](https://github.com/wuespace/telestion-core/commit/81c4d092229d8853f208f1f889b718791282b1fb))
* **api:** Move from `.json()` to `.toJsonObject()` in `WithEventBus` trait ([88b49dc](https://github.com/wuespace/telestion-core/commit/88b49dc1b45f0e9077b90e0ab17ee44507ca4ce2))
* **api:** Update `JsonMessage` unit test ([8f78d07](https://github.com/wuespace/telestion-core/commit/8f78d07094307e5617da90b1de6e4bc938fcabb5))
* **api:** Update `JsonMessage` unit test ([8cd53da](https://github.com/wuespace/telestion-core/commit/8cd53da4ce3b0e9979da0b5dbfb72729cb9161a7))


### Bug Fixes

* **api:** Fix `JsonMessage` asynchronous methods don't catch all possible thrown exceptions during decoding ([d40074e](https://github.com/wuespace/telestion-core/commit/d40074ea27cf84cede90087c78a7e8c36d3fae88))
* **api:** Fix `JsonMessage` asynchronous methods don't catch all possible thrown exceptions during decoding ([d754a88](https://github.com/wuespace/telestion-core/commit/d754a88a77a2b61d29cf0b85937d89dba4b1d100))


### Miscellaneous Chores

* release 0.9.0 ([1d8be86](https://github.com/wuespace/telestion-core/commit/1d8be868f778b10757c6356840eae55ff1872434))

### [0.8.1](https://github.com/wuespace/telestion-core/compare/v0.8.0...v0.8.1) (2022-02-24)


### Bug Fixes

* Fix Gradle accidentally publishing the implicitly created "modules" module ([1cf9d41](https://github.com/wuespace/telestion-core/commit/1cf9d41af09f6771c0afaad72e7e22cc3babf28e))

## [0.8.0](https://github.com/wuespace/telestion-core/compare/v0.7.1...v0.8.0) (2022-02-24)


### Features

* **api:** Add a Vert.x `MultiMap` wrapper which provides better basic type support and some conversion and extraction features for a better coding experience with Vert.x headers. ([efc3886](https://github.com/wuespace/telestion-core/commit/efc38864db7f2a5d29227d9b3fbe2c212a410787))
* **api:** Add support for `HeaderInformation` in the `WithEventBus` verticle trait with many new overloaded methods for a better coding experience. ([9bc61d3](https://github.com/wuespace/telestion-core/commit/9bc61d32f450159ed9e5617749448702889c0fe1))
* **api:** Add utility methods for the Vert.x `MultiMap`. ([17c24a7](https://github.com/wuespace/telestion-core/commit/17c24a7e15ff52511114a22a27f7f5548a84bd4d))
* **api:** Add warnings to `HeaderInformation` add and set methods if there are already values assigned to the key. ([347bad1](https://github.com/wuespace/telestion-core/commit/347bad1bf15f154e52301fb3f86de19fc3e0ce9c))
* **api:** Deprecate `GenericConfiguration` type ([88ab9a1](https://github.com/wuespace/telestion-core/commit/88ab9a1304f5001f229ad9bab811c6a4d18e0f18))
* **api:** Introduce `NoConfiguration` to indicate that a verticle doesn't accept any configuration ([fc322ec](https://github.com/wuespace/telestion-core/commit/fc322ec39f47562e99a92a736c7ba94095f5cd59))
* **api:** Introduce `UntypedConfiguration` to indicate that a verticle doesn't have a strictly typed configuration ([9970457](https://github.com/wuespace/telestion-core/commit/99704571112930b18e2101d99a707c522a267638))
* **api:** Make warnings on add and set in `HeaderInformation` more specific ([a7c018d](https://github.com/wuespace/telestion-core/commit/a7c018d7c0a6295430a14d3e9e473a133d3da7aa))
* **api:** Provide an additional flavor of the `JsonMessage` method, which maps a `JsonObject` to a `JsonMessage`. It returns a `Future` that can resolve with the value or reject. ([d36c9f8](https://github.com/wuespace/telestion-core/commit/d36c9f847db9c699e071928b1dbea43afa2fc0a8))
* **examples:** Add examples which show the usage of the `HeaderInformation` API. ([636def2](https://github.com/wuespace/telestion-core/commit/636def23ebb530168164596a256308e43a867aca))
* **examples:** Update all verticles that use the deprecated `GenericConfiguration` type ([82ba9a9](https://github.com/wuespace/telestion-core/commit/82ba9a97c65eb5279d1fa817502eba68d5053295))
* Reduce log level in `add` and `set` methods ([6e48342](https://github.com/wuespace/telestion-core/commit/6e4834228984bda3666eea9d67c9d9cb7917dddd))


### Bug Fixes

* **api:** Add ignore properties annotation to `GenericConfiguration` to ignore unknown properties and not throw instead ([0acd655](https://github.com/wuespace/telestion-core/commit/0acd655b984365e6c4c591c86a5f6ba03fdfc167))

### [0.7.1](https://github.com/wuespace/telestion-core/compare/v0.7.0...v0.7.1) (2022-01-28)


### Bug Fixes

* **deps:** Remove unused dependency ([e66bb84](https://github.com/wuespace/telestion-core/commit/e66bb843c95a967af55f942c68856bc5977dd040))

## [0.7.0](https://github.com/wuespace/telestion-core/compare/v0.6.2...v0.7.0) (2022-01-22)


### Features

* **api:** Add a default local and remote map for every verticle in `WithSharedData` ([0ecaa9b](https://github.com/wuespace/telestion-core/commit/0ecaa9b9e83e982a99497c55a2b4d92046ba3220))
* **api:** Add automatic loading of the default configuration in `TelestionVerticle` ([a62e37f](https://github.com/wuespace/telestion-core/commit/a62e37f5786c62d152470c74ea3f449934a5ae3f))
* **api:** Add generic types to request and register methods in `WithEventBus` trait ([6a9d344](https://github.com/wuespace/telestion-core/commit/6a9d34475a2c284b8e7270ca25f61367dd3bf491))
* **api:** Add Verticle trait that simplifies the access to the timing functions of Vert.x ([c98c6c1](https://github.com/wuespace/telestion-core/commit/c98c6c1f0c405c250949cd69e6eaa3985dcd7aee))
* **api:** Update log message for no default configuration in `TelestionVerticle` to pass the AWESA principle ([731572e](https://github.com/wuespace/telestion-core/commit/731572e3aa460c6cf38240ce4fd3d9dd6bff259f))
* **api:** Update request methods in `WithEventBus` trait ([ddb8bda](https://github.com/wuespace/telestion-core/commit/ddb8bda9041950e675e871ad789ed25ab497045c))
* **examples:** Add example for automatic default configuration loading in `TelestionVerticle` ([40f2d01](https://github.com/wuespace/telestion-core/commit/40f2d012806d98594c94d535f591b9cc4186b3c1))
* **examples:** Add sample which shows the usage of the `WithTiming` trait ([2d28b89](https://github.com/wuespace/telestion-core/commit/2d28b89cb512ebbf73508dffa507ffa2c524eb50))
* **examples:** Add simple example in `TestVerticle` to show the usage of the default local map ([126e4cd](https://github.com/wuespace/telestion-core/commit/126e4cdd1171507069d671b9d7b1339daf332f84))

### [0.6.2](https://github.com/wuespace/telestion-core/compare/v0.6.1...v0.6.2) (2021-12-28)


### Bug Fixes

* **deps:** bump vertx-core from 4.2.1 to 4.2.3 to harden against CVE-2021-45105 ([99ba24b](https://github.com/wuespace/telestion-core/commit/99ba24b6c440b64fa61580c358792fc5c4fc4dc9))
* **deps:** bump vertx-rx-java2 from 4.2.1 to 4.2.3 to harden against CVE-2021-45105 ([fe81442](https://github.com/wuespace/telestion-core/commit/fe81442743bf3e642abd4deba3db02ae10568ebb))

### [0.6.1](https://github.com/wuespace/telestion-core/compare/v0.6.0...v0.6.1) (2021-12-23)


### Bug Fixes

* **deps:** bump logback-classic from 1.2.7 to 1.2.10 ([dcaa0dd](https://github.com/wuespace/telestion-core/commit/dcaa0dd04cc2dd76716bda97cac33136048b6979))

## [0.6.0](https://github.com/wuespace/telestion-core/compare/v0.5.0...v0.6.0) (2021-12-23)


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

## [0.5.0](https://github.com/wuespace/telestion-core/compare/v0.4.0...v0.5.0) (2021-08-01)


### Features

* **example:** Remove database save in RandomPositionPublisher ([89adfe8](https://github.com/wuespace/telestion-core/commit/89adfe8faeef5e5524567766e78e805018ebc249))
* **services:** Move MongoDB implementation to own extension https://github.com/wuespace/telestion-extension-mongodb ([a7fb49b](https://github.com/wuespace/telestion-core/commit/a7fb49b879cb224a72f7c5b7bdbe325280c2d213))

## [0.4.0](https://github.com/wuespace/telestion-core/compare/v0.3.0...v0.4.0) (2021-07-07)


### Features

* **api:** Add new configuration provider which enables default values ([a973cd1](https://github.com/wuespace/telestion-core/commit/a973cd1f0d30513bcfcae655db156138f74b145a))
* **connection:** Add configuration of baud rate to `SerialConn` verticle. Usages of the `de.wuespace.telestion.services.connection.rework.serial.SerialConn` now requires an additional parameter: `int baudRate`. To migrate, add this parameter to any application configuration using this verticle. The value used before is `9600`. Therefore, you can use `"baudRate": 9600` to match the old default configuration. ([ae4dad2](https://github.com/wuespace/telestion-core/commit/ae4dad2c9732047551ea74cca5b35b45bfd47f83))

## [0.3.0](https://github.com/wuespace/telestion-core/compare/v0.2.1...v0.3.0) (2021-05-08)


### Bug Fixes

* Remove bug that when TCP-Server stops, says the stopPromise is already completed ([6cdb4e3](https://github.com/wuespace/telestion-core/commit/6cdb4e30460ffe1dbe42f055d297ad5e1c9a0158))


### Features

* Delete legacy MAVLink-package ([1cd0196](https://github.com/wuespace/telestion-core/commit/1cd01968776b16325b0f825061c39f65d4b4caa2))
* Delete MAVLink Submodule ([b0b26e9](https://github.com/wuespace/telestion-core/commit/b0b26e9ff0e74f3ac5a4c348a6d72733d530a953))

### [0.2.1](https://github.com/wuespace/telestion-core/compare/v0.2.0...v0.2.1) (2021-05-05)


### Bug Fixes

* Load config for connection api from right location ([90c208d](https://github.com/wuespace/telestion-core/commit/90c208d03d19cc9772903d762c6475e7a533644c))

## [0.2.0](https://github.com/wuespace/telestion-core/compare/v0.1.2...v0.2.0) (2021-05-02)


### Bug Fixes

* Fix tests ([3a199ff](https://github.com/wuespace/telestion-core/commit/3a199ff586a5ed3f4602baebbf585618cba76c43))


### Features

* Deprecate old connection-api ([0613e7d](https://github.com/wuespace/telestion-core/commit/0613e7de4c0ae45c04cd77028c7cc902c586fde5))
* Implement new connection-api ([a90f222](https://github.com/wuespace/telestion-core/commit/a90f2221430a39c050b117f2a1b28b45e226f593))

### [0.1.2](https://github.com/wuespace/telestion-core/compare/v0.1.1...v0.1.2) (2021-04-28)


### Bug Fixes

* Remove web server from configuration ([ac8710e](https://github.com/wuespace/telestion-core/commit/ac8710e88a35060c356f34833cc17f294824db28))

### [0.1.1](https://github.com/wuespace/telestion-core/compare/v0.1.0...v0.1.1) (2021-04-28)


### Bug Fixes

* disable preview features ([#263](https://github.com/wuespace/telestion-core/issues/263)) ([d2271b1](https://github.com/wuespace/telestion-core/commit/d2271b1a14af48e224c6ac984c39d8d07e18ac07))

## [0.1.0](https://github.com/wuespace/telestion-core/compare/a50083983290ffe80825306bf13e5f932ac86c7f...v0.1.0) (2021-04-28)


### Bug Fixes

* Gradle package group ([eefb062](https://github.com/wuespace/telestion-core/commit/eefb0622531f9794b8efc94638e762be82e8cfd8))


### Features

* Add release action based on Conventional commits ([a500839](https://github.com/wuespace/telestion-core/commit/a50083983290ffe80825306bf13e5f932ac86c7f))
