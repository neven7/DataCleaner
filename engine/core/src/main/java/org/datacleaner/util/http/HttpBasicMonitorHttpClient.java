/**
 * DataCleaner (community edition)
 * Copyright (C) 2014 Neopost - Customer Information Management
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.datacleaner.util.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.metamodel.util.FileHelper;

/**
 * {@link MonitorHttpClient} for HTTP BASIC based security.
 */
public class HttpBasicMonitorHttpClient implements MonitorHttpClient {

    private final CloseableHttpClient _httpClient;
    private final HttpClientContext _context;

    public HttpBasicMonitorHttpClient(final CloseableHttpClient httpClient, final String hostname, final int port,
            final String username, final String password) {
        this(httpClient, hostname, port, username, password, false);
    }

    public HttpBasicMonitorHttpClient(final CloseableHttpClient httpClient, final String hostname, final int port,
            final String username, final String password, final boolean preemptiveAuth) {
        _httpClient = httpClient;

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        credentialsProvider.setCredentials(new AuthScope(hostname, port), credentials);

        _context = HttpClientContext.create();
        _context.setCredentialsProvider(credentialsProvider);
        if (preemptiveAuth) {
            final AuthCache authCache = new BasicAuthCache();
            final BasicScheme basicScheme = new BasicScheme();
            authCache.put(new HttpHost(hostname, port, "http"), basicScheme);
            authCache.put(new HttpHost(hostname, port, "https"), basicScheme);
            _context.setAuthCache(authCache);
        }
    }

    @Override
    public HttpResponse execute(final HttpUriRequest request) throws Exception {
        addSecurityHeaders(request);
        return _httpClient.execute(request, _context);
    }

    @Override
    public void close() {
        FileHelper.safeClose(_httpClient);
    }

    protected void addSecurityHeaders(final HttpUriRequest request) throws Exception {
        // DO NOTHING BY DEFAULT
    }


}
