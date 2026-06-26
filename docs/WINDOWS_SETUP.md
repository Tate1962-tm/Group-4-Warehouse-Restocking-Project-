# AWRS Web App — Windows Setup Guide

Follow these steps on your PC to run the **full web app** (login page, dashboard, logo, all features).

---

## What you need installed

| Software | Download | Verify in PowerShell |
|----------|----------|------------------------|
| **Java 17+** | [Adoptium JDK 17](https://adoptium.net/) | `java -version` |
| **Maven** | [Apache Maven](https://maven.apache.org/download.cgi) | `mvn -version` |
| **Git** (optional) | [git-scm.com](https://git-scm.com/) | `git --version` |
| **VS Code** | [code.visualstudio.com](https://code.visualstudio.com/) | — |

Install the **Extension Pack for Java** in VS Code.

---

## Option A — Get the full app from GitHub (recommended)

Your local folder `C:\Users\taten\Desktop\CS4395\awrs` only has **tests** and an old `pom.xml` (no Spring Boot).  
The **full web app** is on GitHub.

### 1. Open PowerShell and clone

```powershell
cd C:\Users\taten\Desktop\CS4395
git clone https://github.com/Tate1962-tm/Group-4-Warehouse-Restocking-Project-.git awrs-web
cd awrs-web
git checkout main
```

### 2. Confirm you have the web app files

You should see:

```
awrs-web/
├── pom.xml                          ← Spring Boot (NOT the old JUnit-only pom)
├── src/main/java/com/awrs/
│   └── AwrsApplication.java         ← THIS is the app entry point
├── src/main/resources/static/
│   ├── index.html                   ← Web UI
│   └── images/logo.png              ← Logo
└── src/test/java/...
```

### 3. Run the app

```powershell
mvn spring-boot:run
```

Wait until you see:

```
Started AwrsApplication
```

### 4. Open in browser

Go to: **http://localhost:8080**

### 5. Log in

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Manager | `manager` | `manager123` |
| Worker | `worker` | `worker123` |

---

## Option B — Run from VS Code

1. **File → Open Folder** → select `awrs-web` (the cloned folder)
2. Open `src/main/java/com/awrs/AwrsApplication.java`
3. Click **Run** above `main()` or press **F5**
4. Open **http://localhost:8080** in Chrome/Edge

---

## Run tests (optional)

```powershell
mvn clean test
```

All 27 tests should pass.

---

## Common problems on Windows

### `mvn: The term 'mvn' is not recognized`

Maven is not on your PATH. Either:
- Reinstall Maven and check **Add to PATH**, or
- Use full path, e.g. `C:\Program Files\Apache\maven\bin\mvn spring-boot:run`

### `java: command not found` or wrong Java version

Install JDK 17+. In VS Code: **Ctrl+Shift+P** → **Java: Configure Java Runtime** → select JDK 17.

### Port 8080 already in use

Stop the other app using port 8080, or change port in:

`src/main/resources/application.properties`

```properties
server.port=8081
```

Then open **http://localhost:8081**

### `cannot find symbol: class User` (100 errors)

You are still in the **old project** (tests only, no Spring Boot).  
Use **Option A** above — clone `awrs-web` from GitHub. Do **not** use the old `pom.xml` without Spring Boot.

### Browser shows nothing / connection refused

The server is not running yet. Wait for `Started AwrsApplication` in the terminal before opening the browser.

---

## Old project vs web app

| | Your old `awrs` folder | Full web app (`awrs-web`) |
|--|------------------------|---------------------------|
| `pom.xml` | JUnit + SQLite only | Spring Boot |
| `AwrsApplication.java` | ❌ No | ✅ Yes |
| Browser UI | ❌ No | ✅ Yes |
| Run command | `mvn test` | `mvn spring-boot:run` |
| Open | — | http://localhost:8080 |

---

## Quick checklist

```
☐ Java 17 installed          → java -version
☐ Maven installed            → mvn -version
☐ Cloned awrs-web from GitHub
☐ pom.xml has spring-boot-starter-parent
☐ AwrsApplication.java exists
☐ mvn spring-boot:run
☐ Browser → http://localhost:8080
☐ Login: admin / admin123
```

---

## Need help?

If something fails, send:
1. Output of `java -version` and `mvn -version`
2. Your folder tree (does `AwrsApplication.java` exist?)
3. The last 20 lines from `mvn spring-boot:run`
