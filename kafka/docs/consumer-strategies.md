# Consumer Strategies

- Auto Commit On - Default

  enable.auto.commit = true
  and
  Synchronous processing of batches i.e. only **poll** after processing current batch (that was fetched).
  
  With auto commit, offsets will be committed automatically for you at a regular interval (by default **auto.commit.interval.ms = 5000**) everytime you call **poll()**.
  
  If you don't use synchronous processing, you will be in **at most once** behaviour because offsets will be committed before data is processed - **which can be dangerous**.
  
- Auto Commit Off

  enable.auto.commit = false
  and so
  Manually commit offsets.