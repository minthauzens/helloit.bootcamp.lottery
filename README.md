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

    

Security:
    Admin:
        username:
        password:
        
TASKS:
    Implement better lottery validation error responses
    /stats need to add participants count
    need to test for lottery participant limit, for participant saving (i dont think it is a validators job)
        because the code is valid, but lottery has reached its limit
            dont have test for that either
        or end_date has been set
    LOGGING
    
    implement lotteryEndDate that can be set in advance
        you can end registration now,
        or on some specific future date 

Future improvements:
    ParticipantValidator:
        Make participant validator (code validator) more efficient
            refactor it. maybe put participantRegisterDto as class parameter.
            now takes a lot of multiple queries to get the result needed.
        Improve empty else if in validate method()
        Add tests for consecutive validator examples
            valid comes after not valid
            false comes after valid
            false after false
            true after true
                // had design issue where ValidatorResponse was defined in constructor not in validate method

Known issues and limits:
    tests can fail if run at midnight, because the date could change between actions.
    cant participate more than Integer.MAX_VALUE participants in lotteries

Should learn:
    how to make entities with many to many many to one one to many relations