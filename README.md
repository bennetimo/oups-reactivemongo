# oups-reactivemongo

This repository contains a simple reproducible test case to trigger the 'Oups' error from reactivemongo, when
under heavy load. 

You just need Docker installed, then to run:

`docker-compose up --build`

This will start mongo in a docker container, and a simple script which hammers the mongo database doing upserts.

At some point (a few minutes) reactivemongo starts to complain:

`docker logs oupstest 2>&1 | grep "Oups"`

```
[reactivemongo-akka.actor.default-dispatcher-3] ERROR reactivemongo.core.actors.MongoDBSystem - [Supervisor-1/Connection-2] Oups. 7380 not found! complete message is Response(MessageHeader(126,5027,7380,1), Reply(8,0,0,1), ResponseInfo(4da58fbe))
```

The mongodb is seeded with a collection in the `docker-entrypoint.sh`. 

Observations:

1. With a fresh collection, the time to trigger an error is increased. The errors are triggered when there is
enough data in the collection such that the queries start taking a while to complete. You can see this easily as
mongo will start to log the slow running queries.
1. Looks like once the queries become slow, the check for isMaster eventually times out (60s)

### Related links/tickets

* https://github.com/ReactiveMongo/ReactiveMongo/issues/441
* https://groups.google.com/forum/#!topic/reactivemongo/eqjA34mdX7s
* https://groups.google.com/forum/#!topic/reactivemongo/nKIN7Cytguc
* https://github.com/ReactiveMongo/ReactiveMongo/issues/721
* https://groups.google.com/forum/#!searchin/reactivemongo/oups%7Csort:date/reactivemongo/FEJjwVivV2Y/FoYUHnX5CQAJ
* https://groups.google.com/forum/#!searchin/reactivemongo/oups%7Csort:date/reactivemongo/wsXlEDvZCGI/FZDKX78KBgAJ
* https://groups.google.com/forum/#!searchin/reactivemongo/oups%7Csort:date/reactivemongo/wMJorjJThrg/DMAKZRemCgAJ

Adapted from https://github.com/joymufeng/test-reactivemongo
