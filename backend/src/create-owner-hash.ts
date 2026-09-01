import crypto from 'node:crypto';
const password = process.argv[2];
if (!password) { console.error('Usage: npm run owner:hash -- "YOUR_PASSWORD"'); process.exit(1); }
const iterations = 210000;
const salt = crypto.randomBytes(16).toString('hex');
const key = crypto.pbkdf2Sync(password, salt, iterations, 32, 'sha256').toString('hex');
console.log(`${iterations}:${salt}:${key}`);
