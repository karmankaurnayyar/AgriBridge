# Maven Wrapper

This backend includes a Windows Maven wrapper (`mvnw.cmd`).

From the `backend` directory, run:

```powershell
.\mvnw.cmd test
```

No system-wide Maven installation is required. On first use, the wrapper downloads Apache Maven 3.9.16 to the user's local `.m2` wrapper directory and reuses it on subsequent runs.

Java/JDK is still required.
