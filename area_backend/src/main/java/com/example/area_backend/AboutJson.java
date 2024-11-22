package com.example.area_backend;
import java.time.Instant;
import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import com.example.area_backend.TableDb.HandleJson.HandleJsonRepo;
import com.example.area_backend.TableDb.HandleJson.HandleJsonTable;
import jakarta.servlet.http.HttpServletRequest;
@RestController
@RequestMapping("/about.json")
@Validated
public class AboutJson
{
    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };
    @Autowired
    private final HandleJsonRepo handleJsonRepository;
    @Autowired
    public AboutJson(HandleJsonRepo handleJsonRepository)
    {
        this.handleJsonRepository = handleJsonRepository;
    }
    public AboutJson()
    {
        this.handleJsonRepository = null;
    }
    @GetMapping("")
    public ResponseEntity<?> getAboutDotJson(HttpServletRequest request)
    {
        return (ResponseEntity.ok().body(this.constructJsonObject(request).toString()));
    }
    private JSONObject appendCreateService(HandleJsonTable json, JSONObject serverJson)
    {
        JSONObject actionReactionValues = new JSONObject();
        if (!serverJson.has("name")) {
            serverJson.put("name", json.getService().toString());
        }
        actionReactionValues.put("name", json.getTitle());
        actionReactionValues.put("description", json.getDescription());
        if (json.getType().equals("Action")) {
            serverJson.append("actions", actionReactionValues);
        } else {
            serverJson.append("reactions", actionReactionValues);
        }
        return (serverJson);
    }
    private JSONObject constructServer()
    {
        JSONObject serverJson = new JSONObject();
        JSONObject serviceJson = new JSONObject();
        serverJson.put("current_time", Instant.now().toEpochMilli());
        List<HandleJsonTable> allJson = this.handleJsonRepository.findAll();
        String serviceNow = allJson.get(0).getService().toString();
        for (HandleJsonTable json : allJson) {
            if (serviceNow.equals(json.getService().toString())) {
                serviceJson = this.appendCreateService(json, serviceJson);
            } else {
                serverJson.append("services", serviceJson);
                serviceNow = json.getService().toString();
                serviceJson = new JSONObject();
                serviceJson = this.appendCreateService(json, serviceJson);
            }
        }
        return (serverJson);
    }
    private Object constructJsonObject(HttpServletRequest request)
    {
        String clientId = this.getClientIPAdress(request);
        JSONObject finalJson = new JSONObject();
        JSONObject tmpJson = new JSONObject();
        tmpJson.put("host", clientId);
        finalJson.put("client", tmpJson);
        tmpJson = this.constructServer();
        finalJson.put("server", tmpJson);
        return (finalJson);
    }
    private String getClientIPAdress(HttpServletRequest request)
    {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return "0.0.0.0";
        }
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0];
                }
                return (ip);
            }
        }
        return (request.getRemoteAddr());
    }
}