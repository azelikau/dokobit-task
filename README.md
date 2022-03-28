# Question answers

* ### If we add multiple archiving methods
  While creating a new archive method, all we need to do is to add new value to ArchiveType enum and
  implement ArchiveStrategy interface. The writeArchive method of this interface provides convenient
  input parameters: FileItemIterator, which could be used to iterate over the request files and read
  their contents easily using the InputStream; OutputStream, to write the result.

* ### Face a significant increase in request count
  Since an archive API uses StreamingResponseBody, archive will be written asynchronously, and so
  threads, serving the requests will not be blocked. Also, archive writing process being performed
  in a streaming fashion (and without any dependencies on network calls, db calls, creating files,
  etc.), therefore threads will not be idled. Furthermore, since this application is stateless one,
  it's instances could be easily started on demand and, providing a load balancer, we can greatly
  improve our performance. However, the weakness of this approach is networking issues, because in
  this case, threads writing archives will be blocked. This problem could be solved by using the
  reactive framework running on NIO runtime.

* ### Allow 1GB max file size
  Since the archive API implemented in a streaming fashion, the files are not stored anywhere.
  Therefore, the allowed file size could be easily increased.

# Getting Started

**_To run this app_**, execute following commands in project root:
`docker-compose up --build`

It will start 2 Docker containers (more info in [docker-compose](docker-compose.yml)) file:

* application
* postgres db