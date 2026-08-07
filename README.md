# Ad Blocker DNS-over-HTTPS (DoH) Server

## Overview
This project is a high-performance, custom-built DNS-over-HTTPS (DoH) server written in Java. It acts as an intermediary resolver that intercepts DNS queries from clients (like web browsers), checks them against a customizable blocklist (to filter out ads, trackers, or malicious sites), and resolves clean domains using an upstream DNS provider (Google DNS). 

The server is built with modern Java features, utilizing Virtual Threads for massive concurrency and `dnsjava` for strict adherence to the DNS wire-format protocol.

## Key Features
* **DNS-over-HTTPS (DoH) Support**: Fully implements RFC 8484, supporting both POST (binary raw packets) and GET (Base64Url encoded packets) requests.
* **Ad-Blocking / Sinkholing**: Intercepts requests for blacklisted domains and returns sinkhole IPs (`0.0.0.0` for IPv4 and `::` for IPv6).
* **Smart Caching Engine**: Thread-safe, in-memory caching mechanism that respects the original Time-To-Live (TTL) of DNS records, segregated by Query Type (A vs AAAA).
* **High Concurrency**: Uses Java Virtual Threads (Project Loom) via the Javalin framework, allowing the server to handle thousands of concurrent DNS requests with minimal RAM and CPU overhead.
* **Native IPv6 Support**: Bypasses local OS network limitations by directly querying upstream servers for AAAA records using the `dnsjava` library.
* **Asynchronous Logging**: Implements a producer-consumer logging system using a daemon thread and `LinkedBlockingQueue` to ensure file logging does not block DNS resolution.
* **Clean Architecture**: Strictly follows SOLID principles, separating networking, caching, filtering, and core business logic, making it 100% unit-testable.

## Technologies Used
* **Java 21+**: Utilizes Records, modernized Collections (`getFirst()`), and Virtual Threads.
* **Javalin**: A lightweight web framework used to expose the HTTPS/HTTP endpoints.
* **dnsjava**: The standard Java library for creating, parsing, and manipulating binary DNS messages.
* **dotenv-java**: For loading environment variables (used for the optional Database connection).
* **JUnit 5 & Mockito**: For comprehensive, isolated unit testing of all core components.
* **Maven**: Dependency management and build system.

## Architecture & Project Structure
The project is structured into highly decoupled packages:

```text
src/main/java/org/example/
├── cache/       # In-memory caching logic (DnsCache, CacheKey)
├── config/      # Database configuration and connection management
├── core/        # Core business logic (AdBlockResolver, DnsRecord)
├── filter/      # Domain filtering engine
├── logging/     # Asynchronous file logging for cache hits/misses
├── provider/    # Data sources for the blocklist (FileProvider, DbProvider)
├── server/      # The web layer (DohServer) handling HTTP request/response
└── upstream/    # Network client fetching records from Google DNS (8.8.8.8)
```

### Request Flow
1. **Server Layer**: `DohServer` receives an HTTP request on `/dns-query`. It extracts the binary DNS payload.
2. **Core Layer**: `AdBlockResolver` receives the domain name and query type (A or AAAA).
3. **Filter Layer**: The domain is checked against the loaded blocklist. If found, a sinkhole IP is returned immediately.
4. **Cache Layer**: If not blocked, the cache is queried. Expired records are automatically purged.
5. **Upstream Layer**: If a cache miss occurs, `UpstreamDnsClient` fetches the real IP from `8.8.8.8`, saves it to the cache, and returns it.
6. **Response**: The `DohServer` constructs a valid binary DNS response and sends it back to the client.

## Getting Started

### Prerequisites
* JDK 21 or newer (required for Virtual Threads).
* Maven installed on your system.

### Installation & Setup
1. Clone the repository.
2. Create the required directories and files in the root of the project (next to `pom.xml`):
   * Create a folder named `BlockedDomains` and inside it, a file named `blocked_domains.txt`. Add the domains you want to block (one per line).
   ```text
   ads.com
   tracker.example.com
   ```
   * Create a folder named `Logs`. The application will automatically create `logs.txt` inside it.
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```

### Running the Server
Execute the `Main` class. The server will start on port `8080`.
```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```
Console output should indicate that the server has started successfully.

## Usage & Testing

### 1. Local Testing via Terminal (GET Request)
You can simulate a browser's DoH request using `curl`. This command sends a Base64Url encoded query asking for the IPv4 (A) record of `google.com`.

```bash
curl -v -H "Accept: application/dns-message" "http://localhost:8080/dns-query?dns=AAABAAABAAAAAAAABmdvb2dsZQNjb20AAAEAAQ" --output dns_response.bin
```
If successful, the server processes the request, logs it, and saves the binary DNS response to `dns_response.bin`.

### 2. Real-World Testing via Web Browser
Modern browsers (Chrome, Firefox, Edge, Brave) require a valid **HTTPS** connection to use a custom DoH server. You can expose your local server securely using [Ngrok](https://ngrok.com/).

1. While your Java server is running, open a new terminal and run:
   ```bash
   ngrok http 8080
   ```
2. Copy the forwarding URL provided by Ngrok (e.g., `https://abcdef123.ngrok-free.app`).
3. Open your browser settings:
   * **Chrome/Brave/Edge**: Settings -> Privacy and security -> Security -> Use secure DNS.
   * Select **Custom** and paste your Ngrok URL appended with `/dns-query` (e.g., `https://abcdef123.ngrok-free.app/dns-query`).
4. Browse the web. Your terminal running the Java application will start logging the intercepted DNS requests. Try visiting a domain from your `blocked_domains.txt` to verify the sinkholing functionality.

## Testing
The project includes a robust suite of unit tests, utilizing Mockito to ensure components are tested in complete isolation (without actual network calls or file system I/O).

To run the tests:
```bash
mvn test
```
The test suite covers:
* HTTP Context mocking for the Javalin server.
* Cache TTL expiration and segregation logic.
* AdBlockResolver flow (Hit, Miss, Blocked).
* File reading edge cases (empty lines, invalid paths).
