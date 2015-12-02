package net.rivergod.sec.seoulrnd.android.menu_seoulrnd;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.navercorp.volleyextensions.volleyer.exception.UnsupportedContentTypeException;
import com.navercorp.volleyextensions.volleyer.http.ContentType;
import com.navercorp.volleyextensions.volleyer.http.ContentTypes;
import com.navercorp.volleyextensions.volleyer.response.parser.NetworkResponseParser;
import com.navercorp.volleyextensions.volleyer.response.parser.StringNetworkResponseParser;
import com.navercorp.volleyextensions.volleyer.response.parser.TypedNetworkResponseParser;
import com.navercorp.volleyextensions.volleyer.util.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rivergod on 2015-12-02.
 */
public class CommunicatorIntegratedNetworkResponseParser implements NetworkResponseParser {

    private static final String CONTENT_TYPE_HEADER_KEY = "Content-type";
    private static final NetworkResponseParser STRING_NETWORK_RESPONSE_PARSER = new EucKrStringNetworkResponseParser();

    private final Map<ContentType, NetworkResponseParser> parsers = new HashMap<ContentType, NetworkResponseParser>();
    /**
     * Default constructor
     * @param parsers Actual parsers from builder
     */
    CommunicatorIntegratedNetworkResponseParser(Map<ContentType, NetworkResponseParser> parsers) {
        this.parsers.putAll(parsers);
    }
    /**
     * Choose a right parser among actual parsers and Parse the data of a response to T object.
     * @param response NetworkResponse instance which has content.
     * @param clazz Target class that content of a response will be parsed to.
     * 		(It does not parse if clazz is {@code Void}.)
     * @return Response which contains parsed T object or contains some error if it happened.
     */
    @Override
    public <T> Response<T> parseNetworkResponse(NetworkResponse response, Class<T> clazz) {
        Assert.notNull(response, "NetworkResponse");
        Assert.notNull(clazz, "Target class token");

        // Skip parsing if target class is {@code Void}
        if (clazz == Void.class) {
            return Response.success(null, null);
        }

        // Use StringNetworkResponseParser if target class is String
        if (clazz == String.class) {
            return STRING_NETWORK_RESPONSE_PARSER.parseNetworkResponse(response, clazz);
        }

        // Return the response without using any other parser if target class is NetworkResponse
        if (clazz == NetworkResponse.class) {
            @SuppressWarnings("unchecked")
            Response<T> successResponse = (Response<T>) Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
            return successResponse;
        }

        // Get a content type string from the response header
        String contentTypeString = getResponseHeader(response, CONTENT_TYPE_HEADER_KEY);
        // Throw an error if content type is null
        if (contentTypeString == null) {
            return Response.error(new ParseError(new UnsupportedContentTypeException("It cannot find any response parser, "
                    + "because the response content type is null.")));
        }

        // Create a content type instance
        ContentType contentType = ContentType.createContentType(contentTypeString);
        // Get a network response parser from parsers collection
        NetworkResponseParser responseParser = parsers.get(contentType);
        // Throw an error if it cannot find any response parser
        if (responseParser == null) {
            return Response.error(new ParseError(new UnsupportedContentTypeException("It cannot find any response parser "
                    + "for the response content type.")));
        }

        // Parse the response and return it
        return responseParser.parseNetworkResponse(response, clazz);
    }

    protected String getResponseHeader(NetworkResponse response, String headerKey) {
        return response.headers.get(headerKey);
    }

    /**
     * Builder class for IntegratedNetworkResponseParser
     * @author Wonjun Kim
     *
     */
    public static class Builder {
        private final Map<ContentType, NetworkResponseParser> parsers = new HashMap<ContentType, NetworkResponseParser>();
        /**
         * <pre>
         * Add a parser into IntegratedNetworkResponseParser.
         * NOTE : If some duplicate content type exists, newly added parser overwrites it.
         * </pre>
         */
        public Builder addParser(TypedNetworkResponseParser typedParser) {
            Assert.notNull(typedParser, "TypedNetworkResponseParser");

            ContentTypes contentTypes = typedParser.getContentTypes();
            addParserForContentTypes(typedParser, contentTypes);

            return this;
        }

        private void addParserForContentTypes(TypedNetworkResponseParser typedParser, ContentTypes contentTypes) {
            List<ContentType> contentTypeList = contentTypes.getListOfContentTypes();
            for(ContentType contentType : contentTypeList) {
                addParser(contentType, typedParser);
            }
        }
        /**
         * <pre>
         * Add a parser into IntegratedNetworkResponseParser.
         * NOTE : If some duplicate content type exists, newly added parser overwrites it.
         * </pre>
         */
        public Builder addParser(ContentType contentType, NetworkResponseParser parser) {
            Assert.notNull(contentType, "ContentType");
            Assert.notNull(parser, "NetworkResponseParser");

            parsers.put(contentType, parser);

            return this;
        }
        /**
         * Create a IntegratedNetworkResponseParser.
         */
        public CommunicatorIntegratedNetworkResponseParser build() {
            return new CommunicatorIntegratedNetworkResponseParser(parsers);
        }
    }
}
