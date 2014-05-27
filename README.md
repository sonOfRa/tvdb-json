TVDB-JSON
=====================================

Play app that takes a subset of TVDB XML output and wraps it in a REST-ful JSON API.

## Building
To build yourself, get Play 2.2.2, move application.conf.example to application.conf and replace your settings.

### TVDB settings
In order to use the API, you have to get an API key here: [TVDB Api Registration](http://thetvdb.com/?tab=apiregister).
Currently there is only one mirror, so that is predefined in the [application.conf.example](conf/application.conf.example)

