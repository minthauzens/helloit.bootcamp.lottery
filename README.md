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

Future improvements:
    Make participant validator (code validator) more efficient
        refactor it. maybe put participantRegisterDto as class parameter.
        now takes a lot of multiple queries to get the result needed.

Known issues and limits:
    tests can fail if run at midnight, because the date could change between actions.
    cant participate more than Integer.MAX_VALUE participants in lotteries

Should learn:
    how to make entities with many to many many to one one to many relations