# oups-reactivemongo

This repository contains a simple reproducible test case to trigger the 'Oups' error from reactivemongo, when
under heavy load. 

You just need Docker & [Docker Compose](https://docs.docker.com/compose/install/) installed, then run:

`docker-compose up --build`

>  This will start mongo in a docker container, so mongo should not already be running on port 27017 on your machine.
 
The script will then start to hammer the mongo database doing upserts.

At some point (a few minutes max) reactivemongo starts to complain.

* For reactivemongo <= 0.16.3

    `docker logs oupstest 2>&1 | grep "Oups"`

    > Oups message [changed](https://github.com/ReactiveMongo/ReactiveMongo/commit/2cd9cf93d92081f39ff3b183d0640e0ae54f0678#diff-efc55a5a6bcd08ebb236b6bba2494dcbR926) after this release

    ```
    [reactivemongo-akka.actor.default-dispatcher-3] ERROR reactivemongo.core.actors.MongoDBSystem - [Supervisor-1/Connection-2] Oups. 7380 not found! complete message is Response(MessageHeader(126,5027,7380,1), Reply(8,0,0,1), ResponseInfo(4da58fbe))
    ```

* For reactivemongo > 0.16.3    
    `docker logs oupstest 2>&1 | grep "No primary node"`
    `docker logs oupstest 2>&1 | grep "hasn't answered in time"`

The mongodb is seeded with a collection in the `docker-entrypoint.sh`, to reduce the time it takes to see the error. 

Observations:

1. With a fresh collection, the time to trigger an error is increased. The errors are triggered when there is
enough data in the collection such that the queries start taking a while to complete. You can see this easily as
mongo will start to log the slow running queries.
1. Looks like once the queries become slow, the check for isMaster might be failing

### Related links/tickets

* https://github.com/ReactiveMongo/ReactiveMongo/issues/441
* https://groups.google.com/forum/#!topic/reactivemongo/eqjA34mdX7s
* https://groups.google.com/forum/#!topic/reactivemongo/nKIN7Cytguc
* https://github.com/ReactiveMongo/ReactiveMongo/issues/721
* https://groups.google.com/forum/#!searchin/reactivemongo/oups%7Csort:date/reactivemongo/FEJjwVivV2Y/FoYUHnX5CQAJ
* https://groups.google.com/forum/#!searchin/reactivemongo/oups%7Csort:date/reactivemongo/wsXlEDvZCGI/FZDKX78KBgAJ
* https://groups.google.com/forum/#!searchin/reactivemongo/oups%7Csort:date/reactivemongo/wMJorjJThrg/DMAKZRemCgAJ

Adapted from https://github.com/joymufeng/test-reactivemongo
