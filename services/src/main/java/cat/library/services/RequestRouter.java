package cat.library.services;

import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

// Interface for request routing
public interface RequestRouter {
    RawHttpResponse<?> execRequest(RawHttpRequest request);
}
