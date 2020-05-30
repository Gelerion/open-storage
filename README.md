# open-storage
Intelligently designed and easy to use API
  
Examples:  
```
// copy 
storage.copy().source(file).target(dir).execute();
// write
storage.writer(file).write("1")
// read
storage.reader(file).read()
```
No need to worry about anything - the library will handle all the hard job.  
StoragePath is fully immutable