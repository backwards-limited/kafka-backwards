# Partitions

## Partition Assignment upon Creating a Topic

![Partition assignment](images/partition-assignment.png)

1. Ordered list of brokers
2. Leader and Followers assignment

![Partition assignment](images/partition-assignment-2.png)

First distribute the Leaders in round-robin, starting with the first broker in the above list:

![Partition assignment](images/partition-assignment-3.png)

Then distribute the Followers in round-robin, starting with the second broker in the above list:

![Partition assignment](images/partition-assignment-4.png)

And finally the last Followers do as above, now starting from the third broker:

![Partition assignment](images/partition-assignment-5.png)