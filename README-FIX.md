# Lenvora V2 - Real CI Fix

This ZIP contains only the changed files needed for the current GitHub Actions failure.

Changes:
- Node.js 22
- `npm ci` replaced with `npm install`
- npm cache remains enabled
- Cache dependency path points to `package.json`, so the workflow does not require a lockfile
- No fake/incomplete `package-lock.json` is included
- Dictionary API is wired into the Express app
- Dictionary search uses the correct PostgreSQL parameters

Copy these files over the matching files in the repository and commit them.

Later, when you intentionally generate real lockfiles with `npm install` locally, you can switch back to `npm ci` and set cache-dependency-path to the real lockfile.
