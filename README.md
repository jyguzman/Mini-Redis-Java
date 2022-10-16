# Mini Redis

## Description
This is a small/toy version of Redis implemented in Java. Redis is a popular in-memory database
that's often used a caching mechanism. I undertook this project to learn more about the internals
of a popular technology, especially the Redis Serializer Protocol (RESP) that Redis employs for
client-server communication.

## Commands 
In addition to PING and ECHO, this toy Redis supports the following commands:

- SET: Set a given key to a given value.
- MSET: Set multiples keys to multiple values at once.
- HSET: Set a hash value to a key.
- GET: Retrieve the value associated to a given key.
- HGET: Retrieve a hash associated to a given key.
- DEL: Delete a certain key.
- FLUSHALL: Delete all keys.
- EXISTS: Check to see if a given key is in the database.

## Possible Improvements/Extensions
- (GitHub README) Provide video demonstration of program
- Implement Transactions, which allow a user to chain multiple commands
- More extensive testing; tests are minimal right now
- Implement event loop; Redis is single-threaded and employs an event loop, but this program is multi-threaded