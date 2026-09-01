CREATE TABLE IF NOT EXISTS words (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  language_code VARCHAR(10) NOT NULL REFERENCES languages(code),
  word TEXT NOT NULL,
  pronunciation TEXT,
  part_of_speech VARCHAR(40),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(language_code, word)
);

CREATE TABLE IF NOT EXISTS meanings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  word_id UUID NOT NULL REFERENCES words(id) ON DELETE CASCADE,
  target_language_code VARCHAR(10) NOT NULL REFERENCES languages(code),
  meaning TEXT NOT NULL,
  example TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE(word_id, target_language_code, meaning)
);

CREATE INDEX IF NOT EXISTS idx_words_language_word ON words(language_code, word);
CREATE INDEX IF NOT EXISTS idx_meanings_word ON meanings(word_id);
