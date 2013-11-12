/**
 * Copyright (C) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package filters;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.Context;
import ninja.Cookie;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is just a demo for a filter. This filter just logs a request in level
 * info. Be inspired and use your own filter.
 * 
 * Filters can be attached to classes or methods via @FilterWith(LoggerFilter.class)
 * 
 * @author ra
 * A typical configuration for the access log might look as follows.

LogFormat "%h %l %u %t \"%r\" %>s %b" common
CustomLog logs/access_log common
This defines the nickname common and associates it with a particular log format string. The format string consists of percent directives, each of which tell the server to log a particular piece of information. Literal characters may also be placed in the format string and will be copied directly into the log output. The quote character (") must be escaped by placing a back-slash before it to prevent it from being interpreted as the end of the format string. The format string may also contain the special control characters "\n" for new-line and "\t" for tab.

The CustomLog directive sets up a new log file using the defined nickname. The filename for the access log is relative to the ServerRoot unless it begins with a slash.

The above configuration will write log entries in a format known as the Common Log Format (CLF). This standard format can be produced by many different web servers and read by many log analysis programs. The log file entries produced in CLF will look something like this:

127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
Each part of this log entry is described below.

127.0.0.1 (%h)
This is the IP address of the client (remote host) which made the request to the server. If HostnameLookups is set to On, then the server will try to determine the hostname and log it in place of the IP address. However, this configuration is not recommended since it can significantly slow the server. Instead, it is best to use a log post-processor such as logresolve to determine the hostnames. The IP address reported here is not necessarily the address of the machine at which the user is sitting. If a proxy server exists between the user and the server, this address will be the address of the proxy, rather than the originating machine.
- (%l)
The "hyphen" in the output indicates that the requested piece of information is not available. In this case, the information that is not available is the RFC 1413 identity of the client determined by identd on the clients machine. This information is highly unreliable and should almost never be used except on tightly controlled internal networks. Apache httpd will not even attempt to determine this information unless IdentityCheck is set to On.
frank (%u)
This is the userid of the person requesting the document as determined by HTTP authentication. The same value is typically provided to CGI scripts in the REMOTE_USER environment variable. If the status code for the request (see below) is 401, then this value should not be trusted because the user is not yet authenticated. If the document is not password protected, this entry will be "-" just like the previous one.
[10/Oct/2000:13:55:36 -0700] (%t)
The time that the server finished processing the request. The format is:
[day/month/year:hour:minute:second zone]
day = 2*digit
month = 3*letter
year = 4*digit
hour = 2*digit
minute = 2*digit
second = 2*digit
zone = (`+' | `-') 4*digit
It is possible to have the time displayed in another format by specifying %{format}t in the log format string, where format is as in strftime(3) from the C standard library.
"GET /apache_pb.gif HTTP/1.0" (\"%r\")
The request line from the client is given in double quotes. The request line contains a great deal of useful information. First, the method used by the client is GET. Second, the client requested the resource /apache_pb.gif, and third, the client used the protocol HTTP/1.0. It is also possible to log one or more parts of the request line independently. For example, the format string "%m %U%q %H" will log the method, path, query-string, and protocol, resulting in exactly the same output as "%r".
200 (%>s)
This is the status code that the server sends back to the client. This information is very valuable, because it reveals whether the request resulted in a successful response (codes beginning in 2), a redirection (codes beginning in 3), an error caused by the client (codes beginning in 4), or an error in the server (codes beginning in 5). The full list of possible status codes can be found in the HTTP specification (RFC2616 section 10).
2326 (%b)
The last entry indicates the size of the object returned to the client, not including the response headers. If no content was returned to the client, this value will be "-". To log "0" for no content, use %B instead.
 */
public class LoggerFilter implements Filter {

    private final static Logger LOG = LoggerFactory.getLogger("HTTP");

    private final class Headers {
	private final Map<String, List<String>> values;

	public Headers(final Map<String, List<String>> values) {
	    this.values = values;
	}

	public String get(final String name) {
	    if ( values.containsKey(name) ) {
		return values.get(name).toString().replaceAll("\\[|\\]", "");
	    } else {
		return "-";
	    }
	}

    }

    @Override
    public Result filter(final FilterChain chain, final Context context) {
	final Headers headers = new Headers(context.getHeaders());
	final String date = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z").format(new Date());
	final Result result = chain.next(context);
	// "%h %l %u %t \"%r\" %>s %b"
	// 127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
	LOG.info("- {} - {} [{}] \"{} {} HTTP/1.1\" {} - \"{}\" \"-\"",
		headers.get("Host"), headers.get("Authentication"), date, context.getMethod(), context.getRequestPath(), result.getStatusCode(),
		headers.get("Referer"), headers.get("User-Agent"));
	for (final Entry<String, List<String>> header : context.getHeaders().entrySet()) {
	    LOG.debug("H  "+header.getKey()+" : "+header.getValue());
	}
	for (final Cookie cookie : context.getCookies()) {
	    LOG.debug("C  "+cookie.getName()+" : "+cookie.getValue());
	}
	for (final Entry<String, String[]> param : context.getParameters().entrySet()) {
	    LOG.debug("P  "+param.getKey()+" : "+Arrays.toString(param.getValue()));
	}
	return chain.next(context);
    }

}
