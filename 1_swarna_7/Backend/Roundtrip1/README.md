Events feature mapping and endpoints:

GET ALL events: http://coms-3090-016.class.las.iastate.edu:8080/api/events

GET Owner events: http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/{userId}

POST new event: http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/{userId}

PUT edit event:  http://coms-3090-016.class.las.iastate.edu:8080/api/events/{id}?userId={userId}

DEL event: http://coms-3090-016.class.las.iastate.edu:8080/api/events/{id}



Profile feature mapping and endpoints:

DEL MAPPING FOR PROFILE: /api/delete/profile?emailId=jane@example.com

As of now, I return a JSON String notifying you on the success OR failure


GET MAPPING FOR PROFILE: /api/profile/{id} where {id} is once again emailId

As of now, I return an exception on failure, and the profile upon the success (success->exists a profile associated with account)


PUT MAPPING FOR EDIT PROFILE: /api/edit/profile/{id}

WITH JSON BODY: {

"profileBio": "changed bio"

}

can only change bio at the moment. I return the profile upon success, give exception on failure.


POST MAPPING FOR CREATE: 

/api/create/profile/{id} where id is the emailId, the email associated for the account. Return the profile on success, give exception on failure.
