# Backend
Place the following `.env` file with your details in the root of the project:
```
DB_HOST=jdbc:mysql://example_host:3306
DB_USERNAME=example_username
DB_PASSWORD=example_password
DB_NAME=example_name
SHOW_SQL=true
CLIENT_HOST=http://localhost:YOUR_PORT_NUMBER
JWT_SECRET=example_jwt_secret
JWT_TESTING=example_jwt_testing
DEFAULT_USER_NAME=example_username
DEFAULT_USER_PASSWORD=example_password
TMDB_API_KEY=<tmdb_api_read_access_token>
PORT=8080
```

## Important note about running the backend in production
The port number of the backend should be **8082**. This is very important, and it will not work otherwise. The reason is that the way Nginx and Docker Compose are currently configured is, for a large part, still hardcoded. In the future, this should be changed to use environment variables instead.
