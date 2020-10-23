# Leaders and Followers

![Leader produce and consume](images/leader-produce-and-consume.png)

A Producer first makes a call to the cluster to get metadata that includes a list of the leader partitions on their respective brokers:

![Metadata](images/metadata.png)

Producer can send event to relevant target:

![Producer send to leader](images/producer-send-to-leader.png)

A consumer also (always) reads from leader partition.

The leader broker must also keep an **in sync replica** list which is shared with Zookeeper. This list will not include the replicas that have fallen behind in their copying of events:

![ISR](images/insync-replicas.png)

Example of when all followers are too far behind (default config of 10 seconds) and we effectively have an empty ISR list:

![Empty ISR](images/isr-empty.png)

Now if the leader crashes, and we failover, we will lose some events. To avoid this, we configure **commits** where the safest is to wait until all followers have acknowledged an event. Now if the leader goes down and we failover, any **uncommitted** events will be retried by the producer and so we don't even lose those:

![Acks All](images/acks-all.png)

Finally, we can set the **min.insync.replicas** to guard against the scenario of losing all followers:

![Min insync](images/min-insync.png)