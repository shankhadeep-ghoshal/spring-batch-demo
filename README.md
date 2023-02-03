
# Spring Batch Demo
The program pulls data from two sources:
1. An API of [Jsonplaceholder](https://jsonplaceholder.typicode.com/users)
2. A CSV file which is located in the classpath resources. It was self-made.

## Prerequisites
1. Docker instance installed and running
2. Docker Compose V2 installed and added to system path variables
The code uses docker compose version 3.9 and Dockerfile v1.4 so make sure your installation can 
   handle these.

## How to Run
1. Clone the master branch
2. Navigate to the root directory
3. Execute the command `docker-compose up -d`
4. Open your favourite API testing tool (Postman, etc.)   
The application runs on port 8085.

## API details
`Context-Path`=> `/winner-weekly`
1. `/{date}` - **GET**  
`date` takes in a date string in the form of `yyyy-MM-dd` as a path parameter.  
**HTTP 200** if date format is correct. Correct data containing the name of the person is 
   returned. Response type is String text  
**HTTP 404** if date format is correct and no winner is found for the week for the given date. 
   Response doesn't contain a body.  
**HTTP 422** if date format is violated. Has a String body with a suitable message.
