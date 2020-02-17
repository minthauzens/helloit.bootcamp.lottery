`# helloit.bootcamp.lottery
**Final project of HelloIT java bootcamp 2019/2020 - web server for lotteries**

Database:
postgreSQL - for production
    dbname:         lotteryDB
    username:       lotteryDBUser
    password:       mX560^UveyUd&#eH

postgreSQL - for testing
    dbname:         lotteryDBDev
    username:       lotteryDBUserDev
    password:       q4cqu&&AmbEV43q%

Difference between schema.sql and schema-base.sql are owners
    additional security for production data.

Security:
    Admin:
        username: lottery
        password: q1w2e3r4
        base64 (lottery:q1w2e3r4): bG90dGVyeTpxMXcyZTNyNA==
        
haven't tested gui side, so my code coverage has dropped

it is design choice not to show login option for public side of things. 
    
Known issues and limits:
    1. tests can fail if run at midnight, because the date could change between actions.
    2. cant participate more than Integer.MAX_VALUE participants in lotteries
    3. ResponseEntities only contain the first error message from bindingResults for RestControllers
    4. is application secure from multiple requests collisions?
        i dont think so, validator checks if lottery has properties with one query, then with a different query the request is saved. (also what was the name of it)
    5. I have used LocalDate in the application and the DB, not LocalDateTime
    
    
    ** When you edit this file, you can see how i structured the content, not how github represents it **
