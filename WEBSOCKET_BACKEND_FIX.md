# WebSocket Backend Fix Plan (Go/Golang)

The Android client is currently receiving a `404 Not Found` when connecting to `wss://.../ws`. This indicates the route is missing, misconfigured, or blocked by a reverse proxy.

## 1. Verify Route Registration (cmd/api/main.go)

Ensure the `/ws` route is actually registered in your main router setup. It often gets missed if routes are grouped under `/api/v1`.

```go
// cmd/api/main.go

func main() {
    // ... setup router (e.g., Gin, Chi, or standard mux) ...
    r := gin.Default() // or mux.NewRouter()

    //  WRONG: If you put it inside a group without matching the client URL
    // v1 := r.Group("/api/v1")
    // v1.GET("/ws", wsHandler) // This would make the URL /api/v1/ws

    //  CORRECT: The client connects to root /ws
    r.GET("/ws", wsHandler.HandleConnections) 
    
    // ...
}
```

## 2. Check WebSocket Handler Implementation

Ensure your handler allows the connection upgrade and reads the token from the **Query Parameter**.

```go
// internal/handlers/websocket_handler.go

var upgrader = websocket.Upgrader{
    ReadBufferSize:  1024,
    WriteBufferSize: 1024,
    // CRITICAL: Allow CORS for development/mobile access
    CheckOrigin: func(r *http.Request) bool {
        return true
    },
}

func (h *WebSocketHandler) HandleConnections(c *gin.Context) {
    // 1. Upgrade HTTP to WebSocket
    ws, err := upgrader.Upgrade(c.Writer, c.Request, nil)
    if err != nil {
        log.Println("Upgrade error:", err)
        return
    }
    defer ws.Close()

    // 2. Authentication (Token from Query Param)
    tokenString := c.Query("token") // Client sends ?token=eyJ...
    if tokenString == "" {
        // Fallback: Check headers if your client supports it, but Android impl uses query
        tokenString = c.Query("token") 
    }

    if tokenString == "" {
        ws.WriteMessage(websocket.CloseMessage, websocket.FormatCloseMessage(websocket.ClosePolicyViolation, "No token provided"))
        return
    }

    // 3. Validate Token
    claims, err := h.tokenService.ValidateToken(tokenString)
    if err != nil {
        ws.WriteMessage(websocket.CloseMessage, websocket.FormatCloseMessage(websocket.ClosePolicyViolation, "Invalid token"))
        return
    }

    // 4. Register Client
    h.hub.Register <- &Client{
        Conn:     ws,
        UserID:   claims.UserID,
        Username: claims.Username,
    }
    
    // ... Listen loop ...
}
```

## 3. Nginx / Reverse Proxy Configuration (If applicable)

If you are running the Go app behind Nginx (likely on Azure), Nginx **drops** the WebSocket Upgrade headers by default. You must explicitly forward them.

**Check your Nginx `location` block for `/ws`:**

```nginx
server {
    listen 443 ssl;
    server_name frostbyte-api.southeastasia.cloudapp.azure.com;

    # ... ssl config ...

    location /ws {
        proxy_pass http://localhost:8080; # Or your internal Go port
        proxy_http_version 1.1;
        
        # CRITICAL FOR WEBSOCKETS:
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "Upgrade";
        
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        
        # Increase timeout so connections don't drop every 60s
        proxy_read_timeout 3600s;
        proxy_send_timeout 3600s;
    }

    location / {
        proxy_pass http://localhost:8080;
        # ... standard headers ...
    }
}
```

## 4. Azure App Service (If applicable)

If you are using Azure App Service instead of a VM with Nginx:

1.  Go to **Configuration** -> **General settings**.
2.  Find **Web sockets**.
3.  Set it to **On**.

## 5. Summary Checklist

- [ ] Does `main.go` have `r.GET("/ws", ...)` registered at the root level?
- [ ] Does the WebSocket Upgrader have `CheckOrigin: return true`?
- [ ] Does the handler read `c.Query("token")`?
- [ ] (If Nginx) Are `Upgrade` and `Connection` headers set?
- [ ] (If Azure App Service) Is "Web sockets" enabled in the portal?
