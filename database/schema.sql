CREATE TABLE IF NOT EXISTS languages (
  code VARCHAR(10) PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  direction VARCHAR(3) NOT NULL CHECK (direction IN ('ltr','rtl')),
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS advertisements (
  id UUID PRIMARY KEY,
  title VARCHAR(120) NOT NULL,
  description TEXT,
  image_url TEXT,
  target_url TEXT,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  start_date TIMESTAMPTZ,
  end_date TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO languages(code,name,direction) VALUES
('fa','فارسی','rtl'),('en','English','ltr'),('ar','العربية','rtl'),
('tr','Türkçe','ltr'),('de','Deutsch','ltr'),('fr','Français','ltr'),
('es','Español','ltr')
ON CONFLICT (code) DO NOTHING;
