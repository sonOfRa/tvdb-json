TVDB-JSON
=====================================

Play app that takes a subset of TVDB XML output and wraps it in a REST-ful JSON API.

## Building
To build yourself, get Play 2.2.2, move application.conf.example to application.conf and replace your settings.

### Cache configuration
By default, this app uses the play2-memcached plugin. In order to use the internal EHCache implementation instead,
simply set
````
ehcacheplugin=enabled
memcachedplugin=disabled
````
in your application.conf, and remove the [play.plugins](conf/play.plugins) file. Using memcached has the advantage that
if you redeploy the the application without restarting memcached, your cache entries are still present. When using EHCache,
the cache lives inside the application itself, and is lost on redeploy.

#### Memcached
When using memcached, be sure to configure it so that it doesn't listen on all interfaces, as this seems to be the default
setting. In [application.conf.example](conf/application.conf.example) there's an example of my memcached instance, which
listens only on a VPN connection.

### TVDB settings
In order to use the API, you have to get an API key here: [TVDB Api Registration](http://thetvdb.com/?tab=apiregister).
Currently there is only one mirror, so that is predefined in the [application.conf.example](conf/application.conf.example)

