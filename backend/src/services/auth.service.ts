import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { db } from '../config/database';
import { env } from '../config/env';

export async function loginAdmin(email: string, password: string) {
  const result = await db.query(
    `SELECT id, email, password_hash, name, role, active
     FROM admins WHERE email = $1 LIMIT 1`,
    [email.toLowerCase()]
  );

  const admin = result.rows[0];
  if (!admin || !admin.active) return null;

  const valid = await bcrypt.compare(password, admin.password_hash);
  if (!valid) return null;

  const token = jwt.sign(
    { id: admin.id, role: admin.role, type: 'admin' },
    env.JWT_SECRET,
    { expiresIn: '7d' }
  );

  return {
    token,
    admin: {
      id: admin.id,
      email: admin.email,
      name: admin.name,
      role: admin.role,
    },
  };
}

export async function createAdmin(email: string, password: string, name: string, role='admin') {
  const passwordHash = await bcrypt.hash(password, 12);

  const result = await db.query(
    `INSERT INTO admins(email,password_hash,name,role)
     VALUES($1,$2,$3,$4)
     RETURNING id,email,name,role,active,created_at`,
    [email.toLowerCase(), passwordHash, name, role]
  );

  return result.rows[0];
}
