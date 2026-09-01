import { describe, expect, it } from 'vitest';
import request from 'supertest';
import { createApp } from '../src/app';

describe('health', () => {
  it('returns ok', async () => {
    const response = await request(createApp()).get('/api/v1/health');
    expect(response.status).toBe(200);
    expect(response.body.ok).toBe(true);
  });
});
