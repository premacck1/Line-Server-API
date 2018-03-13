# Line-Server
A system that is capable of serving lines out of a file to network clients.This service looks up the file and returns the content of the requested line number and also the HTTP status along with it (200 - If the data was successfully retrieved, 413 - If the requested line number > max lines in the file). The output format of this API is in JSON String.

## Input
To start, Server needs a input - text file path. File should be in following format:
* Each line is terminated with a newline ("\n").
* Any given line will fit into memory.
* The line is valid ASCII (e.g. not Unicode).

## Sample Output:
```
{
   "lineNumber":43885556,
   "lineText":"optometries"
}
```
## Routes
GET /lines/<line index>:
It returns an HTTP status of 200 and the text of the requested line or an HTTP 413
status if the requested line is beyond the end of the file.
  
## Follow these steps to execute the project:
- [x] Open terminal and cd to the root directory of the project.
- [x] Type & run "./build.sh" command to execute build.sh script. This will download all the dependencies, build the project,
      run all the unit tests and if they successfully pass, create a Jar file.
- [x] To run the project, simple type "./run.sh <Full path of Input File as Argument>". Server will start.
- [x] You can sent GET request to API using "http://localhost:4567/lines/<LineNumber>". You will receive a JSON in response.
- [x] You can also configure port number by sending it as second argument to "./run.sh". But this is optional. 


## How it works?
* First the Input file provided in argument is Preprocessed. This is how pre-processing works:
   * In preprocessing, goal is to store each line number with its byte offset from begining of the file.
   * So at any time, any line contents can be accessed just by looking up the byte offset for that line.
   * I have used mapDB with line numbers as "Key" and corresponding offset as "Value". Using mapDB a database 
     model can be generated and stored on disk. And can be easily loaded while running the server.
   * I have used "ConcurrentMap" instance of mapDB, so it can be accessed my multiple clients concurrently using the API.
   * Now, I also did the following additional optimization further reducing the size of the storage required by the db model 
     and also reducing the time required to load the db while running the server.
      * Instead of storing offset for each line, I divided the file lines in to chunks and stored the offsets for the start of each             chunks/blocks in mapDB. Chunk size can be configured in Constants.java file. So for a Chunk_size = 25, every 25th 
        line number is pre-processed and stored in the concurrent mapDB.
        
* Now after preprocessing is complete. Spark Server is started by default on port: 4567 and loads up the mapDB model.
* In addition to preprocessing, LRU Cache is also implemented to service the clients faster. LRU Cache stores the top n 
  recent queries in to memory and replies with a response almost instantaneously when about a record in cache.
* For every GET request, Server checks whether the record corresponding to particular line number is avilable in cache. If so,
  then Server replies with the record(JSON object with the corresponding line contents). 
* If not in cache, then it gets a RandomAccessFile Read Handle on file. Exact byte offset for a particular line can be 
  calculated in following way
  ``` 
  offset_inside_chunk = (lineNum < Constants.CHUNK_SIZE) ? (lineNum - 1L) : (lineNum % Constants.CHUNK_SIZE)
  offset = lineNum - offset_inside_chunk;
  
  ```
* offset_inside_chunk is the internal offset of the line within a particular chunk and offset is the offset of the chunk.
* Now using RandomAcessFile Read Handle, we read the file starting from offset to the first '\n' character encountered. 
  This data is converted in to string and then returned as JSON String in response. Now LRU Cache is also updated with 
  this record.
* RandomAccessFile is synchronized. So we can make multiple reads and serve multiple clients at the same time.
* Singleton Design pattern is followed with the mapDB. 

## Dependencies
* spark-core : 2.7.2
* mapdb : 3.0.5
* concurrentlinkedhashmap-lru : 1.4.2
* mochito-core : 2.15.0
* jackson-databind : 2.9.4

## Future Work
Currently, non-blocking requests using spark server API are capped by HTTP thread pool of Jetty. A github issue #549 with 
spark is open. In future if i have more time to implement this project, I would use a good server Framework to support non-blocking
Async operations better.


## NOTES
### a) How does your system work?
    Please refer to section "How it works?".
    
### b) How will your system perform with a 1 GB file? a 10 GB file? a 100 GB file?
    I have tested the System with 1 GB File. It takes 30-40 Seconds to preprocess a 1 GB File. Preprocessing is required only once Next time if we start the server again, It loads up the file instantaneously. For 1 GB file DB takes up 100MB of space. For 10 GB, it will take around 5-6 minutes to preprocess the file. For 100 GB File, it will take around 50-60 minutes to preprocess the file.    

### c) How will your system perform with 100 users? 10000 users? 1000000 users?
    System performance was found to be optimal during Stress Testing with 10000 users(See Screenshots in Performance folder). 
    Data from stress testing: 
    Average Duration: 4 ms
    Min Duration:     1 ms
    Max Duration:     155 ms
    Parallel Concurrent Users: 1501

### d) What documentation, websites, papers, etc did you consult in doing this assignment?
    mvnrepository.com, docs.oracle.com, static.javadoc.io, programcreek.com, stackoverflow, geeksforgeeks.com

### e) What third-party libraries or other tools does the system use? How did you choose each library or framework you used?
    I chose Spark Framework for creating the API Server. Spark is very fast & light weight. It is excellent for rapid prototyping. It is very easy to setup. One of the reasons i chose spark, - so that i can spend most of my time building the logic behind the API program.
    
### f) How long did you spend on this exercise? If you had unlimited more time to spend on this,   how would you spend it and how would you prioritize each item?
    I spent 2-3 days working on this project. Improving/Optimizing the project with each iteration. If i had unlimited time, i would work more on tuning the performance of this system with huge files and 100000 and more concurrent users. I would also work on polishing the documentation and writing more comprehensive unit tests.

### g) If you were to critique your code, what would you have to say about it?
    I didn't do profiling for this code in details, like: heap & thread usage. I just performed stress testing with tool "Restful Stress" to see how the system is behaving under different load. I believe I could do more initial pre-processing of the file by dividing them into smaller chunks and processing it.
