# B2B Shipping Charge Estimator - Run Scripts

# ─── BUILD (only needed after code changes) ────────────────────────────────────
$env:PATH += ";$env:USERPROFILE\maven\apache-maven-3.9.6\bin"
& "$env:USERPROFILE\maven\apache-maven-3.9.6\bin\mvn.cmd" package -DskipTests -f "pom.xml"
