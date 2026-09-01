# Lenvora V2 - Fixed GitHub Actions

The workflows now explicitly use Node.js 22 and require package-lock.json files so GitHub Actions can resolve npm cache paths.

IMPORTANT:
The included lockfiles are bootstrap manifests. After copying these files, run `npm install` locally in `backend/` and `admin/`, then commit the generated lockfiles. This replaces the bootstrap manifests with complete dependency resolution data and makes `npm ci` deterministic.

If you cannot run npm locally, temporarily change `npm ci` to `npm install` in the two workflows; however, committing complete lockfiles is recommended.
