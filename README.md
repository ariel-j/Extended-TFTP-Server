# Extended TFTP Server and Client

This project implements an extended Trivial File Transfer Protocol (TFTP) server. The TFTP server allows multiple users to upload and download files from the server and receive notifications when files are added or deleted. Communication between the server and clients is performed using a binary communication protocol, supporting the upload, download, and lookup of files.

## Features
- **Login**: Users can log in to the server by providing a username.
- **File Operations**: Users can perform various file operations including upload, download, and delete.
- **Directory Listing**: Users can request a listing of all files available on the server.
- **Broadcast Notifications**: The server broadcasts notifications to all logged-in clients when files are added or deleted.
- **Disconnect**: Users can gracefully disconnect from the server.

## Running the Project
To run the project, follow these steps:

1. **Clone the Repository**: Clone the project repository from GitHub.

```bash
git clone https://github.com/your-username/your-repository.git
```

2. **Build the Project**: Use Maven to build the project.

```bash
cd your-repository
mvn clean install
```

3. **Run the Server**: Execute the server JAR file with the desired port number.

```bash
java -jar server.jar <port_number>
```

4. **Run the Client**: Execute the client JAR file with the server's IP address and port number.

```bash
java -jar client.jar <server_ip> <server_port>
```
you can se your own clinet or a built client giving to us by the corse staff: 
https://github.com/bguspl/TFTP-rust-client 

## Usage
After running the client, you can interact with the TFTP server using the following commands:

- **LOGRQ**: Login to the server with a username.
  - Format: `LOGRQ <Username>`
  - Example: `LOGRQ JohnDoe`

- **DELRQ**: Delete a file from the server.
  - Format: `DELRQ <Filename>`
  - Example: `DELRQ example.txt`

- **RRQ**: Download a file from the server to the current directory.
  - Format: `RRQ <Filename>`
  - Example: `RRQ example.txt`

- **WRQ**: Upload a file from the current directory to the server.
  - Format: `WRQ <Filename>`
  - Example: `WRQ example.txt`

- **DIRQ**: List all filenames available on the server.
  - Format: `DIRQ`

- **DISC**: Disconnect from the server and exit the client.
  - Format: `DISC`

## Implementation Details
- The server and client are implemented in Java.
- Maven is used as the build tool for both the server and client.
- The server follows the Thread-Per-Client pattern.
- The project adheres to coding standards and guidelines provided in the assignment instructions.

For more details on the project implementation, refer to the assignment instructions and code documentation.

## Contributors
- Ariel Jayson
- Yuval Nachman
