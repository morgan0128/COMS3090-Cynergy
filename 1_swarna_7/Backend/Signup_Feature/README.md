ROUNDTRIP 1 - SIGN UP





NOTES:



@RequestMapping is set to /api. This will make it easier to change it to api/auth/signup and api/auth/login when this feature is merged with the other backend feature.







Available End points from POSTMAN: CRUDL



1. Create requests - POST request:

/signup - Takes username, password, email and phone number. A unique ID (GeneratedValue) will be assigned to each new user. This will make username and password validation easier for login.



Input format:



{

&nbsp; "userName": "Randy",

&nbsp; "password": "southpark",

&nbsp; "email": "randy@example.com",

&nbsp; "phoneNum": "9876543210"

}



2\. READ requests - GET request:

/users - View all users' signup information. (Can hash password in a future update)







FEATURE TO BE ADDED:


To SignupRepository: Create queries for findByEmail, findByUsername for easier login validation





