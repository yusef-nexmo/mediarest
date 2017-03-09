We have to RESTify the media service according to MS-54 and subtasks.
https://nexmoinc.atlassian.net/browse/MS-54
- MS-56: GET /files
- MS-58: GET /files/ID
- MS-60: GET /files/ID/info
- MS-59: DEL /files/ID
- MS-57: POST /files
- MS-61: POST /files/ID ... I've already diverged from this

MS-54 leaves a few things out, so we've embarked on MS-81 to define the REST API

This is a spec and interactive demo of the proposed API.
It can be run as follows:
	java -jar nexmo-mediarest-0.1.0-capsule.jar server mediarest.yml
A simple demo that maintains an in-memory cache of the uploaded media items.
The search op exposes all the proposed parameters, but for the purposes of this demo, it just unconditionally returns all the items.

The Swagger API spec can be obtained as follows:
	curl localhost:3031/swagger.yaml
OR it can be explored interactively with Swagger UI ...
- Download https://github.com/swagger-api/swagger-ui
- Within browser, open swagger-ui/dist/index.html on local filesystem
- Paste http://localhost:3031/swagger.json into location box at top and hit Explore

Since it's based on our standard REST library, this demo requires authentication and it should accept any valid JWT, or any API-key/secret credentials with the password "secret1".
Examples:
- Upload: curl -v -X POST --header 'Content-Type: multipart/form-data' -F "filedata=@/path/to/image.jpg" -F "mimetype=image/jpeg" http://localhost:3031/v3/media?api_key=user1\&api_secret=secret1
- Search: curl -v http://localhost:3031/v3/media?api_key=user1\&api_secret=secret1
etc

That should be enough to play around with and ensure the API hangs together and has no rough edges.
In particular, it seems to be an open question as to what the URI should be, so I've gone with /v3/media as a starting point.
