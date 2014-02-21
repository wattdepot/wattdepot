# WattDepot 3.0.0-M9 Running on Mopsa Performance Evaluation

## MacBook Pro Laptop at University of Hawaii, Manoa
On February 12th 2014 I started WattDepot server version 3.0.0-M9 on Mopsa.ics.hawaii.edu with out any logging. I then started the performance tools on my laptop, a MacBook Pro with a 2.7 GHz Intel Core i7 processor and 16 GB 1600 MHz DDR3 memory. My laptop was connected to the UHM wireless network.

The first runs were just a single performance tool running alone.

| Performance Tool       | Average operations / second |
| ---------------------- | --------------------------- |
| Put Measurement        | 26.3 puts / second.         |
| Get Value (date)       | 35.6 gets / second.         |
| Get Value (date, date) | 22.4 gets / second.         |
| Get Earliest Value     | 30.5 gets / second.         |
| Get Latest Value       | 30.8 gets / second.         |

The second runs were with the Put Measurements running and a single Get tool running at the same time.

| Performance Tool       | Average operations / second |
| ---------------------- | --------------------------- |
| Get Value (date)       | 22.6 gets / second.         |
| Get Value (date, date) | 13.2 gets / second.         |
| Get Earliest Value     | 22.6 gets / second.         |
| Get Latest Value       | 21.4 gets / second.         |
| Put Measurement        | 24.7 puts / second.         |


## Windows 7 desktop at Kailua Hawaii.

The second set of tests were run from my Windows PC at home in Kailua. It is a Windows 7 Home Premium machine with a Intel Core i7-3770 3.4 GHz CPU with 16 GB Ram. My desktop is connected to RoadRunner cable Internet.

The test I ran were just single performance tools running alone.

| Performance Tool       | Average operations / second |
| ---------------------- | --------------------------- |
| Put Measurement        | 14.4 puts / second.         |
| Get Value (date)       | 15.2 gets / second.         |
| Get Value (date, date) | 13.6 gets / second.         |
| Get Earliest Value     | 14.2 gets / second.         |
| Get Latest Value       | 15.4 gets / second.         |


On February 16th 2014 I started collecting the power and energy data from Hale Aloha, 80 collector processes running at 15 second sample intervals. This results in approximately 20 measurement puts per second.

| Run | Date | Operation | Number of Measurements in DB |
| --- | ----- | --------- | ---------------------------- |
|  1  | 12/19/2014 | Get DateValue (45.9 / sec) | 1,600,000 |
|     |            | Get IntervalValue (36.7 / sec) |      |
|     |            | Get Earliest Value (47.4 / sec) | |
|     |            | Get Latest Value (1 / sec) | |
| --- | ---------- | -------------------------- | ------ |
|  2  | 12/20/2014 | Get DateValue (34.7 / sec) | 2,111,806 |
|     |            | Get IntervalValue (31.7 / sec) |      |
|     |            | Get Earliest Value (35.0 / sec) | |
|     |            | Get Latest Value (1 / sec) | |
