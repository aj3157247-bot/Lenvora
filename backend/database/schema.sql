CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS languages (
  code VARCHAR(10) PRIMARY KEY, name VARCHAR(100) NOT NULL, direction VARCHAR(3) NOT NULL CHECK (direction IN ('ltr','rtl')), enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS advertisements (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), title VARCHAR(120) NOT NULL, description TEXT, image_url TEXT, target_url TEXT, active BOOLEAN NOT NULL DEFAULT TRUE, start_date TIMESTAMPTZ, end_date TIMESTAMPTZ, created_by VARCHAR(120), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS ad_statistics (
  id BIGSERIAL PRIMARY KEY, advertisement_id UUID NOT NULL REFERENCES advertisements(id) ON DELETE CASCADE, event_type VARCHAR(20) NOT NULL CHECK (event_type IN ('impression','click')), user_id VARCHAR(120), created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_ads_active_window ON advertisements(active,start_date,end_date);
CREATE INDEX IF NOT EXISTS idx_ad_stats_ad ON ad_statistics(advertisement_id,event_type);

INSERT INTO languages(code,name,direction) VALUES
('fa','فارسی','rtl'),('en','English','ltr'),('ar','العربية','rtl'),('tr','Türkçe','ltr'),('de','Deutsch','ltr'),('fr','Français','ltr'),('es','Español','ltr')
ON CONFLICT (code) DO NOTHING;
