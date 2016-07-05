[![Release](https://jitpack.io/v/maxxx/ActiveAndroid.svg)](https://jitpack.io/#maxxx/ActiveAndroid)

# ActiveAndroid

Original source you can found here https://github.com/pardom/ActiveAndroid

This is my fork, with my changes.

## Changes list

* Enabled UUID serialization/deserialization
* All arguments in "where" now serialized too if it possible
* AA_DB_RESET meta in AndroidManifest.xml for recreate database [more about](https://github.com/jlhonora/ActiveAndroid/commit/945a096fb28aca21cc8bf99e9f8f6930f8e82098)
* add Many class, that act as ArrayList of childs models
* merge pull requests, optimizations and fixes

## Download

Last version on releases page https://github.com/NickRimmer/ActiveAndroid/releases

Gradle (build.gradle in your module):
If you download JAR files
```groovy
dependencies {
    ...
    compile files('libs/ActiveAndroid.jar')
    ...
}
```
or gradle can do it self
```groovy
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    ...
    compile 'com.github.maxxx:activeAndroid:1.+'
    ...
}
```

## Documentation (for official version)

* [Getting started](http://github.com/pardom/ActiveAndroid/wiki/Getting-started)
* [Creating your database model](http://github.com/pardom/ActiveAndroid/wiki/Creating-your-database-model)
* [Saving to the database](http://github.com/pardom/ActiveAndroid/wiki/Saving-to-the-database)
* [Querying the database](http://github.com/pardom/ActiveAndroid/wiki/Querying-the-database)
* [Type serializers](http://github.com/pardom/ActiveAndroid/wiki/Type-serializers)
* [Using the content provider](http://github.com/pardom/ActiveAndroid/wiki/Using-the-content-provider)
* [Schema migrations](http://github.com/pardom/ActiveAndroid/wiki/Schema-migrations)
* [Pre-populated-databases](http://github.com/pardom/ActiveAndroid/wiki/Pre-populated-databases)
* [Running the Test Suite](https://github.com/pardom/ActiveAndroid/wiki/Running-the-Test-Suite)

## License

[Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

    Copyright (C) 2010 Michael Pardo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
