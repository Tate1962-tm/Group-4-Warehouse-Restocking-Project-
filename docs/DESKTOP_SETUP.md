# AWRS Desktop App — VS Code / Windows Guide

Use this guide to run the **desktop app** (popup window) — **no browser, no website**.

---

## Step 1 — Install

| Software | Link |
|----------|------|
| Java 17+ | https://adoptium.net/ |
| Maven | https://maven.apache.org/download.cgi |
| VS Code | https://code.visualstudio.com/ |

In VS Code, install: **Extension Pack for Java**

---

## Step 2 — Get the project

```powershell
cd C:\Users\taten\Desktop\CS4395
git clone https://github.com/Tate1962-tm/Group-4-Warehouse-Restocking-Project-.git awrs-web
cd awrs-web
git pull origin main
```

---

## Step 3 — Run in VS Code (easiest)

1. Open VS Code
2. **File → Open Folder** → select `awrs-web`
3. Press **F5** (or Run → Start Debugging)
4. Choose **"AWRS Desktop App"**
5. The login window pops up — no browser needed

Login: **admin** / **admin123**

---

## Step 4 — Or run one command in terminal

In VS Code terminal (**Terminal → New Terminal**):

```powershell
.\run-desktop.ps1
```

Or double-click **`run-desktop.bat`**

Or:

```powershell
mvn javafx:run
```

---

## What you should see

A desktop window titled **"AWRS — Automated Warehouse Restock System"** with:
- Login screen + logo
- After login: tabs for Dashboard, Items, Locations, Receive, Fulfill, Restock, Audit

---

## Desktop vs Web

| | Desktop app | Web app |
|--|-------------|---------|
| Command | `mvn javafx:run` | `mvn spring-boot:run` |
| Opens | Popup window | Browser |
| VS Code config | **AWRS Desktop App** | AWRS Web Server |
| Browser needed? | **No** | Yes |

---

## Troubleshooting

### "JavaFX runtime components are missing"

Run from terminal (not by clicking a .java file):

```powershell
mvn javafx:run
```

### Window doesn't appear

- Make sure you chose **AWRS Desktop App** (not AwrsApplication)
- Check terminal for errors
- Run: `mvn clean javafx:run`

### Still see Whitelabel browser page

You launched the **web server** by mistake. Use:
- **F5 → AWRS Desktop App**, or
- `.\run-desktop.ps1`

Do **not** use `run.bat` or `mvn spring-boot:run` if you want the desktop window.

---

## Demo logins

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Manager | manager | manager123 |
| Worker | worker | worker123 |
