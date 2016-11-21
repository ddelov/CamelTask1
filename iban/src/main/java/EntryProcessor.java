import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.camel.OutHeaders;

import java.util.Map;

/**
 * Created by Delcho Delov on 21/11/16.
 */
public class EntryProcessor {
    public void markRequestAsOK(@Headers Map<?, ?> in, @Body String payload, @OutHeaders Map<String, Object> out) {
        out.put("code", 200);
        out.put("message", "OK");
    }
    public void markRequestAsFailed(@Headers Map<?, ?> in, @Body String payload, @OutHeaders Map<String, Object> out) {
        out.put("code", 500);
        out.put("message", "ERROR");
    }

}
