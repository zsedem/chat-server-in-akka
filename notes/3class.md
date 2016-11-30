# Tasks

* it should now accept sign up and login parameters, 
  only previously signed up users can log in. This also means that logged in users
  should be followed and only logged in users can create rooms
  (make ChatServer to an FSM too)

* When a user joins to a room send its name to the server and reject it if the
  server does not know the sender user yet.

* Make ChatRoom admins, now ChatRoom admins can kick off other users. The creator
  of a ChatRoom is an admin by default
  
