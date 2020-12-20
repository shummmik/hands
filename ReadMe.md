There is a problem when running in sbt run. stdin is not perceived normally.
So I added ~ before run. But the ~ causes sbt to re-run on every file save.You need to end this cycle with ctrl+c. And also lines are added to the end of the out file:

< [info] 1. Monitoring source files for hands_sbt/run...

< [info]    Press <enter> to interrupt or '?' for more options.

< [info] Received input event: CancelWatch.