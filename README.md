# helloit.bootcamp.lottery
Final project of HelloIT java bootcamp 2019/2020 - web server for lotteries


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
        
TASKS:
    LOGGING 
    
Future improvements:
    ParticipantValidator:
        Make participant validator (code validator) more efficient
            refactor it. maybe put participantRegisterDto as class parameter.
            now takes a lot of multiple queries to get the result needed.
        Add tests for consecutive validator examples
            valid comes after not valid
            false comes after valid
            false after false
            true after true
                // had design issue where ValidatorResponse was defined in constructor not in validate method     
    LotteryRestControllerTest:
        tests for each error message in LotteryDto
    Improved ResponseEntities:
        use ObjectMapper instead of ResponseEntity< String>
    implement lotteryEndDate that can be set in advance
            you can end registration now,
            or on some specific future date

Known issues and limits:
    tests can fail if run at midnight, because the date could change between actions.
    cant participate more than Integer.MAX_VALUE participants in lotteries
    ResponseEntities only contain the first error message from bindingResults

Should learn:
    how to make entities with many to many many to one one to many relations
    
is application secure from multiple requests collisions
    i dont think so,
    validator checks if lottery has properties with one query, then with a different query the request is saved.

Questions:
    when should crudRepository save be used and when to write custom update query?
    what errors should i throw, if i have already validated for the possible cause, so if they use my methods in correct
        order, the error should never be thrown. how to design the project structure for it.
        
Be mindful of commas in JSONs!! hard to notice, but may cause bugs in your tests

Why 
    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    makes browser send basic authorization without asking and couldn't find where to delete session_id/cookies
    
    
    haven't tested gui side, so my code coverage has dropped
    not sure where should chooseWinner be located. made the choice so both Lottery Controllers would be responsible about it.
    it is design choice not to show login option for public side of things.
    completed in lottery name is a bit ambigious, because for lotteries with no participants, it is possible to end their registration,
        but impossible to complete them, completed means if winner has been chosen.
        this could be resolved by changing the name or setting it to true, when endDate is being set.