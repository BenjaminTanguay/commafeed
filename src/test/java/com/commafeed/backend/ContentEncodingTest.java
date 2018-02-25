package com.commafeed.backend;

import static org.junit.Assert.*;

import org.junit.Test;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HttpContext;

public class ContentEncodingTest {

	private static final Set<String> ALLOWED_CONTENT_ENCODINGS = new HashSet<>(Arrays.asList("gzip", "x-gzip", "deflate", "identity"));
	private static final HttpResponse response = mock(CloseableHttpResponse.class, 
			 Mockito.RETURNS_DEEP_STUBS);
	
	@Test
	public void test() {
		
		/*Http.Context context = mock(Http.Context.class);
		Http.Context.current.set(context);*/
		
		/*Http.Context context = mock(Http.Context.class);
	    Http.Flash flash = mock(Http.Flash.class);
	    when(context.flash()).thenReturn(flash);
	    Http.Context.current.set(context);
		*/
		ContentEncodingInterceptor contentEncoding = mock(ContentEncodingInterceptor.class);
		verify(contentEncoding).process(response, context);
	}

}
