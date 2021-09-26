Additional considerations:

* add() & find() are synchronized to stop multiple threads from interacting with the map at the same time. However,
  this could potentially be improved upon by locking the map on the accessGroupCounts key associated with the given key.
  This would allow threads to find associations in the map concurrently, as long as they weren't going to interfere
  with the logic used to determine which key should be removed when add() is called.
  
* There's no explicit testing around the synchronization of add() and find(), as it's just using the most simple inbuilt
  version of synchronization and would really just be testing the 'synchronized' keyword. I'd expect that any further
  work to change the synchronization of the map would take this into account and provide tests based on those changes.