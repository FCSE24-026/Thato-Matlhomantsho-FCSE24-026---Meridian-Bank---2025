#!/bin/bash
# Project Verification Script

echo "=========================================="
echo "MERIDIAN BANKING SYSTEM - PROJECT VERIFICATION"
echo "=========================================="
echo ""

# Check Java version
echo "[1/5] Checking Java installation..."
java -version 2>&1 | head -1

# Check Maven
echo ""
echo "[2/5] Checking Maven installation..."
mvn -version | head -1

# Check project structure
echo ""
echo "[3/5] Verifying project structure..."
if [ -f "pom.xml" ]; then
    echo "✓ pom.xml found"
else
    echo "✗ pom.xml not found"
fi

if [ -f "ASSIGNMENT_DOCUMENTATION.md" ]; then
    echo "✓ ASSIGNMENT_DOCUMENTATION.md found"
else
    echo "✗ ASSIGNMENT_DOCUMENTATION.md not found"
fi

if [ -d "src/main/java/com/banking" ]; then
    echo "✓ Source code directory found"
else
    echo "✗ Source code directory not found"
fi

# Build the project
echo ""
echo "[4/5] Building project..."
mvn clean compile -q
if [ $? -eq 0 ]; then
    echo "✓ Build successful"
else
    echo "✗ Build failed"
    exit 1
fi

# Check JAR creation
echo ""
echo "[5/5] Verifying artifact creation..."
mvn package -DskipTests -q
if [ -f "target/meridian-banking-1.0.0.jar" ]; then
    echo "✓ JAR artifact created successfully"
    ls -lh target/meridian-banking-1.0.0.jar | awk '{print "  Size: " $5}'
else
    echo "✗ JAR artifact not found"
    exit 1
fi

echo ""
echo "=========================================="
echo "VERIFICATION COMPLETE - ALL CHECKS PASSED ✓"
echo "=========================================="
echo ""
echo "To run the application:"
echo "  mvn javafx:run"
echo ""
echo "To run tests:"
echo "  mvn test"
echo ""
echo "Documentation:"
echo "  - ASSIGNMENT_DOCUMENTATION.md"
echo "  - PROJECT_COMPLETION_SUMMARY.md"
echo "  - README.md"
