CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS admins (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  name VARCHAR(120) NOT NULL,
  role VARCHAR(30) NOT NULL DEFAULT 'admin',
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) UNIQUE,
  name VARCHAR(120),
  preferred_language VARCHAR(10) NOT NULL DEFAULT 'fa',
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS languages (
  code VARCHAR(10) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  direction VARCHAR(3) NOT NULL CHECK (direction IN ('ltr','rtl')),
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS words (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  language_code VARCHAR(10) NOT NULL REFERENCES languages(code),
  word TEXT NOT NULL,
  pronunciation TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(language_code, word)
);

CREATE TABLE IF NOT EXISTS meanings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  word_id UUID NOT NULL REFERENCES words(id) ON DELETE CASCADE,
  target_language_code VARCHAR(10) NOT NULL REFERENCES languages(code),
  meaning TEXT NOT NULL,
  UNIQUE(word_id, target_language_code, meaning)
);

CREATE TABLE IF NOT EXISTS advertisements (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(120) NOT NULL,
  description TEXT,
  image_url TEXT,
  target_url TEXT,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  start_date TIMESTAMPTZ,
  end_date TIMESTAMPTZ,
  created_by UUID REFERENCES admins(id) ON DELETE SET NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS ad_statistics (
  id BIGSERIAL PRIMARY KEY,
  advertisement_id UUID NOT NULL REFERENCES advertisements(id) ON DELETE CASCADE,
  event_type VARCHAR(20) NOT NULL CHECK (event_type IN ('impression','click')),
  user_id UUID REFERENCES users(id) ON DELETE SET NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO languages(code,name,direction) VALUES
('fa','فارسی','rtl'),
('en','English','ltr'),
('ar','العربية','rtl'),
('tr','Türkçe','ltr'),
('de','Deutsch','ltr'),
('fr','Français','ltr'),
('es','Español','ltr')
ON CONFLICT (code) DO UPDATE
SET name=EXCLUDED.name, direction=EXCLUDED.direction;

CREATE INDEX IF NOT EXISTS idx_words_language_word ON words(language_code, word);
CREATE INDEX IF NOT EXISTS idx_ads_active_dates ON advertisements(active, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_ad_stats_ad ON ad_statistics(advertisement_id);
